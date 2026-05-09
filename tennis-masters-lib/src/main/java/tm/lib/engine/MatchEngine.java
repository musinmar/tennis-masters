package tm.lib.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.domain.core.Match;

import java.util.Random;

import static tm.lib.engine.Side.AWAY;
import static tm.lib.engine.Side.HOME;

@Getter
@Setter
public class MatchEngine {

    public static final int TIME_STEP_MILLIS = 20;
    private static final double TIME_STEP = TIME_STEP_MILLIS / 1000.0;
    private static final double TIME_SCALE = 1;
    private static final double SCALED_TIME_STEP = TIME_STEP * TIME_SCALE;

    /**
     * Percentage of momentum a ball will keep after hitting net
     */
    private static final double NET_PONG_FACTOR = 0.5;
    private static final double AIR_FRICTION_COEFFICIENT = 0.0015;

    private static final double ENERGY_LOSS_PER_DISTANCE = 2 / Pitch.WIDTH;
    private static final double ENERGY_LOSS_PER_HIT = 0.3;
    private static final double ENERGY_LOSS_PER_SAVE = 0.6;
    private static final double ENERGY_REGAIN_PER_GAME = 1;
    private static final double ENERGY_REGAIN_PER_SET = 3;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private final StatsCalculator statsCalculator;
    private final Pitch pitch;
    private final StrategyHolder strategyHolder;
    private Side winningSide;

    public MatchEngine(Match match, StrategyHolder strategyHolder) {
        statsCalculator = new StatsCalculator(match.getVenue());
        pitch = new Pitch(match.getHomePlayer(), match.getAwayPlayer(), statsCalculator);
        this.strategyHolder = strategyHolder;

        pitch.reset(HOME);
    }

    public boolean isFinished() {
        return winningSide != null;
    }

    public void reset(Side servingSide) {
        getPitch().reset(servingSide);
        winningSide = null;
    }

    public void next() {
        if (isFinished()) {
            throw new IllegalStateException("Can't continue already finished game");
        }
        performPlayerAction(getPitch().getPlayer(HOME));
        performPlayerAction(getPitch().getPlayer(AWAY));
        performBallAction(getPitch().getBall());
    }

    private static double getScaledTimeStep() {
        return SCALED_TIME_STEP;
    }

    private void decreasePlayerEnergy(Player player, double value) {
        double energyDecreaseModifier = getStatsCalculator().getEnergyDecreaseModifier(player);
        value = value * energyDecreaseModifier;
        player.getState().changeEnergy(-value);
    }

    private boolean isTargetHighEnough(Player player, Vector2D target) {
        double netZoneLength = getPitch().calculateNetBlockedZoneLength(player);
        double modifier = player.getSide().getModifier();
        if (target.getY() * modifier > 0) {
            return true;
        }
        return Math.abs(target.getY()) > netZoneLength;
    }

    private Vector2D applyShotDistanceModification(Player player, Vector2D target) {
        double distance = target.distance(player.getState().getPosition());
        double optimalShotDistance = getStatsCalculator().getActualShotRange(player);
        if (distance > optimalShotDistance) {
            double excess = distance - optimalShotDistance;
            double r = Math.pow(RANDOM.nextDouble(), 2);
            Vector2D d = target.subtract(player.getState().getPosition());
            d = d.scalarMultiply((optimalShotDistance + excess * r) / (optimalShotDistance + excess));
            target = d.add(player.getState().getPosition());
        }
        return target;
    }

    private Vector2D calculateRandomDeviation(double range) {
        double phi = RANDOM.nextDouble() * 2 * Math.PI;
        double r = RANDOM.nextDouble() * range;
        return VectorUtils.rotate(new Vector2D(r, 0), phi);
    }

    private Vector2D applyShotAccuracyModification(Player player, Vector2D target) {
        double targetRange = getStatsCalculator().getActualTargetRange(player);
        Vector2D deviation = calculateRandomDeviation(targetRange);
        return target.add(deviation);
    }

    private Vector2D calculateVisibleBallTarget(Player player, Vector2D target) {
        double visibleTargetRange = getStatsCalculator().getActualVisibleTargetRange(player);
        Vector2D deviation = calculateRandomDeviation(visibleTargetRange);
        return target.add(deviation);
    }

    private void applyBallTarget(Player player, BallState ball, Vector2D target) {
        target = applyShotDistanceModification(player, target);
        target = applyShotAccuracyModification(player, target);
        ball.setRealTarget(target);
        ball.setVisibleTarget(calculateVisibleBallTarget(player, target));
        ball.setSpeed(getStatsCalculator().getActualBallSpeed(player));
        ball.setFlyingAboveNet(true);
    }

    private void hitBall(Player player, BallState ball, Vector2D ballTarget) {
        applyBallTarget(player, ball, ballTarget);
        if (!isTargetHighEnough(player, ball.getRealTarget())) {
            ball.setFlyingAboveNet(false);
        }

        pitch.setLastTouchedBall(player.getSide());
        decreasePlayerEnergy(player, ENERGY_LOSS_PER_HIT);
    }

    private void movePlayerToTarget(Player player, Vector2D target) {
        if (Math.signum(target.getY()) != Math.signum(player.getSide().getModifier())) {
            target = new Vector2D(target.getX(), 0);
        }

        double speed = getStatsCalculator().getActualPlayerSpeed(player);
        double acc = getStatsCalculator().getActualPlayerAcceleration(player);
        double step = speed * getScaledTimeStep();
        double ac_step = acc * getScaledTimeStep();

        var playerState = player.getState();
        Vector2D v = playerState.getDirection().scalarMultiply(playerState.getSpeed() * getScaledTimeStep());
        Vector2D d = target.subtract(playerState.getPosition());
        double dd = d.getNorm();

        if (dd > step) {//PLAYER_SPEED) {
            d = d.scalarMultiply(1 / dd).scalarMultiply(step);
        }
        Vector2D dv = d.subtract(v);
        double dv_len = dv.getNorm();

        if (dv_len > ac_step) {//PLAYER_ACCELERATION) {
            dv = dv.scalarMultiply(1 / dv_len).scalarMultiply(ac_step);
        }

        v = v.add(dv);
        playerState.setSpeed(v.getNorm() / getScaledTimeStep());
        if (playerState.getSpeed() != 0) {
            playerState.setDirection(v.scalarMultiply(1 / (playerState.getSpeed() * getScaledTimeStep())));
        }

        Vector2D move = playerState.getDirection().scalarMultiply(playerState.getSpeed() * getScaledTimeStep());
        playerState.setPosition(playerState.getPosition().add(move));
        decreasePlayerEnergy(player, move.getNorm() / getStatsCalculator().getVenueSpeedModifier() * ENERGY_LOSS_PER_DISTANCE);
    }

    private void performLyingAction(Player player) {
        var playerState = player.getState();
        playerState.addLyingTime(getScaledTimeStep());
        if (playerState.getLyingTime() >= getStatsCalculator().getTotalLyingTime(player)) {
            playerState.setLying(false);
        }
    }

    private void doSave(Player player, BallState ball, Vector2D ballTarget) {
        player.getState().lieDown();
        hitBall(player, ball, ballTarget);
        decreasePlayerEnergy(player, ENERGY_LOSS_PER_SAVE);
    }

    private void performPlayerAction(Player player) {
        BallState ball = getPitch().getBall();
        if (player.getState().isLying()) {
            performLyingAction(player);
            return;
        }

        Decision decision = getPlayerDecision(player);

        if (decision.isHitBall()) {
            if (getPitch().canPlayerHitBall(player)) {
                hitBall(player, ball, decision.getBallTargetPosition());
            } else if (getPitch().canPlayerSaveBall(player, ball.getPosition())) {
                doSave(player, ball, decision.getBallTargetPosition());
            } else {
                // Hit is impossible
            }
        } else {
            movePlayerToTarget(player, decision.getMoveToPosition());
        }
    }

    private Decision getPlayerDecision(Player player) {
        Strategy strategy = strategyHolder.getStrategy(player.getSide());
        if (player.getSide() == HOME) {
            return strategy.makeDecision(getPitch(), getScaledTimeStep());
        } else {
            Pitch mirroredPitch = getPitch().createMirrored();
            Decision decision = strategy.makeDecision(mirroredPitch, getScaledTimeStep());
            return decision.createMirrored();
        }
    }

    private void ballHitsNet(BallState ball) {
        Vector2D d = ball.getRealTarget().subtract(ball.getPosition());
        d = VectorUtils.mirror(d);
        d = d.scalarMultiply(NET_PONG_FACTOR);
        Vector2D newTarget = ball.getPosition().add(d);
        ball.setRealTarget(newTarget);
        ball.setVisibleTarget(newTarget);
    }

    private Vector2D getNextBallPosition(BallState ball) {
        Vector2D d = ball.getVisibleTarget().subtract(ball.getPosition());
        double distToVisibleTarget = d.getNorm();
        double distToRealTarget = ball.getRealTarget().distance(ball.getPosition());
        double step = getScaledTimeStep() * ball.getSpeed();
        double modifiedStep = step * distToVisibleTarget / distToRealTarget;
        if (distToVisibleTarget > modifiedStep) {
            d = d.scalarMultiply(1 / distToVisibleTarget).scalarMultiply(modifiedStep);
        }
        return ball.getPosition().add(d);
    }

    private void moveBall(BallState ball) {
        double distToRealTarget = ball.getRealTarget().distance(ball.getPosition());
        double step = getScaledTimeStep() * ball.getSpeed();
        decreaseBallSpeed(ball);
        Vector2D nextBallPosition = getNextBallPosition(ball);

        if (!ball.isFlyingAboveNet()) {
            if (Math.signum(ball.getPosition().getY()) * Math.signum(nextBallPosition.getY()) <= 0) {
                ballHitsNet(ball);
                return;
            }
        }

        ball.setPosition(nextBallPosition);

        int steps = (int) (distToRealTarget / step);
        if (steps == 0) {
            ball.setVisibleTarget(ball.getRealTarget());
        } else {
            Vector2D visibleTargetD = ball.getRealTarget().subtract(ball.getVisibleTarget()).scalarMultiply(1 / (double) steps);
            ball.setVisibleTarget(ball.getVisibleTarget().add(visibleTargetD));
        }
    }

    private void decreaseBallSpeed(BallState ball) {
        double speed = ball.getSpeed();
        double frictionForce = speed * speed * AIR_FRICTION_COEFFICIENT * getScaledTimeStep();
        ball.setSpeed(speed - frictionForce);
    }

    private void endGame(Side sideHit) {
        if (sideHit == null) {
            winningSide = pitch.getLastTouchedBall().getOpposite();
        } else {
            winningSide = sideHit.getOpposite();
        }
    }

    private void performBallAction(BallState ball) {
        if (ball.hasTouchedGround()) {
            endGame(getPitch().getBallSide(ball));
        } else {
            moveBall(ball);
        }
    }

    public void performEndOfGameActions() {
        getPitch().getPlayer(HOME).getState().changeEnergy(ENERGY_REGAIN_PER_GAME);
        getPitch().getPlayer(AWAY).getState().changeEnergy(ENERGY_REGAIN_PER_GAME);
    }

    public void performEndOfSetActions() {
        getPitch().getPlayer(HOME).getState().changeEnergy(ENERGY_REGAIN_PER_SET);
        getPitch().getPlayer(AWAY).getState().changeEnergy(ENERGY_REGAIN_PER_SET);
    }
}

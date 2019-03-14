package tm.lib.engine;

import java.util.Random;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Precision;
import tm.lib.domain.competition.Match;

public class MatchEngine {

    public static final double TIME_STEP = 0.02;
    private static final double TIME_SCALE = 0.75;
    
    /**
     * Percentage of momentum a ball will keep after hitting net
     */
    private static final double NET_PONG_FACTOR = 0.5;
    
    private static final double ENERGY_LOSS_PER_DISTANCE = 0.9 / Pitch.WIDTH;
    private static final double ENERGY_LOSS_PER_HIT = 0.3;
    private static final double ENERGY_LOSS_PER_SAVE = 0.3;
    private static final double ENERGY_REGAIN_PER_GAME = 1;
    private static final double ENERGY_REGAIN_PER_SET = 3;
    
    private static final Random RANDOM = new Random(System.currentTimeMillis());
        
    private final Pitch pitch;
    private final StatsCalculator statsCalculator;
    private Side winningSide;
    private Player lastHittedPlayer;

    public MatchEngine(Match match) {
        pitch = new Pitch(match.getFirstPlayer(), match.getSecondPlayer(), match.getVenue());
        pitch.setInitialPositions(Side.HOME);
        statsCalculator = new StatsCalculator(match.getVenue());
        lastHittedPlayer = null;
    }

    public void reset(Side startingSide) {
        getPitch().setInitialPositions(startingSide);
        winningSide = null;
    }

    public void next() {
        performPlayerAction(getPitch().getPlayer(Side.HOME));
        performPlayerAction(getPitch().getPlayer(Side.AWAY));
        performBallAction(getPitch().getBall());
    }

    public Pitch getPitch() {
        return pitch;
    }

    public Side getWinningSide() {
        return winningSide;
    }

    private StatsCalculator getStatsCalculator() {
        return statsCalculator;
    }

    private double getScaledTimeStep() {
        return TIME_STEP * TIME_SCALE;
    }
    
    private void decreasePlayerEnergy(Player player, double value) {
        double energyDecreaseModifier = getStatsCalculator().getEnergyDecreaseModifier(player);
        value = value * energyDecreaseModifier;
        player.changeEnergy(-value);
    }
    
    private boolean isTargetSmartEnough(Player player, Vector2D target) {
        Player opposite = getPitch().getOppositePlayer(player);
        double distance = target.distance(opposite.getPosition());
        double acceptableDistance = getStatsCalculator().getActualSkillRange(player);
        return distance >= acceptableDistance;
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
        double distance = target.distance(player.getPosition());
        double optimalShotDistance = getStatsCalculator().getActualShotRange(player);
        if (distance > optimalShotDistance) {
            double excess = distance - optimalShotDistance;
            double r = Math.pow(RANDOM.nextDouble(), 2);
            Vector2D d = target.subtract(player.getPosition());
            d = d.scalarMultiply((optimalShotDistance + excess * r) / (optimalShotDistance + excess));
            target = d.add(player.getPosition());
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
    
    private Vector2D chooseBallTarget(Player player) {
        double netZoneLength = getPitch().calculateNetBlockedZoneLength(player);
        double riskMargin = getStatsCalculator().getActualRiskMargin(player);
        while (true) {
            Vector2D target = VectorUtils.generateRandomVector(riskMargin, Pitch.WIDTH - riskMargin, 
                    netZoneLength + riskMargin, Pitch.HALF_HEIGHT - riskMargin);
            if (player.getSide() == Side.HOME) {
                target = VectorUtils.mirror(target);
            }

            if (isTargetSmartEnough(player, target)) {
                return target;
            }
        }
    }
    
    private Vector2D calculateVisibleBallTarget(Player player, Vector2D target) {
        double visibleTargetRange = getStatsCalculator().getActualVisibleTargetRange(player);
        Vector2D deviation = calculateRandomDeviation(visibleTargetRange);
        return target.add(deviation);
    }

    private void chooseAndApplyBallTarget(Player player, Ball ball) {
        Vector2D target = chooseBallTarget(player);
        target = applyShotDistanceModification(player, target);
        target = applyShotAccuracyModification(player, target);
        ball.setRealTarget(target);
        ball.setVisibleTarget(calculateVisibleBallTarget(player, target));
        ball.setSpeed(getStatsCalculator().getActualBallSpeed(player));
        ball.setFlyingAboveNet(true);
    }

    private void hitBall(Player player, Ball ball) {    
        chooseAndApplyBallTarget(player, ball);
        if (!isTargetHighEnough(player, ball.getRealTarget())) {
            ball.setFlyingAboveNet(false);
        }
        
        lastHittedPlayer = player;
        decreasePlayerEnergy(player, ENERGY_LOSS_PER_HIT);
    }

    private boolean mayBallHitPlayerZone(Player player, Ball ball) {
        double distanceToPlayerZone = getPitch().calculateDistanceToZone(player.getSide(), ball.getVisibleTarget());
        return distanceToPlayerZone < Pitch.HALF_HEIGHT / 6;
    }
    
    Vector2D calculateOptimalBallInterceptPosition(Player player, Ball ball) {
        Line line = new Line(ball.getPosition(), ball.getVisibleTarget(), VectorUtils.DEFAULT_TOLERANCE);
        Vector2D projection = (Vector2D) line.project(player.getPosition());
        Vector2D target = ball.getPosition().distance(projection) < ball.getPosition().distance(ball.getVisibleTarget()) ?
                projection : ball.getVisibleTarget();
        target = getPitch().getClosestPointInZone(player.getSide(), target);
        return target;
    }
    
    Vector2D calculatePlayerOptimalPosition(Player player) {
        double blockedLengthForOppositePlayer = getPitch().calculateNetBlockedZoneLength(getPitch().getOppositePlayer(player));
        double optimalX = Pitch.WIDTH / 2;
        double optimalY = player.getSide().getModifier() * ((Pitch.HALF_HEIGHT + blockedLengthForOppositePlayer) / 2);
        return new Vector2D(optimalX, optimalY);
    }

    private void movePlayerToTarget(Player player, Vector2D target) {
        double speed = getStatsCalculator().getActualPlayerSpeed(player);
        double acc = getStatsCalculator().getActualPlayerAcceleration(player);
        double step = speed * getScaledTimeStep();
        double ac_step = acc * getScaledTimeStep();

        Vector2D v = player.getDirection().scalarMultiply(player.getSpeed() * getScaledTimeStep());
        Vector2D d = target.subtract(player.getPosition());
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
        player.setSpeed(v.getNorm() / getScaledTimeStep());
        if (player.getSpeed() != 0) {
            player.setDirection(v.scalarMultiply(1 / (player.getSpeed() * getScaledTimeStep())));
        }

        Vector2D move = player.getDirection().scalarMultiply(player.getSpeed() * getScaledTimeStep());
        player.setPosition(player.getPosition().add(move));
        decreasePlayerEnergy(player, move.getNorm() / getStatsCalculator().getVenueSpeedModifier() * ENERGY_LOSS_PER_DISTANCE);
    }

    private void decideAndMovePlayer(Player player, Ball ball) {
        Vector2D target;
        if (mayBallHitPlayerZone(player, ball)) {
            target = calculateOptimalBallInterceptPosition(player, ball);
        } else {
            target = calculatePlayerOptimalPosition(player);
        }
        movePlayerToTarget(player, target);
    }

    private void performLyingAction(Player player) {
        player.addLyingTime(getScaledTimeStep());
        if (player.getLyingTime() >= getStatsCalculator().getTotalLyingTime(player)) {
            player.setLying(false);
        }
    }
    
    private boolean isInSaveRange(Player player, Vector2D position) {
        double saveRange = MatchEngineConstants.PLAYER_HAND_LENGTH + getStatsCalculator().getActualSaveAddDistance(player);
        return player.getPosition().distance(position) <= saveRange;
    }
    private boolean isBallStillInSaveRange(Player player, Ball ball) {
        Vector2D nextBallPosition = getNextBallPosition(ball);
        return isInSaveRange(player, ball.getPosition()) && !isInSaveRange(player, nextBallPosition);
    }

    private boolean trySave(Player player, Ball ball) {
        if (isInSaveRange(player, ball.getPosition())) {
            player.lieDown();
            hitBall(player, ball);
            decreasePlayerEnergy(player, ENERGY_LOSS_PER_SAVE);
            return true;
        } else {
            return false;
        }
    }

    private void performPlayerAction(Player player) {
        Ball ball = getPitch().getBall();
        if (player.isLying()) {
            performLyingAction(player);
            return;
        }
        
        if (isPlayerZoneTargeted(player, ball)) {
            if (canPlayerHitBall(player, ball)) {
                hitBall(player, ball);
                return;
            } else if (hasBallHittedGround(ball) || isBallStillInSaveRange(player, ball)) {
                boolean saved = trySave(player, ball);
                if (saved) {
                    return;
                }
            }
        }
        
        decideAndMovePlayer(player, ball);
    }

    boolean isPlayerZoneTargeted(Player player, Ball ball) {
        return getPitch().isInsideZone(player.getSide(), ball.getVisibleTarget());
    }

    static boolean canPlayerHitBall(Player player, Ball ball) {
        final double distanceToBall = player.getPosition().distance(ball.getPosition());
        return Precision.compareTo(distanceToBall, MatchEngineConstants.PLAYER_HAND_LENGTH, VectorUtils.DEFAULT_TOLERANCE) <= 0;
    }

    private boolean hasBallHittedGround(Ball ball) {
        return VectorUtils.equalsWithTolerance(ball.getRealTarget(), ball.getPosition(), 0.001);
    }

    private void ballHitsNet(Ball ball) {
        Vector2D d = ball.getRealTarget().subtract(ball.getPosition());
        d = VectorUtils.mirror(d);
        d = d.scalarMultiply(NET_PONG_FACTOR);
        Vector2D newTarget = ball.getPosition().add(d);
        ball.setRealTarget(newTarget);
        ball.setVisibleTarget(newTarget);
    }
    
    private Vector2D getNextBallPosition(Ball ball) {
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
    
    private void moveBall(Ball ball) {
        double distToRealTarget = ball.getRealTarget().distance(ball.getPosition());
        double step = getScaledTimeStep() * ball.getSpeed();
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

    private void endGame(Side sideHitted) {
        if (sideHitted == null) {
            winningSide = lastHittedPlayer.getSide().getOpposite();
        } else {
            winningSide = sideHitted.getOpposite();
        }
    }

    private void performBallAction(Ball ball) {
        if (hasBallHittedGround(ball)) {
            endGame(getPitch().getBallSide(ball));
        } else {
            moveBall(ball);
        }
    }

    public void performEndOfGameActions() {
        getPitch().getPlayer(Side.HOME).changeEnergy(ENERGY_REGAIN_PER_GAME);
        getPitch().getPlayer(Side.AWAY).changeEnergy(ENERGY_REGAIN_PER_GAME);
    }

    public void performEndOfSetActions() {
        getPitch().getPlayer(Side.HOME).changeEnergy(ENERGY_REGAIN_PER_SET);
        getPitch().getPlayer(Side.AWAY).changeEnergy(ENERGY_REGAIN_PER_SET);
    }
}

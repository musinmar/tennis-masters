package tm.lib.engine;

import java.util.Random;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Precision;
import tm.lib.domain.competition.Match;

public class MatchEngine {

    public static final double TIME_STEP = 0.02;
    
    //private static final double NET_ZONE_MAX_LENGTH = Pitch.HHEIGHT / 5;
    private static final double NET_PONG = Pitch.HALF_HEIGHT / 35;
    private static final double ENERGY_LOSS_PER_DISTANCE = 0.9 / Pitch.WIDTH;
    private static final double ENERGY_LOSS_PER_HIT = 0.3;
    private static final double ENERGY_LOSS_PER_SAVE = 0.3;
    private static final double ENERGY_REGAIN_PER_GAME = 1;
    private static final double ENERGY_REGAIN_PER_SET = 3;
    
    private final Pitch pitch;
    private final StatsCalculator statsCalculator;
    private Side winningSide;
    private Player lastHittedPlayer;
    private boolean netWasHit;

    private final Random random = new Random(System.currentTimeMillis());

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

    private double getPlayerModifier(Player p) {
        return p.getSide().getModifier();
    }
    
    double calculateNetBlockedZoneLength(Player player) {
        double length = Math.abs(player.getPosition().getY()) / Pitch.HALF_HEIGHT * getStatsCalculator().getNetZone();
        return Math.max(length, 0);
    }
    
    private boolean isTargetSmartEnough(Player p, Vector2D target) {
        Player opposite = getPitch().getOppositePlayer(p);
        double distance = target.distance(opposite.getPosition());
        double acceptableDistance = getStatsCalculator().getActualSkillRange(p);
        return distance >= acceptableDistance;
    }

    private boolean isTargetHighEnough(Player p, Vector2D target) {
        double netZoneLength = calculateNetBlockedZoneLength(p);
        double mod = getPlayerModifier(p);
        if (target.getY() * mod > 0) {
            return true;
        }
        return Math.abs(target.getY()) > netZoneLength;
    }

    private void ballHitsNet(Player p, Vector2D target) {
        double mod = getPlayerModifier(p);
        Vector2D d = target.subtract(p.getPosition());
        double k = Math.abs(p.getPosition().getY() / (target.getY() - p.getPosition().getY()));
        d = d.scalarMultiply(k);
        Ball ball = getPitch().getBall();
        ball.setRealTarget(p.getPosition().add(d).add(new Vector2D(0, mod * NET_PONG)));
        ball.setVisibleTarget(target);
        netWasHit = true;
    }

    private Vector2D applyShotDistanceModification(Player p, Vector2D target) {
        double distance = target.distance(p.getPosition());
        double optimalShotDistance = getStatsCalculator().getActualShotRange(p);
        if (distance > optimalShotDistance) {
            double excess = distance - optimalShotDistance;
            double r = Math.pow(random.nextDouble(), 2);
            Vector2D d = target.subtract(p.getPosition());
            d = d.scalarMultiply((optimalShotDistance + excess * r) / (optimalShotDistance + excess));
            target = d.add(p.getPosition());
        }
        return target;
    }
    
    private Vector2D applyShotAccuracyModification(Player p, Vector2D target) {
        double targetRange = getStatsCalculator().getActualTargetRange(p);
        double phi = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * targetRange;
        Vector2D deviation = VectorUtils.rotate(new Vector2D(r, 0), phi);
        return target.add(deviation);
    }
    
    private Vector2D chooseBallTarget(Player p) {
        double netZoneLength = calculateNetBlockedZoneLength(p);
        double riskMargin = getStatsCalculator().getActualRiskMargin(p);
        while (true) {
            Vector2D target = VectorUtils.generateRandomVector(riskMargin, Pitch.WIDTH - riskMargin, 
                    netZoneLength + riskMargin, Pitch.HALF_HEIGHT - riskMargin);
            if (p.getSide() == Side.HOME) {
                target = VectorUtils.mirror(target);
            }

            if (isTargetSmartEnough(p, target)) {
                return target;
            }
        }
    }

    private void chooseAndApplyBallTarget(Player p, Ball b) {
        Vector2D target = chooseBallTarget(p);
        target = applyShotDistanceModification(p, target);
        target = applyShotAccuracyModification(p, target);
        if (!isTargetHighEnough(p, target)) {
            ballHitsNet(p, target);
        } else {
            b.setRealTarget(target);
        }
    }

    private void getNewVisibleBallTarget(Player p, Ball b) {
        double visibleTargetRange = getStatsCalculator().getActualVisibleTargetRange(p);
        double phi = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * visibleTargetRange;
        double target_x = b.getRealTarget().getX() + Math.cos(phi) * r;
        double target_y = b.getRealTarget().getY() + Math.sin(phi) * r;
        b.setVisibleTarget(new Vector2D(target_x, target_y));
    }

    private void hitBall(Player p) {
        Ball ball = getPitch().getBall();
        chooseAndApplyBallTarget(p, ball);
        ball.setSpeed(getStatsCalculator().getActualBallSpeed(p));
        if (netWasHit) {
            netWasHit = false;
        } else {
            getNewVisibleBallTarget(p, ball);
        }
        lastHittedPlayer = p;
        getStatsCalculator().decreasePlayerEnergy(p, ENERGY_LOSS_PER_HIT);
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
        double blockedLengthForOppositePlayer = calculateNetBlockedZoneLength(getPitch().getOppositePlayer(player));
        double optimalX = Pitch.WIDTH / 2;
        double optimalY = player.getSide().getModifier() * ((Pitch.HALF_HEIGHT + blockedLengthForOppositePlayer) / 2);
        return new Vector2D(optimalX, optimalY);
    }

    private void movePlayerToTarget(Player p, Vector2D target) {
        double speed = getStatsCalculator().getActualPlayerSpeed(p);
        double acc = getStatsCalculator().getActualPlayerAcceleration(p);
        double step = speed * TIME_STEP;
        double ac_step = acc * TIME_STEP;

        Vector2D v = p.getDirection().scalarMultiply(p.getSpeed() * TIME_STEP);
        Vector2D d = target.subtract(p.getPosition());
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
        p.setSpeed(v.getNorm() / TIME_STEP);
        if (p.getSpeed() != 0) {
            p.setDirection(v.scalarMultiply(1 / (p.getSpeed() * TIME_STEP)));
        }

        Vector2D move = p.getDirection().scalarMultiply(p.getSpeed() * TIME_STEP);
        p.setPosition(p.getPosition().add(move));
        getStatsCalculator().decreasePlayerEnergy(p, move.getNorm() / getStatsCalculator().getVenueSpeedModifier() * ENERGY_LOSS_PER_DISTANCE);
    }

    private void decideAndMovePlayer(Player player) {
        Ball ball = getPitch().getBall();
        Vector2D target;
        if (mayBallHitPlayerZone(player, ball)) {
            target = calculateOptimalBallInterceptPosition(player, ball);
        } else {
            target = calculatePlayerOptimalPosition(player);
        }
        movePlayerToTarget(player, target);
    }

    private void performLyingAction(Player player) {
        player.addLyingTime(TIME_STEP);
        if (player.getLyingTime() >= getStatsCalculator().getTotalLyingTime(player)) {
            player.setLying(false);
        }
    }

    private void performPlayerAction(Player player) {
        Ball ball = getPitch().getBall();
        if (player.isLying()) {
            performLyingAction(player);
        } else if (hasBallHittedGround(ball) && isPlayerZoneTargeted(player, ball)) {
            trySave(player.getSide());
        } else if (isPlayerZoneTargeted(player, ball) && canPlayerHitBall(player, ball)) {
            hitBall(player);
        } else {
            decideAndMovePlayer(player);
        }
    }

    boolean isPlayerZoneTargeted(Player player, Ball ball) {
        return getPitch().isInsideZone(player.getSide(), ball.getVisibleTarget());
    }

    static boolean canPlayerHitBall(Player player, Ball ball) {
        final double distanceToBall = player.getPosition().distance(ball.getPosition());
        return Precision.compareTo(distanceToBall, MatchEngineConstants.PLAYER_HAND_LENGTH, VectorUtils.DEFAULT_TOLERANCE) <= 0;
    }

    private boolean hasBallHittedGround(Ball b) {
        return VectorUtils.equalsWithTolerance(b.getRealTarget(), b.getPosition(), 0.001);
    }

    private void moveBall(Ball b) {
        Vector2D d = b.getVisibleTarget().subtract(b.getPosition());
        double dd = d.getNorm();
        double dist_to_target = b.getRealTarget().subtract(b.getPosition()).getNorm();
        double step = TIME_STEP * b.getSpeed();
        double m_step = step * dd / dist_to_target;
        if (dd > m_step) {
            d = d.scalarMultiply(1 / dd).scalarMultiply(m_step);
        }
        b.setPosition(b.getPosition().add(d));

        int steps = (int) (dist_to_target / step);
        if (steps == 0) {
            b.setVisibleTarget(b.getRealTarget());
        } else {
            Vector2D visibleTargetD = b.getRealTarget().subtract(b.getVisibleTarget()).scalarMultiply(1 / (double) steps);
            b.setVisibleTarget(b.getVisibleTarget().add(visibleTargetD));
        }
    }

    private void endGame(Side sideHitted) {
        if (sideHitted == null) {
            winningSide = lastHittedPlayer.getSide().getOpposite();
        } else {
            winningSide = sideHitted.getOpposite();
        }
    }

    private Side getBallSide(Ball b) {
        Vector2D position = b.getPosition();
        if (position.getX() >= 0 && position.getX() <= Pitch.WIDTH
                && position.getY() >= -Pitch.HALF_HEIGHT && position.getY() <= Pitch.HALF_HEIGHT) {
            if (position.getY() >= 0) {
                return Side.HOME;
            } else {
                return Side.AWAY;
            }
        } else {
            return null;
        }
    }

    private boolean trySave(Side sideHitted) {
        Player p = getPitch().getPlayer(sideHitted);
        double save_max_distance = MatchEngineConstants.PLAYER_HAND_LENGTH + getStatsCalculator().getActualSaveAddDistance(p);
        if (p.getPosition().subtract(getPitch().getBall().getPosition()).getNorm() <= save_max_distance && !p.isLying()) {
            p.lieDown();
            hitBall(p);
            getStatsCalculator().decreasePlayerEnergy(p, ENERGY_LOSS_PER_SAVE);
            return true;
        } else {
            return false;
        }
    }

    private void performBallAction(Ball b) {
        if (hasBallHittedGround(b)) {
            endGame(getBallSide(b));
        } else {
            moveBall(b);
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

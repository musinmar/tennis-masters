package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class StandardStrategy implements Strategy {

    @Override
    public Decision makeDecision(Pitch pitch, Player player) {
        if (isPlayerZoneTargeted(pitch, player)) {
            if (pitch.canPlayerHitBall(player)) {
                return createHitBallDecision(pitch, player);
            } else if (pitch.getBall().hasHittedGround() || isBallStillInSaveRange(pitch, player)) {
                if (pitch.canPlayerSaveBall(player, pitch.getBall().getPosition())) {
                    return createHitBallDecision(pitch, player);
                }
            }
        }

        return createMoveToDecision(pitch, player);
    }

    private boolean isPlayerZoneTargeted(Pitch pitch, Player player) {
        return pitch.isInsideZone(player.getSide(), pitch.getBall().getVisibleTarget());
    }

    private boolean isBallStillInSaveRange(Pitch pitch, Player player) {
        Ball ball = pitch.getBall();
        Vector2D nextBallPosition = getNextBallPosition(ball);
        return pitch.canPlayerSaveBall(player, ball.getPosition()) && !pitch.canPlayerSaveBall(player, nextBallPosition);
    }

    private Vector2D getNextBallPosition(Ball ball) {
        Vector2D d = ball.getVisibleTarget().subtract(ball.getPosition());
        double distToVisibleTarget = d.getNorm();
        double distToRealTarget = ball.getRealTarget().distance(ball.getPosition());
        double step = MatchEngine.getScaledTimeStep() * ball.getSpeed();
        double modifiedStep = step * distToVisibleTarget / distToRealTarget;
        if (distToVisibleTarget > modifiedStep) {
            d = d.scalarMultiply(1 / distToVisibleTarget).scalarMultiply(modifiedStep);
        }
        return ball.getPosition().add(d);
    }

    private Decision createHitBallDecision(Pitch pitch, Player player) {
        double netZoneLength = pitch.calculateNetBlockedZoneLength(player);
        double riskMargin = pitch.getStatsCalculator().getActualRiskMargin(player);
        while (true) {
            Vector2D target = VectorUtils.generateRandomVector(riskMargin, Pitch.WIDTH - riskMargin,
                    netZoneLength + riskMargin, Pitch.HALF_HEIGHT - riskMargin);
            if (player.getSide() == Side.HOME) {
                target = VectorUtils.mirror(target);
            }

            if (isTargetSmartEnough(pitch, player, target)) {
                Decision decision = new Decision();
                decision.setHitBall(true);
                decision.setBallTargetPosition(target);
                return decision;
            }
        }
    }

    private boolean isTargetSmartEnough(Pitch pitch, Player player, Vector2D target) {
        Player opposite = pitch.getOppositePlayer(player);
        double distance = target.distance(opposite.getPosition());
        double acceptableDistance = pitch.getStatsCalculator().getActualSkillRange(player);
        return distance >= acceptableDistance;
    }

    private Decision createMoveToDecision(Pitch pitch, Player player) {
        Vector2D target;
        if (mayBallHitPlayerZone(pitch, player)) {
            target = calculateOptimalBallInterceptPosition(pitch, player);
        } else {
            target = calculatePlayerOptimalPosition(pitch, player);
        }
        Decision decision = new Decision();
        decision.setMoveToPosition(target);
        return decision;
    }

    private boolean mayBallHitPlayerZone(Pitch pitch, Player player) {
        double distanceToPlayerZone = pitch.calculateDistanceToZone(player.getSide(), pitch.getBall().getVisibleTarget());
        return distanceToPlayerZone < Pitch.HALF_HEIGHT / 6;
    }

    Vector2D calculateOptimalBallInterceptPosition(Pitch pitch, Player player) {
        Ball ball = pitch.getBall();
        Line line = new Line(ball.getPosition(), ball.getVisibleTarget(), VectorUtils.DEFAULT_TOLERANCE);
        Vector2D projection = (Vector2D) line.project(player.getPosition());
        Vector2D target = ball.getPosition().distance(projection) < ball.getPosition().distance(ball.getVisibleTarget()) ?
                projection : ball.getVisibleTarget();
        target = pitch.getClosestPointInZone(player.getSide(), target);
        return target;
    }

    Vector2D calculatePlayerOptimalPosition(Pitch pitch, Player player) {
        double blockedLengthForOppositePlayer = pitch.calculateNetBlockedZoneLength(pitch.getOppositePlayer(player));
        double optimalX = Pitch.WIDTH / 2;
        double optimalY = player.getSide().getModifier() * ((Pitch.HALF_HEIGHT + blockedLengthForOppositePlayer) / 2);
        return new Vector2D(optimalX, optimalY);
    }

}

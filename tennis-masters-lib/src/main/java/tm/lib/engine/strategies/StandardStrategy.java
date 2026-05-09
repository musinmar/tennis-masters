package tm.lib.engine.strategies;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.engine.BallState;
import tm.lib.engine.Decision;
import tm.lib.engine.Pitch;
import tm.lib.engine.Player;
import tm.lib.engine.VectorUtils;

import static tm.lib.engine.Side.HOME;

public class StandardStrategy extends AbstractStrategy {

    @Override
    public Decision makeDecision(Pitch pitch, double timeStep) {
        Player player = pitch.getPlayer(HOME);
        if (isPlayerZoneTargeted(pitch, player)) {
            if (pitch.canPlayerHitBall(player)) {
                return createHitBallDecision(pitch, player);
            } else if (pitch.getBall().hasTouchedGround() || isBallStillInSaveRange(pitch, player, timeStep)) {
                if (pitch.canPlayerSaveBall(player, pitch.getBall().getPosition())) {
                    return createHitBallDecision(pitch, player);
                }
            }
        }

        return createMoveToDecision(pitch, player);
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
        BallState ball = pitch.getBall();
        Line line = new Line(ball.getPosition(), ball.getVisibleTarget(), VectorUtils.DEFAULT_TOLERANCE);
        Vector2D projection = (Vector2D) line.project(player.getState().getPosition());
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

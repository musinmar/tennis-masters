package tm.lib.engine.strategies;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.engine.BallState;
import tm.lib.engine.Decision;
import tm.lib.engine.Pitch;
import tm.lib.engine.Player;
import tm.lib.engine.Strategy;
import tm.lib.engine.VectorUtils;

import static tm.lib.engine.Side.HOME;

public abstract class AbstractStrategy implements Strategy {

    public static final int MAX_BALL_DECISION_ATTEMPTS = 3;

    protected boolean isPlayerZoneTargeted(Pitch pitch, Player player) {
        return pitch.isInsideZone(player.getSide(), pitch.getBall().getVisibleTarget());
    }

    protected Vector2D getNextBallPosition(BallState ball, double timeStep) {
        Vector2D d = ball.getVisibleTarget().subtract(ball.getPosition());
        double distToVisibleTarget = d.getNorm();
        double distToRealTarget = ball.getRealTarget().distance(ball.getPosition());
        double step = timeStep * ball.getSpeed();
        double modifiedStep = step * distToVisibleTarget / distToRealTarget;
        if (distToVisibleTarget > modifiedStep) {
            d = d.scalarMultiply(1 / distToVisibleTarget).scalarMultiply(modifiedStep);
        }
        return ball.getPosition().add(d);
    }

    protected boolean isBallStillInSaveRange(Pitch pitch, Player player, double timeStep) {
        BallState ball = pitch.getBall();
        Vector2D nextBallPosition = getNextBallPosition(ball, timeStep);
        return pitch.canPlayerSaveBall(player, ball.getPosition()) && !pitch.canPlayerSaveBall(player, nextBallPosition);
    }

    protected boolean isTargetSmartEnough(Pitch pitch, Player player, Vector2D target) {
        Player opposite = pitch.getOppositePlayer(player);
        double distance = target.distance(opposite.getState().getPosition());
        double acceptableDistance = pitch.getStatsCalculator().getActualSkillRange(player);
        return distance >= acceptableDistance;
    }

    protected Decision createHitBallDecision(Pitch pitch, Player player) {
        double netZoneLength = pitch.calculateNetBlockedZoneLength(player);
        double riskMargin = pitch.getStatsCalculator().getActualRiskMargin(player);
        int attempt = 0;
        while (true) {
            Vector2D target = VectorUtils.generateRandomVector(riskMargin, Pitch.WIDTH - riskMargin,
                    netZoneLength + riskMargin, Pitch.HALF_HEIGHT - riskMargin);
            if (player.getSide() == HOME) {
                target = VectorUtils.mirror(target);
            }

            if (isTargetSmartEnough(pitch, player, target) || attempt == MAX_BALL_DECISION_ATTEMPTS) {
                Decision decision = new Decision();
                decision.setHitBall(true);
                decision.setBallTargetPosition(target);
                return decision;
            }
            ++attempt;
        }
    }
}

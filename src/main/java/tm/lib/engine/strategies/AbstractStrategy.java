package tm.lib.engine.strategies;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.engine.Ball;
import tm.lib.engine.Decision;
import tm.lib.engine.MatchEngine;
import tm.lib.engine.Pitch;
import tm.lib.engine.Player;
import tm.lib.engine.Strategy;
import tm.lib.engine.VectorUtils;

import static tm.lib.engine.Side.HOME;

public abstract class AbstractStrategy implements Strategy {
    protected boolean isPlayerZoneTargeted(Pitch pitch, Player player) {
        return pitch.isInsideZone(player.getSide(), pitch.getBall().getVisibleTarget());
    }

    protected Vector2D getNextBallPosition(Ball ball) {
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

    protected boolean isBallStillInSaveRange(Pitch pitch, Player player) {
        Ball ball = pitch.getBall();
        Vector2D nextBallPosition = getNextBallPosition(ball);
        return pitch.canPlayerSaveBall(player, ball.getPosition()) && !pitch.canPlayerSaveBall(player, nextBallPosition);
    }

    protected boolean isTargetSmartEnough(Pitch pitch, Player player, Vector2D target) {
        Player opposite = pitch.getOppositePlayer(player);
        double distance = target.distance(opposite.getPosition());
        double acceptableDistance = pitch.getStatsCalculator().getActualSkillRange(player);
        return distance >= acceptableDistance;
    }

    protected Decision createHitBallDecision(Pitch pitch, Player player) {
        double netZoneLength = pitch.calculateNetBlockedZoneLength(player);
        double riskMargin = pitch.getStatsCalculator().getActualRiskMargin(player);
        while (true) {
            Vector2D target = VectorUtils.generateRandomVector(riskMargin, Pitch.WIDTH - riskMargin,
                    netZoneLength + riskMargin, Pitch.HALF_HEIGHT - riskMargin);
            if (player.getSide() == HOME) {
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
}

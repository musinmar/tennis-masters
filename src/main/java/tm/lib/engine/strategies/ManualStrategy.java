package tm.lib.engine.strategies;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.engine.Decision;
import tm.lib.engine.Pitch;
import tm.lib.engine.Player;

import static tm.lib.engine.Side.HOME;

@Getter
@Setter
@Slf4j
public class ManualStrategy extends AbstractStrategy {

    private Vector2D moveToPosition = Vector2D.ZERO;

    @Override
    public Decision makeDecision(Pitch pitch) {
        Player player = pitch.getPlayer(HOME);
        if (isPlayerZoneTargeted(pitch, player)) {
            if (pitch.canPlayerHitBall(player)) {
                return createHitBallDecision(pitch, player);
            } else if (pitch.getBall().hasHittedGround() || isBallStillInSaveRange(pitch, player)) {
                if (pitch.canPlayerSaveBall(player, pitch.getBall().getPosition())) {
                    return createHitBallDecision(pitch, player);
                }
            }
        }

        return createMoveDecision();
    }

    private Decision createMoveDecision() {
        var decision = new Decision();
        log.info("Move to: " + moveToPosition);
        decision.setMoveToPosition(moveToPosition);
        decision.setBallTargetPosition(Vector2D.ZERO);
        decision.setHitBall(false);
        return decision;
    }


}

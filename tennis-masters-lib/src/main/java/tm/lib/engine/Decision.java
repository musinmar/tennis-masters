package tm.lib.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import static tm.lib.engine.VectorUtils.mirror;

@Getter
@Setter
public class Decision {
    private Vector2D moveToPosition = Vector2D.ZERO;
    private boolean hitBall;
    private Vector2D ballTargetPosition = Vector2D.ZERO;

    public Decision createMirrored() {
        Decision mirroredDecision = new Decision();
        mirroredDecision.hitBall = hitBall;
        mirroredDecision.moveToPosition = mirror(moveToPosition);
        mirroredDecision.ballTargetPosition = mirror(ballTargetPosition);
        return mirroredDecision;
    }
}

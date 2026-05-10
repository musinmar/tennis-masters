package tm.lib.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import static tm.lib.engine.VectorUtils.mirror;

@Getter
@Setter
public class BallState {

    private Vector2D position = Vector2D.ZERO;
    private Vector2D realTarget = Vector2D.ZERO;
    private Vector2D visibleTarget = Vector2D.ZERO;
    private double speed = 0;
    private boolean flyingAboveNet = true;

    public BallState createMirrored() {
        BallState mirroredBall = new BallState();
        mirroredBall.position = mirror(position);
        mirroredBall.realTarget = mirror(realTarget);
        mirroredBall.visibleTarget = mirror(visibleTarget);
        mirroredBall.speed = speed;
        mirroredBall.flyingAboveNet = flyingAboveNet;
        return mirroredBall;
    }

    public BallState copy() {
        BallState copy = new BallState();
        copy.position = position;
        copy.realTarget = realTarget;
        copy.visibleTarget = visibleTarget;
        copy.speed = speed;
        copy.flyingAboveNet = flyingAboveNet;
        return copy;
    }
}

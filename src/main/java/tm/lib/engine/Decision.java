package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import static tm.lib.engine.VectorUtils.mirror;

public class Decision {
    private Vector2D moveToPosition = Vector2D.ZERO;
    private boolean hitBall;
    private Vector2D ballTargetPosition = Vector2D.ZERO;

    public Vector2D getMoveToPosition() {
        return moveToPosition;
    }

    public void setMoveToPosition(Vector2D moveToPosition) {
        this.moveToPosition = moveToPosition;
    }

    public boolean isHitBall() {
        return hitBall;
    }

    public void setHitBall(boolean hitBall) {
        this.hitBall = hitBall;
    }

    public Vector2D getBallTargetPosition() {
        return ballTargetPosition;
    }

    public void setBallTargetPosition(Vector2D ballTargetPosition) {
        this.ballTargetPosition = ballTargetPosition;
    }

    public Decision createMirrored() {
        Decision mirroredDecision = new Decision();
        mirroredDecision.hitBall = hitBall;
        mirroredDecision.moveToPosition = mirror(moveToPosition);
        mirroredDecision.ballTargetPosition = mirror(ballTargetPosition);
        return mirroredDecision;
    }
}

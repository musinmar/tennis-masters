package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Decision {
    private Vector2D moveToPosition;
    private boolean hitBall;
    private Vector2D ballTargetPosition;

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
}

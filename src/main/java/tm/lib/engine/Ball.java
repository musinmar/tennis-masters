package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Ball
{
    private Vector2D position;
    private Vector2D realTarget;
    private Vector2D visibleTarget;
    private double speed;

    public Ball()
    {
        position = Vector2D.ZERO;
        realTarget = Vector2D.ZERO;
        visibleTarget = Vector2D.ZERO;
        speed = 0;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getRealTarget() {
        return realTarget;
    }

    public void setRealTarget(Vector2D realTarget) {
        this.realTarget = realTarget;
    }

    public Vector2D getVisibleTarget() {
        return visibleTarget;
    }

    public void setVisibleTarget(Vector2D visibleTarget) {
        this.visibleTarget = visibleTarget;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

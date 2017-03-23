package tm.lib.engine;

public class Ball
{
    private Point2d position;
    private Point2d realTarget;
    private Point2d fakeTarget;
    private double speed;

    public Ball()
    {
        position = new Point2d(0, 0);
        realTarget = new Point2d(0, 0);
        fakeTarget = new Point2d(0, 0);
        speed = 0;
    }

    public Point2d getPosition() {
        return position;
    }

    public void setPosition(Point2d position) {
        this.position = position;
    }

    public Point2d getRealTarget() {
        return realTarget;
    }

    public void setRealTarget(Point2d realTarget) {
        this.realTarget = realTarget;
    }

    public Point2d getFakeTarget() {
        return fakeTarget;
    }

    public void setFakeTarget(Point2d fakeTarget) {
        this.fakeTarget = fakeTarget;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

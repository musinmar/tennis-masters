package tm.lib.engine;

public class Ball
{
    public Point2d position;
    public Point2d target;
    public Point2d fake_target;
    public double speed;

    public Ball()
    {
        position = new Point2d(0, 0);
        target = new Point2d(0, 0);
        fake_target = new Point2d(0, 0);
        speed = 0;
    }
}

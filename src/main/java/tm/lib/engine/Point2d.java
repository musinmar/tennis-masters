package tm.lib.engine;

import org.eclipse.swt.graphics.Point;

public class Point2d
{
    public double x;
    public double y;

    public Point2d()
    {
        x = 0;
        y = 0;
    }

    public Point2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Point2d(Point2d other)
    {
        this(other.x, other.y);
    }

    public void set(Point2d other)
    {
        x = other.x;
        y = other.y;
    }

    public Point2d multiply(double v)
    {
        return new Point2d(x * v, y * v);
    }

    public Point2d div(double v)
    {
        return new Point2d(x / v, y / v);
    }

    public Point2d plus(Point2d other)
    {
        return new Point2d(x + other.x, y + other.y);
    }

    public Point2d minus(Point2d other)
    {
        return new Point2d(x - other.x, y - other.y);
    }

    public Point to_point()
    {
        return new Point((int) x, (int) y);
    }

    public double dist(Point2d other)
    {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public double norm()
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}

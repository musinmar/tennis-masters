package tm.lib.engine;

import org.eclipse.swt.graphics.Point;

public class Point2d {

    public final double x;
    public final double y;

    public Point2d() {
        x = 0;
        y = 0;
    }

    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2d(Point2d other) {
        this(other.x, other.y);
    }

    public Point2d multipliedBy(double v) {
        return new Point2d(x * v, y * v);
    }

    public Point2d dividedBy(double v) {
        return new Point2d(x / v, y / v);
    }

    public Point2d summedWith(Point2d other) {
        return new Point2d(x + other.x, y + other.y);
    }

    public Point2d subtractedBy(Point2d other) {
        return new Point2d(x - other.x, y - other.y);
    }

    public Point2d mirrored() {
        return new Point2d(x, -y);
    }

    public Point toPoint() {
        return new Point((int) x, (int) y);
    }

    public double dist(Point2d other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public double norm() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}

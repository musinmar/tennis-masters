package tm.lib.engine;

public class Point2d {

    private final double x;
    private final double y;

    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2d(Point2d other) {
        this(other.x, other.y);
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point2d scalarMultiply(double v) {
        return new Point2d(getX() * v, getY() * v);
    }

    public Point2d dividedBy(double v) {
        return new Point2d(getX() / v, getY() / v);
    }

    public Point2d add(Point2d other) {
        return new Point2d(getX() + other.getX(), getY() + other.getY());
    }

    public Point2d subtract(Point2d other) {
        return new Point2d(getX() - other.getX(), getY() - other.getY());
    }

    public double distance(Point2d other) {
        return Math.sqrt(Math.pow(getX() - other.getX(), 2) + Math.pow(getY() - other.getY(), 2));
    }

    public double getNorm() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }
}

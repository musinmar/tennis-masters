package tm.lib.engine;

import org.eclipse.swt.graphics.Point;

public class VectorUtils {
    public static Point2d mirror(Point2d vector) {
        return new Point2d(vector.getX(), vector.getY() * (-1));
    }
    
    public static Point toPoint(Point2d vector) {
        return new Point((int) vector.getX(), (int) vector.getY());
    }
}

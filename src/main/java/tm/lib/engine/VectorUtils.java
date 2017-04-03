package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.eclipse.swt.graphics.Point;

public class VectorUtils {
    public static final double DEFAULT_TOLERANCE = 1.0e-10;
    
    public static Vector2D mirror(Vector2D vector) {
        return new Vector2D(vector.getX(), vector.getY() * (-1));
    }
    
    public static Point toPoint(Vector2D vector) {
        return new Point((int) vector.getX(), (int) vector.getY());
    }
    
    public static boolean equalWithTolerance(Vector2D a, Vector2D b) {
        if (a == b) {
            return true;
        }

        if (a.isNaN()) {
            return b.isNaN();
        }

        return FastMath.abs(a.getX() - b.getX()) < DEFAULT_TOLERANCE 
                && FastMath.abs(a.getY() - b.getY()) < DEFAULT_TOLERANCE;
    }
}

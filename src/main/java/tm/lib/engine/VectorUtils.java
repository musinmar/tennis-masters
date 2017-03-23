package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.eclipse.swt.graphics.Point;

public class VectorUtils {
    public static Vector2D mirror(Vector2D vector) {
        return new Vector2D(vector.getX(), vector.getY() * (-1));
    }
    
    public static Point toPoint(Vector2D vector) {
        return new Point((int) vector.getX(), (int) vector.getY());
    }
}

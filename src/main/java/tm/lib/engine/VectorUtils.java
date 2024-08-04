package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Random;

public class VectorUtils {

    public static final double DEFAULT_TOLERANCE = 1.0e-10;
    public static final Random RANDOM = new Random();

    public static Vector2D mirror(Vector2D vector) {
        return new Vector2D(vector.getX(), vector.getY() * (-1));
    }

    public static boolean equalsWithTolerance(Vector2D a, Vector2D b) {
        return equalsWithTolerance(a, b, DEFAULT_TOLERANCE);
    }

    public static boolean equalsWithTolerance(Vector2D a, Vector2D b, double tolerance) {
        if (a == b) {
            return true;
        }

        if (a.isNaN()) {
            return b.isNaN();
        }

        return a.distanceInf(b) < tolerance;
    }

    public static Vector2D rotate(Vector2D vector, double angle) {
        double x = Math.cos(angle) * vector.getX() - Math.sin(angle) * vector.getY();
        double y = Math.sin(angle) * vector.getX() + Math.cos(angle) * vector.getY();
        return new Vector2D(x, y);
    }

    public static Vector2D generateRandomVector(double minX, double maxX, double minY, double maxY) {
        double x = RANDOM.nextDouble() * (maxX - minX) + minX;
        double y = RANDOM.nextDouble() * (maxY - minY) + minY;
        return new Vector2D(x, y);
    }
}

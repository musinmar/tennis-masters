package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VectorUtilsTest {

    private static final double TOLERANCE = 1e-6;

    @Test
    public void testRotate() {
        Vector2D v1 = new Vector2D(1, 0);
        Vector2D actual1 = VectorUtils.rotate(v1, FastMath.toRadians(30));
        Vector2D expected1 = new Vector2D(0.866025, 0.5);
        assertTrue(VectorUtils.equalsWithTolerance(actual1, expected1, TOLERANCE));

        Vector2D actual2 = VectorUtils.rotate(actual1, FastMath.toRadians(60));
        Vector2D expected2 = new Vector2D(0, 1);
        assertTrue(VectorUtils.equalsWithTolerance(actual2, expected2, TOLERANCE));

        Vector2D actual3 = VectorUtils.rotate(actual2, FastMath.toRadians(45));
        Vector2D expected3 = new Vector2D(-0.707107, 0.707107);
        assertTrue(VectorUtils.equalsWithTolerance(actual3, expected3, TOLERANCE));
    }

    @Test
    public void testRandomVector() {
        Vector2D v = VectorUtils.generateRandomVector(-50, 100, -20, 40);
        assertTrue(v.getX() > -50);
        assertTrue(v.getX() < 100);
        assertTrue(v.getY() > -20);
        assertTrue(v.getY() < 40);
    }
}

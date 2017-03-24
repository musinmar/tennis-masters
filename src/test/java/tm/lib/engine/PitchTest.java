package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;
import static org.junit.Assert.*;

public class PitchTest {

    @Test
    public void testIsInsideZone() {
        Vector2D position;

        position = new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT / 2);
        assertTrue(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(-1, Pitch.HALF_HEIGHT / 2);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, 1);
        assertTrue(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, Pitch.HALF_HEIGHT - 1);
        assertTrue(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT + 1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, Pitch.HALF_HEIGHT - 1);
        assertTrue(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH + 1, Pitch.HALF_HEIGHT / 2);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, 1);
        assertTrue(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, 1);
        assertTrue(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT / 2);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertTrue(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(-1, -Pitch.HALF_HEIGHT / 2);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, -1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertTrue(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, -Pitch.HALF_HEIGHT + 1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertTrue(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT - 1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, -Pitch.HALF_HEIGHT + 1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertTrue(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH + 1, -Pitch.HALF_HEIGHT / 2);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertFalse(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, -1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertTrue(Pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, -1);
        assertFalse(Pitch.isInsideZone(Side.HOME, position));
        assertTrue(Pitch.isInsideZone(Side.AWAY, position));
    }

}

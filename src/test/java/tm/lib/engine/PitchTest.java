package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;
import static org.junit.Assert.*;

public class PitchTest {

    @Test
    public void testIsInsideZone() {
        Vector2D position;
        Pitch pitch = new Pitch(null, null, null);

        position = new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT / 2);
        assertTrue(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(-1, Pitch.HALF_HEIGHT / 2);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, 1);
        assertTrue(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, Pitch.HALF_HEIGHT - 1);
        assertTrue(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT + 1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, Pitch.HALF_HEIGHT - 1);
        assertTrue(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH + 1, Pitch.HALF_HEIGHT / 2);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, 1);
        assertTrue(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, 1);
        assertTrue(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT / 2);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertTrue(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(-1, -Pitch.HALF_HEIGHT / 2);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, -1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertTrue(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(1, -Pitch.HALF_HEIGHT + 1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertTrue(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT - 1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, -Pitch.HALF_HEIGHT + 1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertTrue(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH + 1, -Pitch.HALF_HEIGHT / 2);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertFalse(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH - 1, -1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertTrue(pitch.isInsideZone(Side.AWAY, position));

        position = new Vector2D(Pitch.WIDTH / 2, -1);
        assertFalse(pitch.isInsideZone(Side.HOME, position));
        assertTrue(pitch.isInsideZone(Side.AWAY, position));
    }
    
    @Test
    public void testDistanceToZone() {
        Vector2D position;
        Pitch pitch = new Pitch(null, null, null);

        position = new Vector2D(-20, Pitch.HALF_HEIGHT / 2);
        assertEquals(20, pitch.calculateDistanceToZone(Side.HOME, position), VectorUtils.DEFAULT_TOLERANCE);
        
        position = new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT - 30);
        assertEquals(30, pitch.calculateDistanceToZone(Side.AWAY, position), VectorUtils.DEFAULT_TOLERANCE);
    }
    
    @Test
    public void testClosestPointInZone() {
        Vector2D target;
        Pitch pitch = new Pitch(null, null, null);
        
        target = new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT / 2);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT / 2), pitch.getClosestPointInZone(Side.HOME, target));

        target = new Vector2D(-20, Pitch.HALF_HEIGHT / 2);
        assertEquals(new Vector2D(0, Pitch.HALF_HEIGHT / 2), pitch.getClosestPointInZone(Side.HOME, target));
        
        target = new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT - 30);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT), pitch.getClosestPointInZone(Side.AWAY, target));
    }
}

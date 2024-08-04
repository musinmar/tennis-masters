package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Test;
import tm.lib.domain.core.Knight;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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

    @Test
    public void testCanPlayerHitBall() {
        Pitch pitch = new Pitch(null, null, null);
        Knight knight = mock(Knight.class);
        Player player = new Player(knight, Side.HOME);
        Ball ball = pitch.getBall();
        Vector2D ballPosition = new Vector2D(10, 20);
        ball.setPosition(ballPosition);

        player.setPosition(ballPosition);
        assertTrue(pitch.canPlayerHitBall(player));

        player.setPosition(ballPosition.add(new Vector2D(0, MatchEngineConstants.PLAYER_HAND_LENGTH / 2)));
        assertTrue(pitch.canPlayerHitBall(player));

        player.setPosition(ballPosition.add(new Vector2D(0, MatchEngineConstants.PLAYER_HAND_LENGTH)));
        assertTrue(pitch.canPlayerHitBall(player));

        player.setPosition(ballPosition.add(new Vector2D(0, MatchEngineConstants.PLAYER_HAND_LENGTH + 1)));
        assertFalse(pitch.canPlayerHitBall(player));

        player.setPosition(ballPosition.add(new Vector2D(MatchEngineConstants.PLAYER_HAND_LENGTH, 0)));
        assertTrue(pitch.canPlayerHitBall(player));

        player.setPosition(ballPosition.add(new Vector2D(MatchEngineConstants.PLAYER_HAND_LENGTH + 1, 0)));
        assertFalse(pitch.canPlayerHitBall(player));
    }
}

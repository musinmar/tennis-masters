package tm.lib.engine.strategies;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Test;
import tm.lib.engine.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandardStrategyTest {

    private final StandardStrategy strategy = new StandardStrategy();

    @Test
    public void testCalculatePlayerOptimalPosition() {
        Pitch pitch = mock(Pitch.class);
        Player currentPlayer = mock(Player.class);
        Player oppositePlayer = mock(Player.class);
        when(pitch.getOppositePlayer(currentPlayer)).thenReturn(oppositePlayer);

        // Optimal position for home player

        when(currentPlayer.getSide()).thenReturn(Side.HOME);

        when(pitch.calculateNetBlockedZoneLength(any())).thenReturn(0.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT / 2), strategy.calculatePlayerOptimalPosition(pitch, currentPlayer));

        when(pitch.calculateNetBlockedZoneLength(any())).thenReturn(100.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, (Pitch.HALF_HEIGHT + 100) / 2), strategy.calculatePlayerOptimalPosition(pitch, currentPlayer));

        // Optimal position for away player

        when(currentPlayer.getSide()).thenReturn(Side.AWAY);

        when(pitch.calculateNetBlockedZoneLength(any())).thenReturn(0.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT / 2), strategy.calculatePlayerOptimalPosition(pitch, currentPlayer));

        when(pitch.calculateNetBlockedZoneLength(any())).thenReturn(100.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, -(Pitch.HALF_HEIGHT + 100) / 2), strategy.calculatePlayerOptimalPosition(pitch, currentPlayer));
    }

    @Test
    public void testCalculateOptimalBallInterceptPosition() {
        Player player = mock(Player.class);
        when(player.getSide()).thenReturn(Side.HOME);
        Ball ball = new Ball();
        ball.setPosition(new Vector2D(50, 100));
        ball.setVisibleTarget(new Vector2D(100, 150));
        Pitch pitch = mock(Pitch.class);
        when(pitch.getBall()).thenReturn(ball);
        when(pitch.getClosestPointInZone(any(), any())).thenCallRealMethod();
        when(pitch.isInsideZone(any(), any())).thenCallRealMethod();

        when(player.getPosition()).thenReturn(new Vector2D(100, 100));
        assertTrue(VectorUtils.equalsWithTolerance(new Vector2D(75, 125), strategy.calculateOptimalBallInterceptPosition(pitch, player)));

        when(player.getPosition()).thenReturn(new Vector2D(300, 200));
        assertTrue(VectorUtils.equalsWithTolerance(new Vector2D(100, 150), strategy.calculateOptimalBallInterceptPosition(pitch, player)));
    }

}
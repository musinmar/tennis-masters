package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import tm.lib.domain.core.Person;

public class MatchEngineTest {

    @Test
    public void testCanPlayerHitBall() {
        Person person = mock(Person.class);
        Player player = new Player(person, Side.HOME);
        Ball ball = new Ball();
        Vector2D ballPosition = new Vector2D(10, 20);
        ball.setPosition(ballPosition);
        
        player.setPosition(ballPosition);
        assertTrue(MatchEngine.canPlayerHitBall(player, ball));
        
        player.setPosition(ballPosition.add(new Vector2D(0, MatchEngineConstants.PLAYER_HAND_LENGTH / 2)));
        assertTrue(MatchEngine.canPlayerHitBall(player, ball));
        
        player.setPosition(ballPosition.add(new Vector2D(0, MatchEngineConstants.PLAYER_HAND_LENGTH)));
        assertTrue(MatchEngine.canPlayerHitBall(player, ball));
        
        player.setPosition(ballPosition.add(new Vector2D(0, MatchEngineConstants.PLAYER_HAND_LENGTH + 1)));
        assertFalse(MatchEngine.canPlayerHitBall(player, ball));
        
        player.setPosition(ballPosition.add(new Vector2D(MatchEngineConstants.PLAYER_HAND_LENGTH, 0)));
        assertTrue(MatchEngine.canPlayerHitBall(player, ball));
        
        player.setPosition(ballPosition.add(new Vector2D(MatchEngineConstants.PLAYER_HAND_LENGTH + 1, 0)));
        assertFalse(MatchEngine.canPlayerHitBall(player, ball));
    }
    
    @Test
    public void testCalculatePlayerOptimalPosition() {
        Pitch pitch = mock(Pitch.class);
        Player currentPlayer = mock(Player.class);
        Player oppositePlayer = mock(Player.class);
        when(pitch.getOppositePlayer(currentPlayer)).thenReturn(oppositePlayer);
        MatchEngine matchEngine = mock(MatchEngine.class);
        when(matchEngine.getPitch()).thenReturn(pitch);
        when(matchEngine.calculatePlayerOptimalPosition(currentPlayer)).thenCallRealMethod();
        
        // Optimal position for home player
        
        when(currentPlayer.getSide()).thenReturn(Side.HOME);
        
        when(matchEngine.calculateNetBlockedZoneLength(any())).thenReturn(0.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, Pitch.HALF_HEIGHT / 2), matchEngine.calculatePlayerOptimalPosition(currentPlayer));
        
        when(matchEngine.calculateNetBlockedZoneLength(any())).thenReturn(100.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, (Pitch.HALF_HEIGHT + 100) / 2), matchEngine.calculatePlayerOptimalPosition(currentPlayer));
        
        // Optimal position for away player
        
        when(currentPlayer.getSide()).thenReturn(Side.AWAY);
        
        when(matchEngine.calculateNetBlockedZoneLength(any())).thenReturn(0.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, -Pitch.HALF_HEIGHT / 2), matchEngine.calculatePlayerOptimalPosition(currentPlayer));
        
        when(matchEngine.calculateNetBlockedZoneLength(any())).thenReturn(100.0);
        assertEquals(new Vector2D(Pitch.WIDTH / 2, -(Pitch.HALF_HEIGHT + 100) / 2), matchEngine.calculatePlayerOptimalPosition(currentPlayer));
    }
    
    @Test
    public void testCalculateOptimalBallInterceptPosition() {
        Player player = mock(Player.class);
        when(player.getSide()).thenReturn(Side.HOME);
        Ball ball = new Ball();
        MatchEngine matchEngine = mock(MatchEngine.class);
        Pitch pitch = new Pitch(null, null, null);
        when(matchEngine.getPitch()).thenReturn(pitch);
        when(matchEngine.calculateOptimalBallInterceptPosition(player, ball)).thenCallRealMethod();
        
        ball.setPosition(new Vector2D(50, 100));
        ball.setVisibleTarget(new Vector2D(100, 150));
        when(player.getPosition()).thenReturn(new Vector2D(100, 100));
        assertTrue(VectorUtils.equalWithTolerance(new Vector2D(75, 125), matchEngine.calculateOptimalBallInterceptPosition(player, ball)));
        
        when(player.getPosition()).thenReturn(new Vector2D(300, 200));
        assertTrue(VectorUtils.equalWithTolerance(new Vector2D(100, 150), matchEngine.calculateOptimalBallInterceptPosition(player, ball)));
    }
}
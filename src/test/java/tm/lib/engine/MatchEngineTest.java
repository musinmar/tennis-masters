package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import tm.lib.domain.core.Person;

public class MatchEngineTest {

    @Test
    public void testCanPlayerHitBall() {
        Person person = Mockito.mock(Person.class);
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
}
package tm.lib.domain.core;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScoreTest {

    @Test
    public void testConstruction() {
        Score score1 = new Score(Arrays.asList(SetScore.of(1, 4), SetScore.of(3, 2)), null);
        assertEquals(Arrays.asList(SetScore.of(1, 4), SetScore.of(3, 2)), score1.getSets());
        assertEquals(null, score1.getAdditionalTime());
        assertEquals(2, score1.getSetCount());

        Score score2 = new Score(Arrays.asList(SetScore.of(4, 1)), SetScore.of(2, 3));
        assertEquals(Arrays.asList(SetScore.of(4, 1)), score2.getSets());
        assertEquals(SetScore.of(2, 3), score2.getAdditionalTime());
        assertEquals(1, score2.getSetCount());

        Score score3 = new Score(score1);
        assertEquals(Arrays.asList(SetScore.of(1, 4), SetScore.of(3, 2)), score3.getSets());
        assertEquals(null, score3.getAdditionalTime());
        assertEquals(2, score3.getSetCount());
    }

    @Test
    public void testReversed() {
        Score score1 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(3, 2)), null).reversed();
        assertEquals(Arrays.asList(SetScore.of(5, 1), SetScore.of(2, 3)), score1.getSets());
        assertEquals(null, score1.getAdditionalTime());
        assertEquals(2, score1.getSetCount());

        Score score2 = new Score(Arrays.asList(SetScore.of(4, 1)), SetScore.of(2, 3)).reversed();
        assertEquals(Arrays.asList(SetScore.of(1, 4)), score2.getSets());
        assertEquals(SetScore.of(3, 2), score2.getAdditionalTime());
        assertEquals(1, score2.getSetCount());
    }
    
    @Test
    public void testScoreBySets() {
        Score score1 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(3, 2)), null);
        assertEquals(SetScore.of(1, 1), score1.getScoreBySets());
        
        Score score2 = new Score(Arrays.asList(SetScore.of(5, 2), SetScore.of(1, 5)), SetScore.of(2, 4));
        assertEquals(SetScore.of(1, 2), score2.getScoreBySets());
        
        Score score3 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(4, 5)), null);
        assertEquals(SetScore.of(0, 2), score3.getScoreBySets());
    }
    
    @Test
    public void testScoreByGames() {
        Score score1 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(3, 2)), null);
        assertEquals(SetScore.of(4, 7), score1.getScoreByGames());
        
        Score score2 = new Score(Arrays.asList(SetScore.of(5, 2), SetScore.of(1, 5)), SetScore.of(2, 4));
        assertEquals(SetScore.of(8, 11), score2.getScoreByGames());
        
        Score score3 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(4, 5)), null);
        assertEquals(SetScore.of(5, 10), score3.getScoreByGames());
    }
    
    @Test
    public void testOtherMethods() {
        Score score1 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(3, 2)), null);
        assertFalse(score1.isFirstPlayerWinner());
        assertTrue(score1.isDraw());
        
        Score score2 = new Score(Arrays.asList(SetScore.of(5, 2), SetScore.of(1, 5)), SetScore.of(2, 4));
        assertFalse(score2.isFirstPlayerWinner());
        assertFalse(score2.isDraw());
        
        Score score3 = new Score(Arrays.asList(SetScore.of(1, 5), SetScore.of(4, 5)), null);
        assertFalse(score3.isFirstPlayerWinner());
        assertFalse(score3.isDraw());
        
        Score score4 = new Score(Arrays.asList(SetScore.of(5, 1), SetScore.of(5, 4)), null);
        assertTrue(score4.isFirstPlayerWinner());
        assertFalse(score4.isDraw());
    }
}

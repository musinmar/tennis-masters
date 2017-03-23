package tm.lib.domain.core;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatchScoreTest {

    @Test
    public void testConstruction() {
        MatchScore score1 = new MatchScore(Arrays.asList(BasicScore.of(1, 4), BasicScore.of(3, 2)), null);
        assertEquals(Arrays.asList(BasicScore.of(1, 4), BasicScore.of(3, 2)), score1.getSets());
        assertEquals(null, score1.getAdditionalTime());
        assertEquals(2, score1.getSetCount());

        MatchScore score2 = new MatchScore(Arrays.asList(BasicScore.of(4, 1)), BasicScore.of(2, 3));
        assertEquals(Arrays.asList(BasicScore.of(4, 1)), score2.getSets());
        assertEquals(BasicScore.of(2, 3), score2.getAdditionalTime());
        assertEquals(1, score2.getSetCount());

        MatchScore score3 = new MatchScore(score1);
        assertEquals(Arrays.asList(BasicScore.of(1, 4), BasicScore.of(3, 2)), score3.getSets());
        assertEquals(null, score3.getAdditionalTime());
        assertEquals(2, score3.getSetCount());
    }

    @Test
    public void testReversed() {
        MatchScore score1 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(3, 2)), null).reversed();
        assertEquals(Arrays.asList(BasicScore.of(5, 1), BasicScore.of(2, 3)), score1.getSets());
        assertEquals(null, score1.getAdditionalTime());
        assertEquals(2, score1.getSetCount());

        MatchScore score2 = new MatchScore(Arrays.asList(BasicScore.of(4, 1)), BasicScore.of(2, 3)).reversed();
        assertEquals(Arrays.asList(BasicScore.of(1, 4)), score2.getSets());
        assertEquals(BasicScore.of(3, 2), score2.getAdditionalTime());
        assertEquals(1, score2.getSetCount());
    }
    
    @Test
    public void testScoreBySets() {
        MatchScore score1 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(3, 2)), null);
        assertEquals(BasicScore.of(1, 1), score1.getScoreBySets());
        
        MatchScore score2 = new MatchScore(Arrays.asList(BasicScore.of(5, 2), BasicScore.of(1, 5)), BasicScore.of(2, 4));
        assertEquals(BasicScore.of(1, 2), score2.getScoreBySets());
        
        MatchScore score3 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(4, 5)), null);
        assertEquals(BasicScore.of(0, 2), score3.getScoreBySets());
    }
    
    @Test
    public void testScoreByGames() {
        MatchScore score1 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(3, 2)), null);
        assertEquals(BasicScore.of(4, 7), score1.getScoreByGames());
        
        MatchScore score2 = new MatchScore(Arrays.asList(BasicScore.of(5, 2), BasicScore.of(1, 5)), BasicScore.of(2, 4));
        assertEquals(BasicScore.of(8, 11), score2.getScoreByGames());
        
        MatchScore score3 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(4, 5)), null);
        assertEquals(BasicScore.of(5, 10), score3.getScoreByGames());
    }
    
    @Test
    public void testOtherMethods() {
        MatchScore score1 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(3, 2)), null);
        assertFalse(score1.isFirstPlayerWinner());
        assertTrue(score1.isDraw());
        
        MatchScore score2 = new MatchScore(Arrays.asList(BasicScore.of(5, 2), BasicScore.of(1, 5)), BasicScore.of(2, 4));
        assertFalse(score2.isFirstPlayerWinner());
        assertFalse(score2.isDraw());
        
        MatchScore score3 = new MatchScore(Arrays.asList(BasicScore.of(1, 5), BasicScore.of(4, 5)), null);
        assertFalse(score3.isFirstPlayerWinner());
        assertFalse(score3.isDraw());
        
        MatchScore score4 = new MatchScore(Arrays.asList(BasicScore.of(5, 1), BasicScore.of(5, 4)), null);
        assertTrue(score4.isFirstPlayerWinner());
        assertFalse(score4.isDraw());
    }
}

package tm.lib.engine;

import org.junit.Test;
import static org.junit.Assert.*;
import tm.lib.domain.core.SetScore;

public class MatchProgressTrackerTest {

    @Test
    public void testGroupMatchProgress() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, false);

        assertEquals(tracker.getScoreBySets(), SetScore.of(0, 0));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(0, 0));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(0, 1));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(2, 1));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(2, 4));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(2, 5));
        assertTrue(tracker.isSetFinished());

        assertEquals(tracker.getScoreBySets(), SetScore.of(0, 1));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(4, 4));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 4));
        assertTrue(tracker.isSetFinished());

        assertEquals(tracker.getScoreBySets(), SetScore.of(1, 1));
        assertTrue(tracker.isMatchFinished());
    }

    @Test
    public void testGroupMatchProgress2() {
        MatchProgressTracker tracker = new MatchProgressTracker(4, false);

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 0));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(1, 0));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 0));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(2, 0));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 0));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(3, 0));
        assertTrue(tracker.isMatchFinished());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFaultyConstruction() {
        new MatchProgressTracker(3, false);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalSetStart() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, false);

        tracker.startNewSet();
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);

        tracker.startNewSet();
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);

        tracker.startNewSet();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalSetStart2() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, false);

        tracker.startNewSet();
        tracker.addPoint(Side.HOME);
        tracker.startNewSet();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalAddPoint() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, false);
        tracker.addPoint(Side.HOME);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalAddPoint2() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, false);
        tracker.startNewSet();
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
    }

    @Test
    public void testPlayoffMatchProgress() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, true);

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 0));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(1, 0));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 0));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(2, 0));
        assertTrue(tracker.isMatchFinished());
    }

    @Test
    public void testPlayoffMatchProgress2() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, true);

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 0));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(1, 0));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(0, 5));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(1, 1));
        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        assertEquals(tracker.getServingSide(), Side.HOME);
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(0, 1));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(0, 2));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(1, 2));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(3, 2));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(4, 2));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(2, 1));
        assertTrue(tracker.isMatchFinished());
    }

    @Test
    public void testPlayoffMatchProgress3() {
        MatchProgressTracker tracker = new MatchProgressTracker(2, true);

        tracker.startNewSet();
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);

        tracker.startNewSet();
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);

        assertFalse(tracker.isMatchFinished());

        tracker.startNewSet();
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.AWAY);
        tracker.addPoint(Side.HOME);
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(3, 3));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(4, 3));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(4, 4));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.HOME);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 4));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 5));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.AWAY);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 6));
        assertFalse(tracker.isSetFinished());
        tracker.addPoint(Side.AWAY);
        assertEquals(tracker.getServingSide(), Side.HOME);
        assertEquals(tracker.getCurrentSetScore(), SetScore.of(5, 7));
        assertTrue(tracker.isSetFinished());
        assertEquals(tracker.getScoreBySets(), SetScore.of(1, 2));
        assertTrue(tracker.isMatchFinished());
    }
}

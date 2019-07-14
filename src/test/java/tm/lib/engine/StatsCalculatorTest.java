package tm.lib.engine;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import static org.mockito.Mockito.*;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Stadium;

public class StatsCalculatorTest {

    private Stadium stadium;
    private StatsCalculator statsCalculator;
    
    @Before
    public void beforeTest() {
        stadium = mock(Stadium.class);
        statsCalculator = new StatsCalculator(stadium);
    }
    
    @Test
    public void testMap() {
        assertEquals(20, StatsCalculator.map(0, 20, 30), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(25, StatsCalculator.map(50, 20, 30), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(30, StatsCalculator.map(100, 20, 30), VectorUtils.DEFAULT_TOLERANCE);
    }

    @Test
    public void testGetInvertedScaleModifier() {
        assertEquals(1, StatsCalculator.getInvertedScaleModifier(100, 2), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(2, StatsCalculator.getInvertedScaleModifier(50, 2), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(3, StatsCalculator.getInvertedScaleModifier(0, 2), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(1, StatsCalculator.getInvertedScaleModifier(100, 0.5), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(1.25, StatsCalculator.getInvertedScaleModifier(50, 0.5), VectorUtils.DEFAULT_TOLERANCE);
        assertEquals(1.5, StatsCalculator.getInvertedScaleModifier(0, 0.5), VectorUtils.DEFAULT_TOLERANCE);
    }

    @Test
    public void testGetTotalLyingTime() {
        Knight knight = mock(Knight.class);
        Player player = mock(Player.class);
        when(player.getKnight()).thenReturn(knight);

        when(knight.getDexterity()).thenReturn(100.0);
        when(player.getEnergy()).thenReturn(100.0);
        assertEquals(MatchEngineConstants.MIN_LYING_TIME, statsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);

        when(knight.getDexterity()).thenReturn(100.0);
        when(player.getEnergy()).thenReturn(0.0);
        assertEquals(MatchEngineConstants.MIN_LYING_TIME * (1 + MatchEngineConstants.LYING_TIME_ENERGY_MODIFIER), statsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);

        when(knight.getDexterity()).thenReturn(0.0);
        when(player.getEnergy()).thenReturn(100.0);
        assertEquals(MatchEngineConstants.MAX_LYING_TIME, statsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);

        when(knight.getDexterity()).thenReturn(0.0);
        when(player.getEnergy()).thenReturn(0.0);
        assertEquals(MatchEngineConstants.MAX_LYING_TIME * (1 + MatchEngineConstants.LYING_TIME_ENERGY_MODIFIER), statsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);
    }
}

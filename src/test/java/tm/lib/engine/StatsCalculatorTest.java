package tm.lib.engine;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import tm.lib.domain.core.Person;

public class StatsCalculatorTest {
    
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
        Person person = mock(Person.class);
        Player player = mock(Player.class);
        when(player.getPerson()).thenReturn(person);
        
        when(person.getDexterity()).thenReturn(100.0);
        when(player.getEnergy()).thenReturn(100.0);
        assertEquals(MatchEngineConstants.MIN_LYING_TIME, StatsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);
        
        when(person.getDexterity()).thenReturn(100.0);
        when(player.getEnergy()).thenReturn(0.0);
        assertEquals(MatchEngineConstants.MIN_LYING_TIME * (1 + MatchEngineConstants.LYING_TIME_ENERGY_MODIFIER), StatsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);
        
        when(person.getDexterity()).thenReturn(0.0);
        when(player.getEnergy()).thenReturn(100.0);
        assertEquals(MatchEngineConstants.MAX_LYING_TIME, StatsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);
        
        when(person.getDexterity()).thenReturn(0.0);
        when(player.getEnergy()).thenReturn(0.0);
        assertEquals(MatchEngineConstants.MAX_LYING_TIME * (1 + MatchEngineConstants.LYING_TIME_ENERGY_MODIFIER), StatsCalculator.getTotalLyingTime(player), VectorUtils.DEFAULT_TOLERANCE);
    }
}
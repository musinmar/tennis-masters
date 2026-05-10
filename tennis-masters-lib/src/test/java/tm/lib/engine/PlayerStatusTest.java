package tm.lib.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerStatusTest {

    private static final double EPSILON = 1e-8;

    @Test
    public void testChangeEnergy() {
        PlayerState state = new PlayerState();
        assertEquals(100, state.getEnergy(), EPSILON);
        state.changeEnergy(10);
        assertEquals(100, state.getEnergy(), EPSILON);
        state.changeEnergy(-10);
        assertEquals(90, state.getEnergy(), EPSILON);
        state.changeEnergy(20);
        assertEquals(100, state.getEnergy(), EPSILON);
        state.changeEnergy(-70);
        assertEquals(30, state.getEnergy(), EPSILON);
        state.changeEnergy(30);
        assertEquals(60, state.getEnergy(), EPSILON);
        state.changeEnergy(-70);
        assertEquals(0, state.getEnergy(), EPSILON);
    }

}

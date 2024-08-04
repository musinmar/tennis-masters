package tm.lib.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    private static final double EPSILON = 1e-8;

    @Test
    public void testChangeEnergy() {
        Player player = new Player(null, Side.HOME);
        assertEquals(100, player.getEnergy(), EPSILON);
        player.changeEnergy(10);
        assertEquals(100, player.getEnergy(), EPSILON);
        player.changeEnergy(-10);
        assertEquals(90, player.getEnergy(), EPSILON);
        player.changeEnergy(20);
        assertEquals(100, player.getEnergy(), EPSILON);
        player.changeEnergy(-70);
        assertEquals(30, player.getEnergy(), EPSILON);
        player.changeEnergy(30);
        assertEquals(60, player.getEnergy(), EPSILON);
        player.changeEnergy(-70);
        assertEquals(0, player.getEnergy(), EPSILON);
    }

}

package tm.lib.domain.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicScoreTest {

    @Test
    public void testNormalization() {
        BasicScore setScore1 = BasicScore.of(0, 0);
        assertEquals(BasicScore.of(0, 0), setScore1.normalized());

        BasicScore setScore2 = BasicScore.of(5, 0);
        assertEquals(BasicScore.of(1, 0), setScore2.normalized());

        BasicScore setScore3 = BasicScore.of(2, 3);
        assertEquals(BasicScore.of(0, 1), setScore3.normalized());

        BasicScore setScore4 = BasicScore.of(5, 5);
        assertEquals(BasicScore.of(0, 0), setScore4.normalized());
    }

    @Test
    public void testReversed() {
        BasicScore setScore1 = BasicScore.of(0, 0);
        assertEquals(BasicScore.of(0, 0), setScore1.reversed());

        BasicScore setScore2 = BasicScore.of(5, 0);
        assertEquals(BasicScore.of(0, 5), setScore2.reversed());

        BasicScore setScore3 = BasicScore.of(2, 3);
        assertEquals(BasicScore.of(3, 2), setScore3.reversed());

        BasicScore setScore4 = BasicScore.of(5, 5);
        assertEquals(BasicScore.of(5, 5), setScore4.reversed());
    }

    @Test
    public void testSum() {
        BasicScore setScore1 = BasicScore.of(2, 5);
        BasicScore setScore2 = BasicScore.of(3, 1);
        assertEquals(BasicScore.of(5, 6), setScore1.summedWith(setScore2));
    }
}

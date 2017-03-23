package tm.lib.domain.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class SetScoreTest {

    @Test
    public void testNormalization() {
        SetScore setScore1 = SetScore.of(0, 0);
        assertEquals(SetScore.of(0, 0), setScore1.normalized());

        SetScore setScore2 = SetScore.of(5, 0);
        assertEquals(SetScore.of(1, 0), setScore2.normalized());

        SetScore setScore3 = SetScore.of(2, 3);
        assertEquals(SetScore.of(0, 1), setScore3.normalized());

        SetScore setScore4 = SetScore.of(5, 5);
        assertEquals(SetScore.of(0, 0), setScore4.normalized());
    }

    @Test
    public void testReversed() {
        SetScore setScore1 = SetScore.of(0, 0);
        assertEquals(SetScore.of(0, 0), setScore1.reversed());

        SetScore setScore2 = SetScore.of(5, 0);
        assertEquals(SetScore.of(0, 5), setScore2.reversed());

        SetScore setScore3 = SetScore.of(2, 3);
        assertEquals(SetScore.of(3, 2), setScore3.reversed());

        SetScore setScore4 = SetScore.of(5, 5);
        assertEquals(SetScore.of(5, 5), setScore4.reversed());
    }

    @Test
    public void testSum() {
        SetScore setScore1 = SetScore.of(2, 5);
        SetScore setScore2 = SetScore.of(3, 1);
        assertEquals(SetScore.of(5, 6), setScore1.summedWith(setScore2));
    }
}

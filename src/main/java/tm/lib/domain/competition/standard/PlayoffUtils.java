package tm.lib.domain.competition.standard;

import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class PlayoffUtils {

    public static List<Knight> drawPlayersInPairs(List<Knight> knights, boolean checkNoSameNationPairs) {
        ArrayList<Knight> knightsToShuffle = new ArrayList<>(knights);

        boolean done = false;
        while (!done) {
            shuffle(knightsToShuffle);
            if (!checkNoSameNationPairs) {
                break;
            } else {
                done = true;
                for (int i = 0; i < knightsToShuffle.size(); i += 2) {
                    if (knightsToShuffle.get(i).getNation() == knightsToShuffle.get(i + 1).getNation()) {
                        done = false;
                        break;
                    }
                }
            }
        }

        return knightsToShuffle;
    }

}

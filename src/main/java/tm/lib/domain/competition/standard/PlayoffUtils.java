package tm.lib.domain.competition.standard;

import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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

    public static List<Knight> drawPlayersInPairsFromGroupResults(GroupStageResult groupResults) {
        if (groupResults.getGroupCount() == 2) {
            return asList(
                    groupResults.getGroupPosition(0, 0),
                    groupResults.getGroupPosition(1, 1),
                    groupResults.getGroupPosition(1, 0),
                    groupResults.getGroupPosition(0, 1)
            );
        } else if (groupResults.getGroupCount() == 4) {
            return asList(
                    groupResults.getGroupPosition(0, 0),
                    groupResults.getGroupPosition(1, 1),
                    groupResults.getGroupPosition(2, 0),
                    groupResults.getGroupPosition(3, 1),
                    groupResults.getGroupPosition(1, 0),
                    groupResults.getGroupPosition(0, 1),
                    groupResults.getGroupPosition(3, 0),
                    groupResults.getGroupPosition(2, 1)
            );
        } else {
            throw new IllegalArgumentException("Can't make pairs from " + groupResults.getGroupCount() + " groups");
        }
    }

}

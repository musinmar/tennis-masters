package tm.lib.domain.world;

import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static tm.lib.domain.world.PlayOffUtils.drawPlayersInPairs;

public class GroupStageUtils {
    public static List<List<Knight>> makeSeededGroupsTwoByFour(List<Knight> knights, EloRating eloRating) {
        List<Knight> sortedKnights = new ArrayList<>(knights);
        eloRating.sortPersonsByRating(sortedKnights);
        List<Knight> top = new ArrayList<>(sortedKnights.subList(0, 4));
        List<Knight> bottom = new ArrayList<>(sortedKnights.subList(4, 8));
        top = drawPlayersInPairs(top, false);
        bottom = drawPlayersInPairs(bottom, false);
        return asList(
                asList(
                        top.get(0),
                        top.get(1),
                        bottom.get(0),
                        bottom.get(1)),
                asList(
                        top.get(2),
                        top.get(3),
                        bottom.get(2),
                        bottom.get(3)
                ));
    }

    public static List<List<Knight>> performPotBasedDraw(List<Knight> players) {
        List<List<Knight>> pots = new ArrayList<>();
        var potCount = 4;
        var potSize = players.size() / 4;
        for (int i = 0; i < potCount; ++i) {
            pots.add(new ArrayList<>());
        }
        for (int i = 0; i < players.size(); i++) {
            pots.get(i / potSize).add(players.get(i));
        }
        for (int i = 0; i < potCount; ++i) {
            Collections.shuffle(pots.get(i));
        }
        List<List<Knight>> groups = new ArrayList<>();
        for (int i = 0; i < potSize; ++i) {
            final int groupIndex = i;
            List<Knight> group = pots.stream().map(pot -> pot.get(groupIndex)).collect(Collectors.toList());
            groups.add(group);
        }
        return groups;
    }
}

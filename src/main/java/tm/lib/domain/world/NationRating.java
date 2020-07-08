package tm.lib.domain.world;

import org.apache.commons.lang3.mutable.MutableDouble;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class NationRating {
    private static final int SEASON_COUNT = 5;

    private Map<Nation, PointHistoryItem> pointHistory = new HashMap<>();
    private List<Nation> ranking;
    private Map<Nation, MutableDouble> seasonPoints = new HashMap<>();
    private Map<Knight, Double> playerSeasonPoints = new HashMap<>();

    private static class PointHistoryItem {
        double[] seasons = new double[SEASON_COUNT];

        PointHistoryItem(double defaultPoints) {
            for (int i = 0; i < SEASON_COUNT; i++) {
                seasons[i] = defaultPoints;
            }
        }

//        NationRatingDto.PointHistoryItemDto toDto() {
//            NationRatingDto.PointHistoryItemDto itemDto = new NationRatingDto.PointHistoryItemDto();
//            itemDto.setSeasons(seasons);
//            return itemDto;
//        }

//        static PointHistoryItem fromDto(NationRatingDto.PointHistoryItemDto itemDto) {
//            PointHistoryItem item = new PointHistoryItem(0);
//            item.seasons = Arrays.copyOf(itemDto.getSeasons(), SEASON_COUNT);
//            return item;
//        }
    }

    public NationRating() {
        for (Nation nation : Nation.values()) {
            seasonPoints.put(nation, new MutableDouble(0));
        }
    }

    public Nation getRankedNation(int rank) {
        return ranking.get(rank);
    }

    public void initDefault() {
        pointHistory.put(Nation.ALMAGEST, new PointHistoryItem(3.5));
        pointHistory.put(Nation.BELLEROFON, new PointHistoryItem(3));
        pointHistory.put(Nation.GALILEO, new PointHistoryItem(2.5));
        pointHistory.put(Nation.KAMELEOPARD, new PointHistoryItem(4.5));
        pointHistory.put(Nation.OBERON_22, new PointHistoryItem(4));
    }

//    public NationRatingDto toDto() {
//        NationRatingDto nationRatingDto = new NationRatingDto();
//        nationRatingDto.setPointHistory(pointHistory.entrySet().stream()
//                .collect(toMap(Map.Entry::getKey, e -> e.getValue().toDto())));
//        return nationRatingDto;
//    }
//
//    public static NationRating fromDto(NationRatingDto nationRatingDto) {
//        NationRating nationRating = new NationRating();
//        nationRating.pointHistory = nationRatingDto.getPointHistory().entrySet().stream()
//                .collect(toMap(Map.Entry::getKey, e -> PointHistoryItem.fromDto(e.getValue())));
//        return nationRating;
//    }

//    public void printPointHistory() {
//        println("Federations table");
//        println();
//        int index = 0;
//        for (Nation nation : Nation.values()) {
//            PointHistoryItem pointHistoryItem = pointHistory.get(nation);
//            print(String.format("%d) %-11s", index + 1, nation.getName()));
//            Arrays.stream(pointHistoryItem.seasons).forEach(d -> print(String.format("%7.2f", d)));
//            println();
//            ++index;
//        }
//        println();
//    }

    public void calculateRankingsAndPrint() {
        double[] sums = Arrays.stream(Nation.values())
                .map(pointHistory::get)
                .mapToDouble(item -> {
                    return Arrays.stream(item.seasons).sum();
                })
                .toArray();

        ranking = Arrays.stream(Nation.values())
                .sorted(comparingDouble((Nation nation) -> sums[nation.ordinal()]).reversed())
                .collect(toList());

//        println("Start of season federations ranking:");
//        println();
//        for (int i = 0; i < ranking.size(); ++i) {
//            Nation nation = ranking.get(i);
//            println(String.format("%d) %-11s %7.2f", i + 1, nation.getName(), sums[nation.getId()]));
//        }
//        readln();
    }

    public void advanceYear() {
        double sum = seasonPoints.values().stream().mapToDouble(MutableDouble::doubleValue).sum();
//        println("Total points received this season: %.2f", sum);
//        playerSeasonPoints.entrySet().stream()
//                .sorted(comparing(e -> e.getKey().getId()))
//                .forEach(e -> println("%s: %.2f", e.getKey().getPlayerName(), e.getValue()));
//        println();

        int[] countCoefficient = new int[]{5, 5, 5, 4, 4};
        for (int i = 0; i < Nation.COUNT; i++) {
            Nation nation = getRankedNation(i);
            PointHistoryItem item = pointHistory.get(nation);
            for (int j = SEASON_COUNT - 1; j >= 1; --j) {
                item.seasons[j] = item.seasons[j - 1];
            }
            item.seasons[0] = seasonPoints.get(nation).doubleValue() / countCoefficient[i];
        }
    }

    public void updateRatings(Knight p1, Knight p2, BasicScore r, int points) {
        if (r.v1 > r.v2) {
            addSeasonPoints(p1, points);
        } else if (r.v2 > r.v1) {
            addSeasonPoints(p2, points);
        } else {
            addSeasonPoints(p1, points / 2.0);
            addSeasonPoints(p2, points / 2.0);
        }
    }

    private void addSeasonPoints(Knight p, double points) {
        if (points == 0) {
            return;
        }
        playerSeasonPoints.put(p, playerSeasonPoints.computeIfAbsent(p, player -> 0.) + points);
        addSeasonPoints(p.getNation(), points);
    }

    private void addSeasonPoints(Nation nation, double points) {
        seasonPoints.get(nation).add(points);
    }
}
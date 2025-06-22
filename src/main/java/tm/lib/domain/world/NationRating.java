package tm.lib.domain.world;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class NationRating {
    public static final int SEASON_COUNT = 5;

    private Map<Nation, PointHistory> pointHistoryPerNation = new HashMap<>();
    private List<Nation> nationRanking;
    private Map<Nation, Double> nationSeasonPoints = new HashMap<>();
    private Map<Knight, Double> playerSeasonPoints = new HashMap<>();

    @Getter
    @Setter
    public static class PointHistory {
        public List<Double> seasons = emptyList();
    }

    public void initializeDefault() {
        pointHistoryPerNation.put(Nation.ALMAGEST, createDefaultPointHistory(3.5));
        pointHistoryPerNation.put(Nation.BELLEROFON, createDefaultPointHistory(3));
        pointHistoryPerNation.put(Nation.GALILEO, createDefaultPointHistory(2.5));
        pointHistoryPerNation.put(Nation.KAMELEOPARD, createDefaultPointHistory(4.5));
        pointHistoryPerNation.put(Nation.OBERON_22, createDefaultPointHistory(4));
        updateNationRanking();
        resetSeasonPoints();
    }

    private PointHistory createDefaultPointHistory(double pointsPerSeason) {
        var result = new PointHistory();
        var seasons = new ArrayList<>(Collections.nCopies(SEASON_COUNT, pointsPerSeason));
        result.setSeasons(seasons);
        return result;
    }

    public void resetSeasonPoints() {
        for (Nation nation : Nation.values()) {
            nationSeasonPoints.put(nation, 0.0);
        }
        playerSeasonPoints.clear();
    }

    public PointHistory getPointHistoryPerNation(Nation nation) {
        return pointHistoryPerNation.get(nation);
    }

    public Double getCurrentSeasonPoints(Nation nation) {
        return nationSeasonPoints.get(nation);
    }

    public Double getCurrentSeasonValue(Nation nation) {
        return getCurrentSeasonPoints(nation) / getCountCoefficient(getNationRanking(nation));
    }

    public Double getTotalPoints(Nation nation) {
        double historySum = pointHistoryPerNation.get(nation).seasons.stream().mapToDouble(v -> v).sum();
        return historySum + getCurrentSeasonValue(nation);
    }

    public int getNationRanking(Nation nation) {
        return nationRanking.indexOf(nation);
    }

    public Nation getRankedNation(int rank) {
        return nationRanking.get(rank);
    }

    public int getCountCoefficient(int ranking) {
        if (ranking >= 0 && ranking < 3) {
            return 5;
        } else if (ranking < 5) {
            return 4;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void printPointHistory(WorldLogger logger) {
        logger.println("Рейтинг наций");
        logger.println();
        int index = 0;
        for (Nation nation : Nation.values()) {
            PointHistory pointHistoryItem = pointHistoryPerNation.get(nation);
            logger.print(String.format("%d) %-11s", index + 1, nation.getName()));
            pointHistoryItem.seasons.stream().forEach(d -> logger.print(String.format("%7.2f", d)));
            logger.println();
            ++index;
        }
        logger.println();
    }

    public void updateNationRanking() {
        double[] sums = Arrays.stream(Nation.values())
                .map(pointHistoryPerNation::get)
                .mapToDouble(item -> {
                    return item.seasons.stream().mapToDouble(v -> v).sum();
                })
                .toArray();

        nationRanking = Arrays.stream(Nation.values())
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
//        double sum = nationSeasonPoints.values().stream().mapToDouble(MutableDouble::doubleValue).sum();
//        println("Total points received this season: %.2f", sum);
//        playerSeasonPoints.entrySet().stream()
//                .sorted(comparing(e -> e.getKey().getId()))
//                .forEach(e -> println("%s: %.2f", e.getKey().getPlayerName(), e.getValue()));
//        println();

        for (Nation nation : Nation.values()) {
            PointHistory item = pointHistoryPerNation.get(nation);
            var newSeasons = new ArrayList<Double>();
            newSeasons.add(getCurrentSeasonValue(nation));
            newSeasons.addAll(item.seasons.subList(0, SEASON_COUNT - 2));
            var newPointHistory = new PointHistory();
            newPointHistory.setSeasons(newSeasons);
            pointHistoryPerNation.put(nation, newPointHistory);
        }
        updateNationRanking();
        resetSeasonPoints();
    }

    public void updateRatings(Knight k1, Knight k2, BasicScore r, int points) {
        if (r.v1 > r.v2) {
            addSeasonPoints(k1, points);
        } else if (r.v2 > r.v1) {
            addSeasonPoints(k2, points);
        } else {
            addSeasonPoints(k1, points / 2.0);
            addSeasonPoints(k2, points / 2.0);
        }
    }

    private void addSeasonPoints(Knight knight, double points) {
        playerSeasonPoints.put(knight, playerSeasonPoints.computeIfAbsent(knight, player -> 0.) + points);
        addSeasonPoints(knight.getNation(), points);
    }

    private void addSeasonPoints(Nation nation, double points) {
        var newValue = nationSeasonPoints.get(nation) + points;
        nationSeasonPoints.put(nation, newValue);
    }
}
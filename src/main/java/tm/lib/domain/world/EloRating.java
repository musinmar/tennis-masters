package tm.lib.domain.world;

import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingDouble;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class EloRating {
    private static final double K_FACTOR = 20;
    private static final double AVERAGE_RATING = 500;

    private Map<Knight, Rating> ratings;

    private static class Rating {
        private double value;
        private double valuePreviousYear;

        Rating(double rating) {
            this.value = rating;
            this.valuePreviousYear = rating;
        }
    }

    public EloRating(List<Knight> players) {
        ratings = players.stream().collect(toMap(identity(), p -> new Rating(AVERAGE_RATING)));
    }

//    public EloRatingDto toDto() {
//        EloRatingDto eloRatingDto = new EloRatingDto();
//        List<EloRatingDto.ItemDto> itemDtos = ratings.entrySet().stream()
//                .map(e -> new EloRatingDto.ItemDto(e.getKey().getId(), e.getValue().value))
//                .collect(toList());
//        eloRatingDto.setItems(itemDtos);
//        return eloRatingDto;
//    }
//
//    public static EloRating fromDto(EloRatingDto eloRatingDto, List<Knight> Persons) {
//        EloRating eloRating = new EloRating(Persons);
//        eloRating.ratings = eloRatingDto.getItems().stream()
//                .collect(toMap(item -> Persons.get(item.getPersonId()), item -> new Rating(item.getPoints())));
//        eloRating.normalize();
//        eloRating.savePointsAsLastYearPoints();
//        return eloRating;
//    }

    private void normalize() {
        Collection<Rating> allRatings = ratings.values();
        double ratingSum = allRatings.stream().mapToDouble(i -> i.value).sum();
        double dif = ratingSum - AVERAGE_RATING * ratings.size();
        if (Math.abs(dif) > 1) {
            allRatings.forEach(i -> i.value -= dif / ratings.size());
        }
    }

    public List<Knight> getPersonsByRating() {
        return ratings.keySet().stream()
                .sorted(comparingDouble(this::getRating).reversed())
                .collect(toList());
    }

    public void updateRatings(Knight p1, Knight p2, BasicScore r) {
        double sum = r.v1 + r.v2;
        double r1 = r.v1 / sum;
        double r2 = r.v2 / sum;

        Rating item1 = ratings.get(p1);
        Rating item2 = ratings.get(p2);
        double rat1 = item1.value;
        double rat2 = item2.value;
        item1.value += calculateRatingChange(rat1, rat2, r1);
        item2.value += calculateRatingChange(rat2, rat1, r2);
    }

    public double getRating(Knight player) {
        return ratings.get(player).value;
    }

    public double getRatingChange(Knight player) {
        Rating rating = ratings.get(player);
        return rating.value - rating.valuePreviousYear;
    }

    public void sortPersonsByRating(List<Knight> players) {
        players.sort(comparingDouble(this::getRating).reversed());
    }

    public void print(PrintWriter writer, boolean withDifs) {
        int maxNameLength = ratings.keySet().stream()
                .map(Knight::getFullName)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        String formatString = "%-2d: %-" + (maxNameLength + 1) + "s %-7.2f";

        List<Map.Entry<Knight, Rating>> sortedItems = ratings.entrySet().stream()
                .sorted(comparingDouble((Map.Entry<Knight, Rating> entry) -> entry.getValue().value).reversed())
                .collect(toList());

        for (int i = 0; i < sortedItems.size(); i++) {
            Map.Entry<Knight, Rating> entry = sortedItems.get(i);
            writer.print(String.format(formatString, (i + 1), entry.getKey().getFullName(), entry.getValue().value));
            if (withDifs) {
                writer.println(String.format("   %+5.2f", entry.getValue().value - entry.getValue().valuePreviousYear));
            } else {
                writer.println();
            }
        }
    }

    private void savePointsAsLastYearPoints() {
        ratings.values().forEach(item -> item.valuePreviousYear = item.value);
    }

    public void resetRating(Knight player) {
//        double[][] data = ratings.entrySet().stream()
//                .map(entry -> new double[]{entry.getKey().getLevel(), entry.getValue().value})
//                .toArray(double[][]::new);
//
//        SimpleRegression simpleRegression = new SimpleRegression();
//        simpleRegression.addData(data);

        Rating item = ratings.get(player);
//        item.value = simpleRegression.predict(Knight.getLevel());
        item.value = AVERAGE_RATING;
        normalize();
    }

    private static double calculateRatingChange(double rat1, double rat2, double res) {
        double dif = rat2 - rat1;
        double e = 1 / (1 + Math.exp(dif / 400 * Math.log(10)));
        return K_FACTOR * (res - e);
    }
}

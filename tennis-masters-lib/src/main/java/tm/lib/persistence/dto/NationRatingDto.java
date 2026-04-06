package tm.lib.persistence.dto;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Nation;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class NationRatingDto {
    private Map<Nation, PointHistoryItemDto> pointHistory;
    private List<Nation> nationRanking;
    private Map<Nation, Double> nationSeasonPoints;
    private Map<Integer, Double> playerSeasonPoints;

    @Getter
    @Setter
    public static class PointHistoryItemDto {
        private List<Double> seasons;
    }
}

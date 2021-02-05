package tm.lib.domain.world.dto;

import tm.lib.domain.core.Nation;

import java.util.Map;

public class NationRatingDto {
    private Map<Nation, PointHistoryItemDto> pointHistory;

    public static class PointHistoryItemDto {
        private double[] seasons;

        public double[] getSeasons() {
            return seasons;
        }

        public void setSeasons(double[] seasons) {
            this.seasons = seasons;
        }
    }

    public Map<Nation, PointHistoryItemDto> getPointHistory() {
        return pointHistory;
    }

    public void setPointHistory(Map<Nation, PointHistoryItemDto> pointHistory) {
        this.pointHistory = pointHistory;
    }
}

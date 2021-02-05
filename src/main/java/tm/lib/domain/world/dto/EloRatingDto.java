package tm.lib.domain.world.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class EloRatingDto {
    private List<ItemDto> items;

    @JsonIgnoreProperties("pointsLastYear")
    public static class ItemDto {
        private int playerId;
        private double points;

        public ItemDto() {
        }

        public ItemDto(int playerId, double points) {
            this.playerId = playerId;
            this.points = points;
        }

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public double getPoints() {
            return points;
        }

        public void setPoints(double points) {
            this.points = points;
        }
    }

    public List<ItemDto> getItems() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        this.items = items;
    }
}

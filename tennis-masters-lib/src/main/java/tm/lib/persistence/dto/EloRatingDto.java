package tm.lib.persistence.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EloRatingDto {
    private List<ItemDto> items;

    @Getter
    @Setter
    public static class ItemDto {
        private int playerId;
        private double points;
        private double pointsPreviousYear;
    }
}

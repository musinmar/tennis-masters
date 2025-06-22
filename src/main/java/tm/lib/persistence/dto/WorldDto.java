package tm.lib.persistence.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorldDto {

    private int year;
    private boolean isSeasonFinished;
    private List<KnightDto> knights;
    private EloRatingDto eloRating;
    private NationRatingDto nationRating;
}

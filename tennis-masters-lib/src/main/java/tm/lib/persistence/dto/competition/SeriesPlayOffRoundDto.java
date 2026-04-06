package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeriesPlayOffRoundDto extends SimpleCompetitionDto {
    public static final String TYPE = "seriesPlayOffRound";

    private int winCount;
}
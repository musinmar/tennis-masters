package tm.lib.persistence.dto;

import lombok.Getter;
import lombok.Setter;
import tm.lib.persistence.dto.competition.AbstractCompetitionDto;
import tm.lib.persistence.dto.core.KnightDto;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WorldDto {
    private int year;
    private boolean isSeasonFinished;
    private List<KnightDto> knights;
    private EloRatingDto eloRating;
    private NationRatingDto nationRating;
    private AbstractCompetitionDto seasonCompetition;
    private Map<String, Integer> competitionPointValues;
}

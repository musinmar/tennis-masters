package tm.lib.persistence.dto;

import lombok.Getter;
import lombok.Setter;
import tm.lib.persistence.dto.competition.AbstractCompetitionDto;

import java.util.Map;

@Getter
@Setter
public class WorldDto {
    private int year;
    private boolean isSeasonFinished;
    private KnightRepositoryDto knightRepository;
    private EloRatingDto eloRating;
    private NationRatingDto nationRating;
    private AbstractCompetitionDto seasonCompetition;
    private Map<String, Integer> competitionPointValues;
}

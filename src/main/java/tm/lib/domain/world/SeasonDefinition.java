package tm.lib.domain.world;

import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.definition.CompetitionDefinition;

import java.util.Map;

public record SeasonDefinition(
        CompetitionDefinition rootCompetitionDefinition,
        Map<String, Integer> competitionPoints
) {
}

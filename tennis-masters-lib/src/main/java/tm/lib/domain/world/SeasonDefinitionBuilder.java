package tm.lib.domain.world;

import tm.lib.domain.competition.definition.CompetitionDefinition;
import tm.lib.domain.competition.definition.MultiStageCompetitionDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class SeasonDefinitionBuilder {
    private int year;
    private List<CompetitionDefinition> competitions = new ArrayList<>();
    private Map<String, Integer> competitionPoints = new HashMap<>();

    public SeasonDefinitionBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    public SeasonDefinitionBuilder withCompetitions(List<CompetitionDefinition> competitions) {
        this.competitions.addAll(competitions);
        return this;
    }

    public SeasonDefinitionBuilder withCompetition(CompetitionDefinition competition) {
        return withCompetitions(singletonList(competition));
    }

    public SeasonDefinitionBuilder withCompetitionPoints(Map<String, Integer> competitionPoints) {
        this.competitionPoints = competitionPoints;
        return this;
    }

    public SeasonDefinition build() {
        var rootCompetition = new MultiStageCompetitionDefinition();
        rootCompetition.setId("SEASON");
        rootCompetition.setName("Season " + (year + 1));
        rootCompetition.setStages(competitions);
        return new SeasonDefinition(rootCompetition, competitionPoints);
    }
}

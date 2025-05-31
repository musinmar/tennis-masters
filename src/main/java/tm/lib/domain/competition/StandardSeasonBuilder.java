package tm.lib.domain.competition;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;

import java.util.Collections;

public class StandardSeasonBuilder {

    public static CompetitionDefinition buildSeasonCompetitionDefinition() {
        var competitions = Collections.<CompetitionDefinition>emptyList();
        return new MultiStageCompetitionDefinition("SEASON", "Сезон", competitions);
    }
}

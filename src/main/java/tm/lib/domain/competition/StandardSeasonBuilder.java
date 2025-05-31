package tm.lib.domain.competition;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayoffStageDefinition;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;

import java.util.Arrays;
import java.util.List;

public class StandardSeasonBuilder {

    public static CompetitionDefinition buildSeasonCompetitionDefinition() {
        var competitions = List.of(buildStandardCompetition());
        return new MultiStageCompetitionDefinition("SEASON", "Сезон", competitions);
    }

    public static CompetitionDefinition buildStandardCompetition() {
        var groupStage = new GroupStageDefinition("GS",
                "Групповой этап",
                2,
                4);
        var playOffStage = new PlayoffStageDefinition("PO",
                "Плэй-офф",
                PlayoffStageConfiguration.builder().playerCount(4).build());
        return new MultiStageCompetitionDefinition("SC",
                "Стандартный турнир",
                Arrays.asList(groupStage, playOffStage));
    }
}

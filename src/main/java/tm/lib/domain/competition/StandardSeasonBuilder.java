package tm.lib.domain.competition;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayoffStageDefinition;
import tm.lib.domain.competition.base.triggers.SeasonStartTriggerTime;
import tm.lib.domain.competition.base.triggers.SeedingRules.RandomSelection;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StandardSeasonBuilder {

    public static CompetitionDefinition buildSeasonCompetitionDefinition() {
        var competitions = List.of(buildStandardCompetition());
        return new MultiStageCompetitionDefinition("SEASON", "Сезон", competitions, Optional.empty());
    }

    public static CompetitionDefinition buildStandardCompetition() {
        var groupStage = new GroupStageDefinition("GS",
                "Групповой этап",
                2,
                4,
                Optional.of(new SeedingTrigger(new SeasonStartTriggerTime(), new RandomSelection())));
        var playOffStage = new PlayoffStageDefinition("PO",
                "Плэй-офф",
                PlayoffStageConfiguration.builder().playerCount(4).build(),
                Optional.empty());
        return new MultiStageCompetitionDefinition("SC",
                "Стандартный турнир",
                Arrays.asList(groupStage, playOffStage),
                Optional.empty());
    }
}

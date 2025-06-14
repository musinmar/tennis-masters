package tm.lib.domain.competition;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayOffStageDefinition;
import tm.lib.domain.competition.base.triggers.SeedingRules;
import tm.lib.domain.competition.base.triggers.SeedingRules.AllFromNationSeedingRule;
import tm.lib.domain.competition.base.triggers.SeedingRules.RandomSelection;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.competition.base.triggers.TriggerTimes;
import tm.lib.domain.competition.base.triggers.TriggerTimes.SeasonStartTriggerTime;
import tm.lib.domain.competition.standard.PlayOffStageConfiguration;
import tm.lib.domain.core.Nation;

import java.util.Arrays;
import java.util.Optional;

public class StandardSeasonBuilder {

    public static CompetitionDefinition buildSeasonCompetitionDefinition() {
        var competitions = Arrays.stream(Nation.values()).map(StandardSeasonBuilder::buildNationalCup).toList();
        return new MultiStageCompetitionDefinition("SEASON", "Сезон", competitions, Optional.empty());
    }

    public static CompetitionDefinition buildNationalCup(Nation nation) {
        var nationSeeding = new SeedingTrigger(new SeasonStartTriggerTime(),
                new AllFromNationSeedingRule(nation));
        return new GroupStageDefinition(nation.name().toUpperCase(), "Кубок " + nation.getName(),
                1, 6, Optional.of(nationSeeding));
    }

    public static CompetitionDefinition buildStandardCompetition() {
        var groupStage = new GroupStageDefinition("GS",
                "Групповой этап",
                4,
                4,
                Optional.of(new SeedingTrigger(new SeasonStartTriggerTime(), new RandomSelection())));
        var groupStagePath = "../GS";
        var playOffSeedingTrigger = new SeedingTrigger(
                new TriggerTimes.CompetitionEndedTriggerTime(groupStagePath),
                new SeedingRules.GroupStageToPlayOff(groupStagePath));
        var playOffStage = new PlayOffStageDefinition("PO",
                "Плэй-офф",
                PlayOffStageConfiguration.builder().playerCount(8).build(),
                Optional.of(playOffSeedingTrigger));
        return new MultiStageCompetitionDefinition("SC",
                "Стандартный турнир",
                Arrays.asList(groupStage, playOffStage),
                Optional.empty());
    }
}

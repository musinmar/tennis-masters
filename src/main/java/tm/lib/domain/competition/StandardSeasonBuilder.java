package tm.lib.domain.competition;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayOffStageDefinition;
import tm.lib.domain.competition.base.triggers.SeedingRules.AllFromNationSeedingRule;
import tm.lib.domain.competition.base.triggers.SeedingRules.BasedOnNationalRankingSeedingRule;
import tm.lib.domain.competition.base.triggers.SeedingRules.BasedOnNationalRankingSeedingRule.PreviousRound;
import tm.lib.domain.competition.base.triggers.SeedingRules.GroupStageToPlayOff;
import tm.lib.domain.competition.base.triggers.SeedingRules.RandomSelection;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.competition.base.triggers.TriggerTimes.CompetitionEndedTriggerTime;
import tm.lib.domain.competition.base.triggers.TriggerTimes.SeasonStartTriggerTime;
import tm.lib.domain.competition.standard.PlayOffStageConfiguration;
import tm.lib.domain.core.Nation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.base.Competition.PARENT;
import static tm.lib.domain.competition.base.Competition.buildPath;
import static tm.lib.domain.competition.base.triggers.SeedingRules.BasedOnNationalRankingSeedingRule.buildParticipants;

public class StandardSeasonBuilder {
    public static final String FIRST_QUALIFYING_ROUND_ID = "FQR";
    public static final String SECOND_QUALIFYING_ROUND_ID = "SQR";
    public static final String GROUP_STAGE_ID = "GS";
    public static final String PLAY_OFF_ID = "PO";
    public static final String CHAMPIONS_LEAGUE_ID = "CL";
    public static final String FEDERATIONS_CUP_ID = "FC";

    public static final int NATIONAL_CUPS_START_DATE = 0;
    public static final int NATIONAL_CUPS_DATE_OFFSET = 6;
    public static final int CHAMPIONS_LEAGUE_FQS_START_DATE = 30;
    public static final int FEDERATIONS_CUP_FQS_START_DATE = 31;
    public static final int CHAMPIONS_LEAGUE_SQS_START_DATE = 32;
    public static final int FEDERATIONS_CUP_SQS_START_DATE = 33;
    public static final int FEDERATIONS_CUP_GROUP_STAGE_START_DATE = 35;
    public static final int CHAMPIONS_LEAGUE_GROUP_STAGE_START_DATE = 38;
    public static final int FEDERATIONS_CUP_PLAY_OFF_START_DATE = 42;
    public static final int CHAMPIONS_LEAGUE_PLAY_OFF_START_DATE = 42;

    public static CompetitionDefinition buildSeasonCompetitionDefinition() {
        var nationalCups = Arrays.stream(Nation.values()).map(StandardSeasonBuilder::buildNationalCup).toList();
        var competitions = new ArrayList<>(nationalCups);
        competitions.add(buildFederationsCup());
        competitions.add(buildChampionsLeague());
        return new MultiStageCompetitionDefinition("SEASON", "Сезон", competitions, Optional.empty());
    }

    public static CompetitionDefinition buildNationalCup(Nation nation) {
        var nationSeeding = new SeedingTrigger(new SeasonStartTriggerTime(),
                new AllFromNationSeedingRule(nation));
        var startDate = NATIONAL_CUPS_START_DATE + nation.ordinal() * NATIONAL_CUPS_DATE_OFFSET;
        return new GroupStageDefinition(nation.name().toUpperCase(), "Кубок " + nation.getName(), startDate,
                1, 6, Optional.of(nationSeeding));
    }

    public static CompetitionDefinition buildChampionsLeague() {
        var lastNationalCupPath = buildPath(PARENT, PARENT, Nation.values()[Nation.values().length - 1].name());
        var firstQualifyingStageSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(lastNationalCupPath),
                new BasedOnNationalRankingSeedingRule(emptyList(), buildParticipants(
                        1, 4,
                        3, 3,
                        4, 2,
                        4, 3,
                        5, 1,
                        5, 2
                )));
        var firstQualifyingStageConfiguration = PlayOffStageConfiguration.builder().playerCount(6).rounds(1).build();
        var firstQualifyingStage = new PlayOffStageDefinition(FIRST_QUALIFYING_ROUND_ID,
                "Первый квалификационный раунд", CHAMPIONS_LEAGUE_FQS_START_DATE,
                firstQualifyingStageConfiguration, Optional.of(firstQualifyingStageSeedingTrigger));

        var secondQualifyingStageSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, FIRST_QUALIFYING_ROUND_ID)),
                new BasedOnNationalRankingSeedingRule(
                        List.of(
                                new PreviousRound(buildPath(PARENT, FIRST_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                1, 3,
                                2, 2,
                                2, 3,
                                3, 2,
                                4, 1
                        )
                )
        );
        var secondQualifyingStageConfiguration = PlayOffStageConfiguration.builder().playerCount(8).rounds(1).build();
        var secondQualifyingStage = new PlayOffStageDefinition(SECOND_QUALIFYING_ROUND_ID,
                "Второй квалификационный раунд", CHAMPIONS_LEAGUE_SQS_START_DATE,
                secondQualifyingStageConfiguration, Optional.of(secondQualifyingStageSeedingTrigger));

        var groupStageSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new BasedOnNationalRankingSeedingRule(
                        List.of(
                                new PreviousRound(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                1, 1,
                                1, 2,
                                2, 1,
                                3, 1
                        )
                )
        );
        var groupStage = new GroupStageDefinition(GROUP_STAGE_ID, "Групповой раунд",
                CHAMPIONS_LEAGUE_GROUP_STAGE_START_DATE, 2, 4,
                Optional.of(groupStageSeedingTrigger));

        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, GROUP_STAGE_ID)),
                new GroupStageToPlayOff(buildPath(PARENT, GROUP_STAGE_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = new PlayOffStageDefinition(PLAY_OFF_ID, "Плей-офф",
                CHAMPIONS_LEAGUE_PLAY_OFF_START_DATE, playOffStageConfiguration, Optional.of(playOffSeedingTrigger));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        return new MultiStageCompetitionDefinition(CHAMPIONS_LEAGUE_ID, "Лига Чемпионов",
                stages, Optional.empty());
    }

    public static CompetitionDefinition buildFederationsCup() {
        var firstCLQualifyingRoundPath = buildPath(PARENT, PARENT, CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID);
        var firstQualifyingStageSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(firstCLQualifyingRoundPath),
                new BasedOnNationalRankingSeedingRule(
                        List.of(new PreviousRound(firstCLQualifyingRoundPath, false)),
                        buildParticipants(
                                1, 5
                        )
                )
        );
        var firstQualifyingStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).rounds(1).build();
        var firstQualifyingStage = new PlayOffStageDefinition(FIRST_QUALIFYING_ROUND_ID,
                "Первый квалификационный раунд", FEDERATIONS_CUP_FQS_START_DATE,
                firstQualifyingStageConfiguration, Optional.of(firstQualifyingStageSeedingTrigger));

        var secondCLQualifyingRoundPath = buildPath(PARENT, PARENT, CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID);
        var secondQualifyingStageSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(secondCLQualifyingRoundPath),
                new BasedOnNationalRankingSeedingRule(
                        List.of(
                                new PreviousRound(secondCLQualifyingRoundPath, false),
                                new PreviousRound(buildPath(PARENT, FIRST_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                2, 5,
                                3, 5,
                                4, 4,
                                5, 4
                        )
                )
        );
        var secondQualifyingStageConfiguration = PlayOffStageConfiguration.builder().playerCount(10).rounds(1).build();
        var secondQualifyingStage = new PlayOffStageDefinition(SECOND_QUALIFYING_ROUND_ID,
                "Второй квалификационный раунд", FEDERATIONS_CUP_SQS_START_DATE,
                secondQualifyingStageConfiguration, Optional.of(secondQualifyingStageSeedingTrigger));

        var groupStageSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new BasedOnNationalRankingSeedingRule(
                        List.of(
                                new PreviousRound(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                2, 4,
                                3, 4,
                                5, 3
                        )
                )
        );
        var groupStage = new GroupStageDefinition(GROUP_STAGE_ID, "Групповой раунд",
                FEDERATIONS_CUP_GROUP_STAGE_START_DATE, 2, 4,
                Optional.of(groupStageSeedingTrigger));

        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, GROUP_STAGE_ID)),
                new GroupStageToPlayOff(buildPath(PARENT, GROUP_STAGE_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = new PlayOffStageDefinition(PLAY_OFF_ID, "Плей-офф",
                FEDERATIONS_CUP_PLAY_OFF_START_DATE, playOffStageConfiguration, Optional.of(playOffSeedingTrigger));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        return new MultiStageCompetitionDefinition(FEDERATIONS_CUP_ID, "Кубок Федераций",
                stages, Optional.empty());
    }

    public static CompetitionDefinition buildStandardCompetition() {
        var groupStage = new GroupStageDefinition(GROUP_STAGE_ID, "Групповой этап",
                4, 4, 0,
                Optional.of(new SeedingTrigger(new SeasonStartTriggerTime(), new RandomSelection())));
        var groupStagePath = buildPath(PARENT, GROUP_STAGE_ID);
        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(groupStagePath),
                new GroupStageToPlayOff(groupStagePath));
        var playOffStage = new PlayOffStageDefinition(PLAY_OFF_ID,
                "Плэй-офф", 10,
                PlayOffStageConfiguration.builder().playerCount(8).build(),
                Optional.of(playOffSeedingTrigger));
        return new MultiStageCompetitionDefinition("SC",
                "Стандартный турнир",
                Arrays.asList(groupStage, playOffStage),
                Optional.empty());
    }
}

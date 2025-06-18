package tm.lib.domain.competition;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayOffStageDefinition;
import tm.lib.domain.competition.base.CompetitionSchedule;
import tm.lib.domain.competition.base.triggers.SeedingRules.AllFromNationSeedingRule;
import tm.lib.domain.competition.base.triggers.SeedingRules.BasedOnNationalRankingSeedingRule;
import tm.lib.domain.competition.base.triggers.SeedingRules.BasedOnNationalRankingSeedingRule.PreviousRound;
import tm.lib.domain.competition.base.triggers.SeedingRules.GroupStageToPlayOff;
import tm.lib.domain.competition.base.triggers.SeedingRules.PreAssignFromEloRankings;
import tm.lib.domain.competition.base.triggers.SeedingRules.RandomSelection;
import tm.lib.domain.competition.base.triggers.SeedingRules.WorldCupRoundSeeding;
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
    public static final String WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID = "FQRG";
    public static final String WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID = "FQRPO";
    public static final String WORLD_CUP_ID = "WC";

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
    public static final int WORLD_CUP_FQR_GROUP_STAGE_START_DATE = 48;
    public static final int WORLD_CUP_FQR_PLAY_OFF_STAGE_START_DATE = 52;
    public static final int WORLD_CUP_SQR_START_DATE = 54;
    public static final int WORLD_CUP_GROUP_STAGE_START_DATE = 58;
    public static final int WORLD_CUP_PLAY_OFF_START_DATE = 62;


    public static CompetitionDefinition buildSeasonCompetitionDefinition() {
        var nationalCups = Arrays.stream(Nation.values()).map(StandardSeasonBuilder::buildNationalCup).toList();
        var competitions = new ArrayList<>(nationalCups);
        competitions.add(buildFederationsCup());
        competitions.add(buildChampionsLeague());
        competitions.add(buildWorldCup());
        return newMultiStageCompetition("SEASON", "Сезон", competitions);
    }

    public static CompetitionDefinition buildNationalCup(Nation nation) {
        var nationSeeding = new SeedingTrigger(new SeasonStartTriggerTime(),
                new AllFromNationSeedingRule(nation));
        var startDate = NATIONAL_CUPS_START_DATE + nation.ordinal() * NATIONAL_CUPS_DATE_OFFSET;
        return newGroupStage(nation.name().toUpperCase(), "Кубок " + nation.getName(), startDate,
                1, 6, List.of(nationSeeding));
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
        var firstQualifyingStage = newPlayOffStage(FIRST_QUALIFYING_ROUND_ID,
                "Первый квалификационный раунд",
                firstQualifyingStageConfiguration);
        firstQualifyingStage.setStartDate(CHAMPIONS_LEAGUE_FQS_START_DATE);
        firstQualifyingStage.setSeedingTriggers(List.of(firstQualifyingStageSeedingTrigger));

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
        var secondQualifyingStage = newPlayOffStage(SECOND_QUALIFYING_ROUND_ID,
                "Второй квалификационный раунд",
                secondQualifyingStageConfiguration);
        secondQualifyingStage.setStartDate(CHAMPIONS_LEAGUE_SQS_START_DATE);
        secondQualifyingStage.setSeedingTriggers(List.of(secondQualifyingStageSeedingTrigger));

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
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Групповой раунд",
                CHAMPIONS_LEAGUE_GROUP_STAGE_START_DATE, 2, 4,
                List.of(groupStageSeedingTrigger));

        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, GROUP_STAGE_ID)),
                new GroupStageToPlayOff(buildPath(PARENT, GROUP_STAGE_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = newPlayOffStage(PLAY_OFF_ID, "Плей-офф",
                playOffStageConfiguration);
        playOffStage.setStartDate(CHAMPIONS_LEAGUE_PLAY_OFF_START_DATE);
        playOffStage.setSeedingTriggers(List.of(playOffSeedingTrigger));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        return newMultiStageCompetition(CHAMPIONS_LEAGUE_ID, "Лига Чемпионов", stages);
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
        var firstQualifyingStage = newPlayOffStage(FIRST_QUALIFYING_ROUND_ID,
                "Первый квалификационный раунд",
                firstQualifyingStageConfiguration);
        firstQualifyingStage.setStartDate(FEDERATIONS_CUP_FQS_START_DATE);
        firstQualifyingStage.setSeedingTriggers(List.of(firstQualifyingStageSeedingTrigger));

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
        var secondQualifyingStage = newPlayOffStage(SECOND_QUALIFYING_ROUND_ID,
                "Второй квалификационный раунд",
                secondQualifyingStageConfiguration);
        secondQualifyingStage.setStartDate(FEDERATIONS_CUP_SQS_START_DATE);
        secondQualifyingStage.setSeedingTriggers(List.of(secondQualifyingStageSeedingTrigger));

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
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Групповой раунд",
                FEDERATIONS_CUP_GROUP_STAGE_START_DATE, 2, 4,
                List.of(groupStageSeedingTrigger));

        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, GROUP_STAGE_ID)),
                new GroupStageToPlayOff(buildPath(PARENT, GROUP_STAGE_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = newPlayOffStage(PLAY_OFF_ID, "Плей-офф",
                playOffStageConfiguration);
        playOffStage.setStartDate(FEDERATIONS_CUP_PLAY_OFF_START_DATE);
        playOffStage.setSeedingTriggers(List.of(playOffSeedingTrigger));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        return newMultiStageCompetition(FEDERATIONS_CUP_ID, "Кубок Федераций", stages);
    }

    public static CompetitionDefinition buildWorldCup() {
        var championsLeagueTriggerTime = new CompetitionEndedTriggerTime(buildPath(PARENT, PARENT, CHAMPIONS_LEAGUE_ID));
        var firstQualifyingStageEloTrigger = new SeedingTrigger(
                championsLeagueTriggerTime,
                new PreAssignFromEloRankings(22, 30)
        );
        var firstQualifyingStageTrigger = new SeedingTrigger(
                championsLeagueTriggerTime,
                new WorldCupRoundSeeding(Optional.empty())
        );
        var firstQualifyingStageGroupRound = newGroupStage(WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID,
                "Первый квалификационный раунд", WORLD_CUP_FQR_GROUP_STAGE_START_DATE, 2, 4,
                List.of(firstQualifyingStageEloTrigger, firstQualifyingStageTrigger));

        var fqrPath = buildPath(PARENT, WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID);
        var firstQualifyingStagePlayOffSeedingTrigger = new SeedingTrigger(
                championsLeagueTriggerTime,
                new GroupStageToPlayOff(fqrPath));
        var firstQualifyingRoundPlayOffStage = newPlayOffStage(WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID,
                "Плей-офф первого квалификационного раунда",
                PlayOffStageConfiguration.builder().playerCount(4).rounds(1).build());
        firstQualifyingRoundPlayOffStage.setStartDate(WORLD_CUP_FQR_PLAY_OFF_STAGE_START_DATE);
        firstQualifyingRoundPlayOffStage.setSeedingTriggers(List.of(firstQualifyingStagePlayOffSeedingTrigger));

        var secondQualifyingStageEloTrigger = new SeedingTrigger(
                championsLeagueTriggerTime,
                new PreAssignFromEloRankings(8, 22)
        );
        var secondQualifyingStageTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)),
                new WorldCupRoundSeeding(Optional.of(buildPath(PARENT, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)))
        );
        var secondQualifyingStageGroupRound = newGroupStage(SECOND_QUALIFYING_ROUND_ID,
                "Второй квалификационный раунд", WORLD_CUP_SQR_START_DATE, 4, 4,
                List.of(secondQualifyingStageEloTrigger, secondQualifyingStageTrigger));

        var groupStageEloTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new PreAssignFromEloRankings(0, 8)
        );
        var groupStageTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new WorldCupRoundSeeding(Optional.of(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)))
        );
        var groupStage = newGroupStage(GROUP_STAGE_ID,
                "Групповой раунд", WORLD_CUP_GROUP_STAGE_START_DATE, 4, 4,
                List.of(groupStageEloTrigger, groupStageTrigger));

        var groupStagePath = buildPath(PARENT, GROUP_STAGE_ID);
        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(groupStagePath),
                new GroupStageToPlayOff(groupStagePath));
        var playOffStage = newPlayOffStage(PLAY_OFF_ID,
                "Плэй-офф",
                PlayOffStageConfiguration.builder().playerCount(8).build());
        playOffStage.setStartDate(WORLD_CUP_PLAY_OFF_START_DATE);
        playOffStage.setSeedingTriggers(List.of(playOffSeedingTrigger));

        var stages = List.of(firstQualifyingStageGroupRound,
                firstQualifyingRoundPlayOffStage,
                secondQualifyingStageGroupRound,
                groupStage,
                playOffStage);
        var worldCup = newMultiStageCompetition(WORLD_CUP_ID, "Чемпионат Мира", stages);
        var schedule = new CompetitionSchedule();
        schedule.setPeriodInYears(4);
        schedule.setStartingFromYear(2);
        worldCup.setSchedule(schedule);
        return worldCup;
    }

    public static CompetitionDefinition buildStandardCompetition() {
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Групповой этап",
                0, 4, 4,
                List.of(new SeedingTrigger(new SeasonStartTriggerTime(), new RandomSelection())));
        var groupStagePath = buildPath(PARENT, GROUP_STAGE_ID);
        var playOffSeedingTrigger = new SeedingTrigger(
                new CompetitionEndedTriggerTime(groupStagePath),
                new GroupStageToPlayOff(groupStagePath));
        var playOffStage = newPlayOffStage(PLAY_OFF_ID,
                "Плэй-офф",
                PlayOffStageConfiguration.builder().playerCount(8).build());
        playOffStage.setStartDate(10);
        playOffStage.setSeedingTriggers(List.of(playOffSeedingTrigger));
        return newMultiStageCompetition("SC",
                "Стандартный турнир",
                Arrays.asList(groupStage, playOffStage));
    }

    private static GroupStageDefinition newGroupStage(String id,
                                                      String name,
                                                      int startDate,
                                                      int groupCount,
                                                      int playersPerGroup,
                                                      List<SeedingTrigger> seedingTriggers) {
        var definition = new GroupStageDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStartDate(startDate);
        definition.setGroupCount(groupCount);
        definition.setPlayersPerGroup(playersPerGroup);
        definition.setSeedingTriggers(seedingTriggers);
        return definition;
    }

    private static PlayOffStageDefinition newPlayOffStage(String id,
                                                          String name,
                                                          PlayOffStageConfiguration configuration) {
        var definition = new PlayOffStageDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setPlayOffStageConfiguration(configuration);
        return definition;
    }

    private static MultiStageCompetitionDefinition newMultiStageCompetition(String id,
                                                                            String name,
                                                                            List<CompetitionDefinition> stages) {
        var definition = new MultiStageCompetitionDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStages(stages);
        return definition;
    }
}

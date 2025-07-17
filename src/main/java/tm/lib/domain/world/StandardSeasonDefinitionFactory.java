package tm.lib.domain.world;

import com.google.common.collect.ImmutableMap;
import tm.lib.domain.competition.definition.CompetitionDefinition;
import tm.lib.domain.competition.definition.CompetitionEventDefinition;
import tm.lib.domain.competition.definition.CompetitionSchedule;
import tm.lib.domain.competition.definition.GroupDefinition;
import tm.lib.domain.competition.definition.GroupStageDefinition;
import tm.lib.domain.competition.definition.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.definition.PlayOffRoundDefinition;
import tm.lib.domain.competition.definition.PlayOffStageDefinition;
import tm.lib.domain.competition.definition.SeriesPlayOffRoundDefinition;
import tm.lib.domain.competition.definition.actions.AddAssignedParticipantsFromEloRankings;
import tm.lib.domain.competition.definition.actions.AwardTitle;
import tm.lib.domain.competition.definition.actions.GiveTrophy;
import tm.lib.domain.competition.definition.actions.SeedAllFromNationRule;
import tm.lib.domain.competition.definition.actions.SeedBasedOnNationalRankingRule;
import tm.lib.domain.competition.definition.actions.SeedBasedOnNationalRankingRule.PreviousRound;
import tm.lib.domain.competition.definition.actions.SeedGroupStageToPlayOff;
import tm.lib.domain.competition.definition.actions.SeedRandomPlayers;
import tm.lib.domain.competition.definition.actions.SeedTitlePlayOffParticipants;
import tm.lib.domain.competition.definition.actions.SeedWorldCupRound;
import tm.lib.domain.competition.definition.triggers.OnAllOfCompetitionsEnd;
import tm.lib.domain.competition.definition.triggers.OnCompetitionEnd;
import tm.lib.domain.competition.definition.triggers.OnSeasonStart;
import tm.lib.domain.competition.impl.PlayOffStageConfiguration;
import tm.lib.domain.core.Nation;
import tm.lib.domain.core.Title;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.CompetitionPath.createPath;
import static tm.lib.domain.competition.definition.actions.SeedBasedOnNationalRankingRule.buildParticipants;

public class StandardSeasonDefinitionFactory {
    public static final String FIRST_QUALIFYING_ROUND_ID = "FQR";
    public static final String SECOND_QUALIFYING_ROUND_ID = "SQR";
    public static final String GROUP_STAGE_ID = "GS";
    public static final String PLAY_OFF_ID = "PO";
    public static final String CHAMPIONS_LEAGUE_ID = "CL";
    public static final String FEDERATIONS_CUP_ID = "FC";
    public static final String WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID = "FQRG";
    public static final String WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID = "FQRPO";
    public static final String WORLD_CUP_ID = "WC";
    public static final String TITLE_PLAY_OFFS_ID = "TPO";
    public static final String SIR_TITLE_PLAY_OFF_ID = "STPO";
    public static final String LORD_TITLE_PLAY_OFF_ID = "LTPO";

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
    public static final int SIR_TITLE_PLAY_OFF_START_DATE = 69;
    public static final int LORD_TITLE_PLAY_OFF_START_DATE = 74;

    private static CompetitionEventDefinition createGiveTrophyEvent(String competitionPath) {
        return new CompetitionEventDefinition(
                new OnCompetitionEnd(competitionPath),
                new GiveTrophy(competitionPath)
        );
    }

    public static SeasonDefinition createSeasonDefinition(int year) {
        var builder = new SeasonDefinitionBuilder();
        builder.withYear(year)
                .withCompetitions(createNationalCups())
                .withCompetition(createFederationsCup())
                .withCompetition(createChampionsLeague())
                .withCompetition(createWorldCup())
                .withCompetition(createTitlePlayOffs())
                .withCompetitionPoints(createCompetitionPoints());
        return builder.build();
    }

    private static List<CompetitionDefinition> createNationalCups() {
        return Arrays.stream(Nation.values()).map(StandardSeasonDefinitionFactory::buildNationalCup).toList();
    }

    private static Map<String, Integer> createCompetitionPoints() {
        return ImmutableMap.<String, Integer>builder()
                .put(createPath(CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID), 2)
                .put(createPath(FEDERATIONS_CUP_ID, FIRST_QUALIFYING_ROUND_ID), 1)
                .put(createPath(CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID), 2)
                .put(createPath(FEDERATIONS_CUP_ID, SECOND_QUALIFYING_ROUND_ID), 1)
                .put(createPath(FEDERATIONS_CUP_ID, GROUP_STAGE_ID), 2)
                .put(createPath(CHAMPIONS_LEAGUE_ID, GROUP_STAGE_ID), 4)
                .put(createPath(FEDERATIONS_CUP_ID, PLAY_OFF_ID), 2)
                .put(createPath(CHAMPIONS_LEAGUE_ID, PLAY_OFF_ID), 4)
                .build();
    }

    private static CompetitionDefinition buildNationalCup(Nation nation) {
        var competitionId = nation.name().toUpperCase();
        var nationSeeding = new CompetitionEventDefinition(new OnSeasonStart(),
                new SeedAllFromNationRule(nation, competitionId));
        var startDate = NATIONAL_CUPS_START_DATE + nation.ordinal() * NATIONAL_CUPS_DATE_OFFSET;
        return newGroup(competitionId, "Cup of " + nation.getName(), startDate,
                6, List.of(nationSeeding, createGiveTrophyEvent(competitionId)));
    }

    private static CompetitionDefinition createChampionsLeague() {
        var lastNationalCupPath = createPath(Nation.values()[Nation.values().length - 1].name());
        var firstQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(lastNationalCupPath),
                new SeedBasedOnNationalRankingRule(emptyList(), buildParticipants(
                        1, 4,
                        3, 3,
                        4, 2,
                        4, 3,
                        5, 1,
                        5, 2
                ), createPath(CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID)));
        ;
        var firstQualifyingStage = newPlayOffRound(FIRST_QUALIFYING_ROUND_ID,
                "First qualifying round",
                CHAMPIONS_LEAGUE_FQS_START_DATE,
                6,
                List.of(firstQualifyingStageSeedingEvent));

        var secondQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID)),
                new SeedBasedOnNationalRankingRule(
                        List.of(
                                new PreviousRound(createPath(CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                1, 3,
                                2, 2,
                                2, 3,
                                3, 2,
                                4, 1
                        ),
                        createPath(CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID)
                )
        );
        var secondQualifyingStage = newPlayOffRound(SECOND_QUALIFYING_ROUND_ID,
                "Second qualifying round",
                CHAMPIONS_LEAGUE_SQS_START_DATE,
                8,
                List.of(secondQualifyingStageSeedingEvent));

        var groupStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID)),
                new SeedBasedOnNationalRankingRule(
                        List.of(
                                new PreviousRound(createPath(CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                1, 1,
                                1, 2,
                                2, 1,
                                3, 1
                        ),
                        createPath(CHAMPIONS_LEAGUE_ID, GROUP_STAGE_ID)
                )
        );
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Group round",
                CHAMPIONS_LEAGUE_GROUP_STAGE_START_DATE, 2, 4,
                List.of(groupStageSeedingEvent));

        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(CHAMPIONS_LEAGUE_ID, GROUP_STAGE_ID)),
                new SeedGroupStageToPlayOff(createPath(CHAMPIONS_LEAGUE_ID, GROUP_STAGE_ID), createPath(CHAMPIONS_LEAGUE_ID, PLAY_OFF_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = newPlayOffStage(PLAY_OFF_ID, "Play-off",
                CHAMPIONS_LEAGUE_PLAY_OFF_START_DATE,
                playOffStageConfiguration,
                List.of(playOffSeedingEvent));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        var championsLeague = newMultiStageCompetition(CHAMPIONS_LEAGUE_ID, "Champions League", stages);
        championsLeague.setEvents(List.of(createGiveTrophyEvent(CHAMPIONS_LEAGUE_ID)));
        return championsLeague;
    }

    private static CompetitionDefinition createFederationsCup() {
        var firstCLQualifyingRoundPath = createPath(CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID);
        var firstQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(firstCLQualifyingRoundPath),
                new SeedBasedOnNationalRankingRule(
                        List.of(new PreviousRound(firstCLQualifyingRoundPath, false)),
                        buildParticipants(
                                1, 5
                        ),
                        createPath(FEDERATIONS_CUP_ID, FIRST_QUALIFYING_ROUND_ID)
                )
        );
        var firstQualifyingStage = newPlayOffRound(FIRST_QUALIFYING_ROUND_ID,
                "First qualifying round",
                FEDERATIONS_CUP_FQS_START_DATE,
                4,
                List.of(firstQualifyingStageSeedingEvent));

        var secondCLQualifyingRoundPath = createPath(CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID);
        var secondQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(secondCLQualifyingRoundPath),
                new SeedBasedOnNationalRankingRule(
                        List.of(
                                new PreviousRound(secondCLQualifyingRoundPath, false),
                                new PreviousRound(createPath(FEDERATIONS_CUP_ID, FIRST_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                2, 5,
                                3, 5,
                                4, 4,
                                5, 4
                        ),
                        createPath(FEDERATIONS_CUP_ID, SECOND_QUALIFYING_ROUND_ID)
                )
        );
        var secondQualifyingStage = newPlayOffRound(SECOND_QUALIFYING_ROUND_ID,
                "Second qualifying round",
                FEDERATIONS_CUP_SQS_START_DATE,
                10,
                List.of(secondQualifyingStageSeedingEvent));

        var groupStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(FEDERATIONS_CUP_ID, SECOND_QUALIFYING_ROUND_ID)),
                new SeedBasedOnNationalRankingRule(
                        List.of(
                                new PreviousRound(createPath(FEDERATIONS_CUP_ID, SECOND_QUALIFYING_ROUND_ID), true)
                        ),
                        buildParticipants(
                                2, 4,
                                3, 4,
                                5, 3
                        ),
                        createPath(FEDERATIONS_CUP_ID, GROUP_STAGE_ID)
                )
        );
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Group round",
                FEDERATIONS_CUP_GROUP_STAGE_START_DATE, 2, 4,
                List.of(groupStageSeedingEvent));

        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(FEDERATIONS_CUP_ID, GROUP_STAGE_ID)),
                new SeedGroupStageToPlayOff(createPath(FEDERATIONS_CUP_ID, GROUP_STAGE_ID), createPath(FEDERATIONS_CUP_ID, PLAY_OFF_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = newPlayOffStage(PLAY_OFF_ID, "Play-off",
                FEDERATIONS_CUP_PLAY_OFF_START_DATE,
                playOffStageConfiguration,
                List.of(playOffSeedingEvent));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        var federationsCup = newMultiStageCompetition(FEDERATIONS_CUP_ID, "Federation Cup", stages);
        federationsCup.setEvents(List.of(createGiveTrophyEvent(FEDERATIONS_CUP_ID)));
        return federationsCup;
    }

    private static CompetitionDefinition createWorldCup() {
        var championsLeagueTrigger = new OnCompetitionEnd(createPath(CHAMPIONS_LEAGUE_ID));
        var firstQualifyingStageEloEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new AddAssignedParticipantsFromEloRankings(22, 30, createPath(WORLD_CUP_ID, WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID))
        );
        var firstQualifyingStageEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new SeedWorldCupRound(Optional.empty(), createPath(WORLD_CUP_ID, WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID))
        );
        var firstQualifyingStageGroupRound = newGroupStage(WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID,
                "First qualifying round", WORLD_CUP_FQR_GROUP_STAGE_START_DATE, 2, 4,
                List.of(firstQualifyingStageEloEvent, firstQualifyingStageEvent));

        var fqrPath = createPath(WORLD_CUP_ID, WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID);
        var firstQualifyingStagePlayOffSeedingEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new SeedGroupStageToPlayOff(fqrPath, createPath(WORLD_CUP_ID, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)));
        var firstQualifyingRoundPlayOffStage = newPlayOffStage(WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID,
                "First qualifying round play-off",
                WORLD_CUP_FQR_PLAY_OFF_STAGE_START_DATE,
                PlayOffStageConfiguration.builder().playerCount(4).rounds(1).build(),
                List.of(firstQualifyingStagePlayOffSeedingEvent));

        var secondQualifyingStageEloEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new AddAssignedParticipantsFromEloRankings(8, 22, createPath(WORLD_CUP_ID, SECOND_QUALIFYING_ROUND_ID))
        );
        var secondQualifyingStageEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(WORLD_CUP_ID, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)),
                new SeedWorldCupRound(Optional.of(createPath(WORLD_CUP_ID, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)), createPath(WORLD_CUP_ID, SECOND_QUALIFYING_ROUND_ID))
        );
        var secondQualifyingStageGroupRound = newGroupStage(SECOND_QUALIFYING_ROUND_ID,
                "Second qualifying round", WORLD_CUP_SQR_START_DATE, 4, 4,
                List.of(secondQualifyingStageEloEvent, secondQualifyingStageEvent));

        var groupStageEloEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(WORLD_CUP_ID, SECOND_QUALIFYING_ROUND_ID)),
                new AddAssignedParticipantsFromEloRankings(0, 8, createPath(WORLD_CUP_ID, GROUP_STAGE_ID))
        );
        var groupStageEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(createPath(WORLD_CUP_ID, SECOND_QUALIFYING_ROUND_ID)),
                new SeedWorldCupRound(Optional.of(createPath(WORLD_CUP_ID, SECOND_QUALIFYING_ROUND_ID)), createPath(WORLD_CUP_ID, GROUP_STAGE_ID))
        );
        var groupStage = newGroupStage(GROUP_STAGE_ID,
                "Group round", WORLD_CUP_GROUP_STAGE_START_DATE, 4, 4,
                List.of(groupStageEloEvent, groupStageEvent));

        var groupStagePath = createPath(WORLD_CUP_ID, GROUP_STAGE_ID);
        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(groupStagePath),
                new SeedGroupStageToPlayOff(groupStagePath, createPath(WORLD_CUP_ID, PLAY_OFF_ID)));
        var playOffStage = newPlayOffStage(PLAY_OFF_ID,
                "Play-off",
                WORLD_CUP_PLAY_OFF_START_DATE,
                PlayOffStageConfiguration.builder().playerCount(8).build(),
                List.of(playOffSeedingEvent));

        var stages = List.of(firstQualifyingStageGroupRound,
                firstQualifyingRoundPlayOffStage,
                secondQualifyingStageGroupRound,
                groupStage,
                playOffStage);
        var worldCup = newMultiStageCompetition(WORLD_CUP_ID, "World Cup", stages);
        worldCup.setEvents(List.of(createGiveTrophyEvent(WORLD_CUP_ID)));
        var schedule = new CompetitionSchedule();
        schedule.setPeriodInYears(4);
        schedule.setStartingFromYear(2);
        worldCup.setSchedule(schedule);
        return worldCup;
    }

    private static CompetitionDefinition createTitlePlayOffs() {
        var trigger = new OnAllOfCompetitionsEnd(List.of(
                createPath(CHAMPIONS_LEAGUE_ID),
                createPath(WORLD_CUP_ID)
        ));
        var sirPlayOffEvents = List.of(
                new CompetitionEventDefinition(
                        trigger,
                        new SeedTitlePlayOffParticipants(Title.SIR, createPath(TITLE_PLAY_OFFS_ID, SIR_TITLE_PLAY_OFF_ID))),
                new CompetitionEventDefinition(
                        new OnCompetitionEnd(createPath(TITLE_PLAY_OFFS_ID, SIR_TITLE_PLAY_OFF_ID)),
                        new AwardTitle(Title.SIR, createPath(TITLE_PLAY_OFFS_ID, SIR_TITLE_PLAY_OFF_ID)))
        );
        var sirPlayOff = newSeriesPlayOffRound(SIR_TITLE_PLAY_OFF_ID, "Sir Title Play-off",
                SIR_TITLE_PLAY_OFF_START_DATE, 3, sirPlayOffEvents);

        var lordPlayOffEvents = List.of(
                new CompetitionEventDefinition(
                        trigger,
                        new SeedTitlePlayOffParticipants(Title.LORD, createPath(TITLE_PLAY_OFFS_ID, LORD_TITLE_PLAY_OFF_ID))),
                new CompetitionEventDefinition(
                        new OnCompetitionEnd(createPath(TITLE_PLAY_OFFS_ID, LORD_TITLE_PLAY_OFF_ID)),
                        new AwardTitle(Title.LORD, createPath(TITLE_PLAY_OFFS_ID, LORD_TITLE_PLAY_OFF_ID)))
        );
        var lordPlayOff = newSeriesPlayOffRound(LORD_TITLE_PLAY_OFF_ID, "Lord Title Play-off",
                LORD_TITLE_PLAY_OFF_START_DATE, 3, lordPlayOffEvents);

        return newMultiStageCompetition(TITLE_PLAY_OFFS_ID, "Title play-offs", List.of(sirPlayOff, lordPlayOff));
    }

    private static CompetitionDefinition buildStandardCompetition() {
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Group stage",
                0, 4, 4,
                List.of(new CompetitionEventDefinition(new OnSeasonStart(), new SeedRandomPlayers(createPath("SC", GROUP_STAGE_ID)))));
        var groupStagePath = createPath("SC", GROUP_STAGE_ID);
        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(groupStagePath),
                new SeedGroupStageToPlayOff(groupStagePath, createPath("SC", PLAY_OFF_ID)));
        var playOffStage = newPlayOffStage(PLAY_OFF_ID,
                "Play-off",
                10,
                PlayOffStageConfiguration.builder().playerCount(8).build(),
                List.of(playOffSeedingEvent));
        return newMultiStageCompetition("SC",
                "Standard tournament",
                Arrays.asList(groupStage, playOffStage));
    }

    private static GroupDefinition newGroup(String id,
                                            String name,
                                            int startDate,
                                            int participantCount,
                                            List<CompetitionEventDefinition> events) {
        var definition = new GroupDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStartDate(startDate);
        definition.setParticipantCount(participantCount);
        definition.setEvents(events);
        return definition;
    }

    private static GroupStageDefinition newGroupStage(String id,
                                                      String name,
                                                      int startDate,
                                                      int groupCount,
                                                      int playersPerGroup,
                                                      List<CompetitionEventDefinition> events) {
        var definition = new GroupStageDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStartDate(startDate);
        definition.setGroupCount(groupCount);
        definition.setPlayersPerGroup(playersPerGroup);
        definition.setEvents(events);
        return definition;
    }

    private static PlayOffRoundDefinition newPlayOffRound(String id,
                                                          String name,
                                                          int startDate,
                                                          int participantCount,
                                                          List<CompetitionEventDefinition> events) {
        var definition = new PlayOffRoundDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStartDate(startDate);
        definition.setParticipantCount(participantCount);
        definition.setEvents(events);
        return definition;
    }

    private static SeriesPlayOffRoundDefinition newSeriesPlayOffRound(String id,
                                                                      String name,
                                                                      int startDate,
                                                                      int winCount,
                                                                      List<CompetitionEventDefinition> events) {
        var definition = new SeriesPlayOffRoundDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStartDate(startDate);
        definition.setWinCount(winCount);
        definition.setEvents(events);
        return definition;
    }

    private static PlayOffStageDefinition newPlayOffStage(String id,
                                                          String name,
                                                          int startDate,
                                                          PlayOffStageConfiguration configuration,
                                                          List<CompetitionEventDefinition> events) {
        var definition = new PlayOffStageDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setStartDate(startDate);
        definition.setPlayOffStageConfiguration(configuration);
        definition.setEvents(events);
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

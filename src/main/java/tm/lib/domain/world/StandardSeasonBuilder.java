package tm.lib.domain.world;

import com.google.common.collect.ImmutableMap;
import tm.lib.domain.competition.definition.CompetitionDefinition;
import tm.lib.domain.competition.definition.CompetitionEventDefinition;
import tm.lib.domain.competition.definition.GroupStageDefinition;
import tm.lib.domain.competition.definition.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.definition.PlayOffStageDefinition;
import tm.lib.domain.competition.definition.CompetitionSchedule;
import tm.lib.domain.competition.definition.actions.Actions.AddAssignedParticipantsFromEloRankings;
import tm.lib.domain.competition.definition.actions.Actions.GiveTrophy;
import tm.lib.domain.competition.definition.actions.Actions.SeedAllFromNationRule;
import tm.lib.domain.competition.definition.actions.Actions.SeedBasedOnNationalRankingRule;
import tm.lib.domain.competition.definition.actions.Actions.SeedBasedOnNationalRankingRule.PreviousRound;
import tm.lib.domain.competition.definition.actions.Actions.SeedGroupStageToPlayOff;
import tm.lib.domain.competition.definition.actions.Actions.SeedRandomPlayers;
import tm.lib.domain.competition.definition.actions.Actions.SeedWorldCupRound;
import tm.lib.domain.competition.definition.actions.Triggers.OnCompetitionEnd;
import tm.lib.domain.competition.definition.actions.Triggers.OnSeasonStart;
import tm.lib.domain.competition.impl.PlayOffStageConfiguration;
import tm.lib.domain.core.Nation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.CompetitionPath.PARENT;
import static tm.lib.domain.competition.CompetitionPath.SELF;
import static tm.lib.domain.competition.CompetitionPath.buildPath;
import static tm.lib.domain.competition.definition.actions.Actions.SeedBasedOnNationalRankingRule.buildParticipants;

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

    private static final CompetitionEventDefinition GIVE_TROPHY = new CompetitionEventDefinition(
            new OnCompetitionEnd(SELF),
            new GiveTrophy()
    );

    public static CompetitionDefinition buildSeasonCompetitionDefinition(int year) {
        var nationalCups = Arrays.stream(Nation.values()).map(StandardSeasonBuilder::buildNationalCup).toList();
        var competitions = new ArrayList<>(nationalCups);
        competitions.add(buildFederationsCup());
        competitions.add(buildChampionsLeague());
        competitions.add(buildWorldCup());
        return newMultiStageCompetition("SEASON", "Season " + (year + 1), competitions);
    }

    public static Map<String, Integer> buildCompetitionPoints() {
        return ImmutableMap.<String, Integer>builder()
                .put(buildPath(CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID), 2)
                .put(buildPath(FEDERATIONS_CUP_ID, FIRST_QUALIFYING_ROUND_ID), 1)
                .put(buildPath(CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID), 2)
                .put(buildPath(FEDERATIONS_CUP_ID, SECOND_QUALIFYING_ROUND_ID), 1)
                .put(buildPath(FEDERATIONS_CUP_ID, GROUP_STAGE_ID), 2)
                .put(buildPath(CHAMPIONS_LEAGUE_ID, GROUP_STAGE_ID), 4)
                .put(buildPath(FEDERATIONS_CUP_ID, PLAY_OFF_ID), 2)
                .put(buildPath(CHAMPIONS_LEAGUE_ID, PLAY_OFF_ID), 4)
                .build();
    }

    public static CompetitionDefinition buildNationalCup(Nation nation) {
        var nationSeeding = new CompetitionEventDefinition(new OnSeasonStart(),
                new SeedAllFromNationRule(nation));
        var startDate = NATIONAL_CUPS_START_DATE + nation.ordinal() * NATIONAL_CUPS_DATE_OFFSET;
        return newGroupStage(nation.name().toUpperCase(), "Cup of " + nation.getName(), startDate,
                1, 6, List.of(nationSeeding, GIVE_TROPHY));
    }

    public static CompetitionDefinition buildChampionsLeague() {
        var lastNationalCupPath = buildPath(PARENT, PARENT, Nation.values()[Nation.values().length - 1].name());
        var firstQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(lastNationalCupPath),
                new SeedBasedOnNationalRankingRule(emptyList(), buildParticipants(
                        1, 4,
                        3, 3,
                        4, 2,
                        4, 3,
                        5, 1,
                        5, 2
                )));
        var firstQualifyingStageConfiguration = PlayOffStageConfiguration.builder().playerCount(6).rounds(1).build();
        var firstQualifyingStage = newPlayOffStage(FIRST_QUALIFYING_ROUND_ID,
                "First qualifying round",
                CHAMPIONS_LEAGUE_FQS_START_DATE,
                firstQualifyingStageConfiguration,
                List.of(firstQualifyingStageSeedingEvent));

        var secondQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, FIRST_QUALIFYING_ROUND_ID)),
                new SeedBasedOnNationalRankingRule(
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
                "Second qualifying round",
                CHAMPIONS_LEAGUE_SQS_START_DATE,
                secondQualifyingStageConfiguration,
                List.of(secondQualifyingStageSeedingEvent));

        var groupStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new SeedBasedOnNationalRankingRule(
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
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Group round",
                CHAMPIONS_LEAGUE_GROUP_STAGE_START_DATE, 2, 4,
                List.of(groupStageSeedingEvent));

        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, GROUP_STAGE_ID)),
                new SeedGroupStageToPlayOff(buildPath(PARENT, GROUP_STAGE_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = newPlayOffStage(PLAY_OFF_ID, "Play-off",
                CHAMPIONS_LEAGUE_PLAY_OFF_START_DATE,
                playOffStageConfiguration,
                List.of(playOffSeedingEvent));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        var championsLeague = newMultiStageCompetition(CHAMPIONS_LEAGUE_ID, "Champions League", stages);
        championsLeague.setEvents(List.of(GIVE_TROPHY));
        return championsLeague;
    }

    public static CompetitionDefinition buildFederationsCup() {
        var firstCLQualifyingRoundPath = buildPath(PARENT, PARENT, CHAMPIONS_LEAGUE_ID, FIRST_QUALIFYING_ROUND_ID);
        var firstQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(firstCLQualifyingRoundPath),
                new SeedBasedOnNationalRankingRule(
                        List.of(new PreviousRound(firstCLQualifyingRoundPath, false)),
                        buildParticipants(
                                1, 5
                        )
                )
        );
        var firstQualifyingStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).rounds(1).build();
        var firstQualifyingStage = newPlayOffStage(FIRST_QUALIFYING_ROUND_ID,
                "First qualifying round",
                FEDERATIONS_CUP_FQS_START_DATE,
                firstQualifyingStageConfiguration,
                List.of(firstQualifyingStageSeedingEvent));

        var secondCLQualifyingRoundPath = buildPath(PARENT, PARENT, CHAMPIONS_LEAGUE_ID, SECOND_QUALIFYING_ROUND_ID);
        var secondQualifyingStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(secondCLQualifyingRoundPath),
                new SeedBasedOnNationalRankingRule(
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
                "Second qualifying round",
                FEDERATIONS_CUP_SQS_START_DATE,
                secondQualifyingStageConfiguration,
                List.of(secondQualifyingStageSeedingEvent));

        var groupStageSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new SeedBasedOnNationalRankingRule(
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
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Group round",
                FEDERATIONS_CUP_GROUP_STAGE_START_DATE, 2, 4,
                List.of(groupStageSeedingEvent));

        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, GROUP_STAGE_ID)),
                new SeedGroupStageToPlayOff(buildPath(PARENT, GROUP_STAGE_ID)));
        var playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        var playOffStage = newPlayOffStage(PLAY_OFF_ID, "Play-off",
                FEDERATIONS_CUP_PLAY_OFF_START_DATE,
                playOffStageConfiguration,
                List.of(playOffSeedingEvent));

        var stages = List.of(firstQualifyingStage, secondQualifyingStage, groupStage, playOffStage);

        var federationsCup = newMultiStageCompetition(FEDERATIONS_CUP_ID, "Federation Cup", stages);
        federationsCup.setEvents(List.of(GIVE_TROPHY));
        return federationsCup;
    }

    public static CompetitionDefinition buildWorldCup() {
        var championsLeagueTrigger = new OnCompetitionEnd(buildPath(PARENT, PARENT, CHAMPIONS_LEAGUE_ID));
        var firstQualifyingStageEloEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new AddAssignedParticipantsFromEloRankings(22, 30)
        );
        var firstQualifyingStageEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new SeedWorldCupRound(Optional.empty())
        );
        var firstQualifyingStageGroupRound = newGroupStage(WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID,
                "First qualifying round", WORLD_CUP_FQR_GROUP_STAGE_START_DATE, 2, 4,
                List.of(firstQualifyingStageEloEvent, firstQualifyingStageEvent));

        var fqrPath = buildPath(PARENT, WORLD_CUP_FIRST_QUALIFYING_ROUND_GROUP_STAGE_ID);
        var firstQualifyingStagePlayOffSeedingEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new SeedGroupStageToPlayOff(fqrPath));
        var firstQualifyingRoundPlayOffStage = newPlayOffStage(WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID,
                "First qualifying round play-off",
                WORLD_CUP_FQR_PLAY_OFF_STAGE_START_DATE,
                PlayOffStageConfiguration.builder().playerCount(4).rounds(1).build(),
                List.of(firstQualifyingStagePlayOffSeedingEvent));

        var secondQualifyingStageEloEvent = new CompetitionEventDefinition(
                championsLeagueTrigger,
                new AddAssignedParticipantsFromEloRankings(8, 22)
        );
        var secondQualifyingStageEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)),
                new SeedWorldCupRound(Optional.of(buildPath(PARENT, WORLD_CUP_FIRST_QUALIFYING_ROUND_PLAY_OFF_ID)))
        );
        var secondQualifyingStageGroupRound = newGroupStage(SECOND_QUALIFYING_ROUND_ID,
                "Second qualifying round", WORLD_CUP_SQR_START_DATE, 4, 4,
                List.of(secondQualifyingStageEloEvent, secondQualifyingStageEvent));

        var groupStageEloEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new AddAssignedParticipantsFromEloRankings(0, 8)
        );
        var groupStageEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)),
                new SeedWorldCupRound(Optional.of(buildPath(PARENT, SECOND_QUALIFYING_ROUND_ID)))
        );
        var groupStage = newGroupStage(GROUP_STAGE_ID,
                "Group round", WORLD_CUP_GROUP_STAGE_START_DATE, 4, 4,
                List.of(groupStageEloEvent, groupStageEvent));

        var groupStagePath = buildPath(PARENT, GROUP_STAGE_ID);
        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(groupStagePath),
                new SeedGroupStageToPlayOff(groupStagePath));
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
        worldCup.setEvents(List.of(GIVE_TROPHY));
        var schedule = new CompetitionSchedule();
        schedule.setPeriodInYears(4);
        schedule.setStartingFromYear(2);
        worldCup.setSchedule(schedule);
        return worldCup;
    }

    public static CompetitionDefinition buildStandardCompetition() {
        var groupStage = newGroupStage(GROUP_STAGE_ID, "Group stage",
                0, 4, 4,
                List.of(new CompetitionEventDefinition(new OnSeasonStart(), new SeedRandomPlayers())));
        var groupStagePath = buildPath(PARENT, GROUP_STAGE_ID);
        var playOffSeedingEvent = new CompetitionEventDefinition(
                new OnCompetitionEnd(groupStagePath),
                new SeedGroupStageToPlayOff(groupStagePath));
        var playOffStage = newPlayOffStage(PLAY_OFF_ID,
                "Play-off",
                10,
                PlayOffStageConfiguration.builder().playerCount(8).build(),
                List.of(playOffSeedingEvent));
        return newMultiStageCompetition("SC",
                "Standard tournament",
                Arrays.asList(groupStage, playOffStage));
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

package tm.lib.domain.competition;

import tm.lib.domain.competition.definition.CompetitionDefinition;
import tm.lib.domain.competition.definition.CompetitionSchedule;
import tm.lib.domain.competition.definition.GroupDefinition;
import tm.lib.domain.competition.definition.GroupStageDefinition;
import tm.lib.domain.competition.definition.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.definition.PlayOffRoundDefinition;
import tm.lib.domain.competition.definition.PlayOffStageDefinition;
import tm.lib.domain.competition.definition.SeriesPlayOffRoundDefinition;
import tm.lib.domain.competition.impl.GroupCompetition;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.MultiStageCompetition;
import tm.lib.domain.competition.impl.PlayOffRound;
import tm.lib.domain.competition.impl.PlayOffStage;
import tm.lib.domain.competition.impl.PlayOffStageConfiguration;
import tm.lib.domain.competition.impl.SeriesPlayOffRound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompetitionFactory {
    public static Competition createCompetition(CompetitionDefinition definition, int year) {
        var schedule = definition.getSchedule();
        if (checkSchedule(schedule, year)) {
            return null;
        } else {
            var competition = createTypedCompetition(definition, year);
            var events = definition.getEvents().stream()
                    .map(event ->
                            new CompetitionEvent(event.getTrigger(), event.getAction())
                    ).toList();
            competition.setEvents(events);
            return competition;
        }
    }

    private static boolean checkSchedule(CompetitionSchedule schedule, int year) {
        return schedule.getPeriodInYears() > 1
                && (year - schedule.getStartingFromYear()) % schedule.getPeriodInYears() != 0;
    }

    private static Competition createTypedCompetition(CompetitionDefinition definition, int year) {
        return switch (definition) {
            case GroupDefinition groupDefinition -> createGroupCompetition(groupDefinition);
            case GroupStageDefinition groupStageDefinition -> createGroupStage(groupStageDefinition);
            case PlayOffRoundDefinition playOffRoundDefinition -> createPlayOffRound(playOffRoundDefinition);
            case PlayOffStageDefinition playOffStageDefinition -> createPlayOffStage(playOffStageDefinition);
            case SeriesPlayOffRoundDefinition seriesPlayOffRoundDefinition ->
                    createSeriesPlayOffRound(seriesPlayOffRoundDefinition);
            case MultiStageCompetitionDefinition multiStageCompetitionDefinition ->
                    createMultiStageCompetition(year, multiStageCompetitionDefinition);
            case null, default ->
                    throw new IllegalArgumentException("Unknown competition definition type: " + definition);
        };
    }

    private static GroupCompetition createGroupCompetition(GroupDefinition groupDefinition) {
        var group = new GroupCompetition(
                groupDefinition.getId(),
                groupDefinition.getName(),
                groupDefinition.getParticipantCount()
        );
        group.setStartDate(groupDefinition.getStartDate());
        return group;
    }

    private static GroupStage createGroupStage(GroupStageDefinition groupStageDefinition) {
        var groupStage = new GroupStage(groupStageDefinition.getId(), groupStageDefinition.getName());
        var groups = createGroups(groupStageDefinition.getGroupCount(),
                groupStageDefinition.getPlayersPerGroup(),
                groupStageDefinition.getStartDate());
        groupStage.setGroups(groups);
        return groupStage;
    }

    private static PlayOffRound createPlayOffRound(PlayOffRoundDefinition playOffRoundDefinition) {
        var playOffRound = new PlayOffRound(
                playOffRoundDefinition.getId(),
                playOffRoundDefinition.getName(),
                playOffRoundDefinition.getParticipantCount());
        playOffRound.setStartDate(playOffRoundDefinition.getStartDate());
        return playOffRound;
    }

    private static Competition createSeriesPlayOffRound(SeriesPlayOffRoundDefinition definition) {
        var seriesPlayOff = new SeriesPlayOffRound(
                definition.getId(),
                definition.getName(),
                definition.getWinCount());
        seriesPlayOff.setStartDate(definition.getStartDate());
        return seriesPlayOff;
    }

    private static List<GroupCompetition> createGroups(int groupCount, int playersPerGroup, int startDate) {
        List<GroupCompetition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            String groupId = String.valueOf((char) ('A' + i));
            GroupCompetition group = new GroupCompetition(groupId, "Group " + groupId, playersPerGroup);
            group.setStartDate(startDate);
            groups.add(group);
        }
        return groups;
    }

    private static PlayOffStage createPlayOffStage(PlayOffStageDefinition playOffStageDefinition) {
        var playOffStage = new PlayOffStage(playOffStageDefinition.getId(),
                playOffStageDefinition.getName());
        var rounds = createRounds(playOffStageDefinition.getPlayOffStageConfiguration(),
                playOffStageDefinition.getStartDate());
        playOffStage.setRounds(rounds);
        return playOffStage;
    }

    private static List<PlayOffRound> createRounds(PlayOffStageConfiguration configuration, int startDate) {
        List<PlayOffRound> stages = new ArrayList<>();
        int roundPlayerCount = configuration.getPlayerCount();
        for (int i = 0; i < configuration.getRounds(); i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            var stageId = "POS" + (i + 1);
            PlayOffRound stage = new PlayOffRound(stageId, subStageName, roundPlayerCount);
            stage.setStartDate(startDate + i * 2);
            stages.add(stage);
            roundPlayerCount /= 2;
        }
        return stages;
    }

    private static String getDefaultRoundName(int playerCount) {
        if (playerCount == 2) {
            return "Final";
        } else if (playerCount == 4) {
            return "Semi-Finals";
        } else if (playerCount == 8) {
            return "Quarter-Finals";
        } else {
            return "Play-off";
        }
    }

    private static MultiStageCompetition createMultiStageCompetition(int year, MultiStageCompetitionDefinition multiStageCompetitionDefinition) {
        var multiStageCompetition = new MultiStageCompetition(multiStageCompetitionDefinition.getId(),
                multiStageCompetitionDefinition.getName());
        var stages = multiStageCompetitionDefinition.getStages().stream()
                .map(stage -> createCompetition(stage, year))
                .filter(Objects::nonNull)
                .toList();
        multiStageCompetition.setStages(stages);
        return multiStageCompetition;
    }
}

package tm.lib.domain.competition;

import tm.lib.domain.competition.definition.CompetitionDefinition;
import tm.lib.domain.competition.definition.CompetitionSchedule;
import tm.lib.domain.competition.definition.GroupStageDefinition;
import tm.lib.domain.competition.definition.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.definition.PlayOffRoundDefinition;
import tm.lib.domain.competition.definition.PlayOffStageDefinition;
import tm.lib.domain.competition.definition.actions.Actions;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.competition.definition.actions.Triggers;
import tm.lib.domain.competition.impl.GroupCompetition;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.MultiStageCompetition;
import tm.lib.domain.competition.impl.PlayOffRound;
import tm.lib.domain.competition.impl.PlayOffStage;
import tm.lib.domain.competition.impl.PlayOffStageConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompetitionBuilder {
    public static Competition buildCompetition(CompetitionDefinition definition, int year) {
        var schedule = definition.getSchedule();
        if (checkSchedule(schedule, year)) {
            return null;
        } else {
            var competition = buildTypedCompetition(definition, year);
            var events = definition.getEvents().stream()
                    .map(event ->
                            new CompetitionEvent(competition, event.getTrigger(), event.getAction())
                    ).toList();
            competition.setEvents(events);
            return competition;
        }
    }

    private static boolean checkSchedule(CompetitionSchedule schedule, int year) {
        return schedule.getPeriodInYears() > 1
                && (year - schedule.getStartingFromYear()) % schedule.getPeriodInYears() != 0;
    }

    private static Competition buildTypedCompetition(CompetitionDefinition definition, int year) {
        return switch (definition) {
            case GroupStageDefinition groupStageDefinition -> buildGroupStage(groupStageDefinition);
            case PlayOffRoundDefinition playOffRoundDefinition -> {
                var playOffRound = new PlayOffRound(
                        playOffRoundDefinition.getId(),
                        playOffRoundDefinition.getName(),
                        playOffRoundDefinition.getParticipantCount());
                playOffRound.setStartDate(playOffRoundDefinition.getStartDate());
                yield playOffRound;
            }
            case PlayOffStageDefinition playOffStageDefinition -> buildPlayOffStage(playOffStageDefinition);
            case MultiStageCompetitionDefinition multiStageCompetitionDefinition -> {
                var multiStageCompetition = new MultiStageCompetition(multiStageCompetitionDefinition.getId(),
                        multiStageCompetitionDefinition.getName());
                var stages = multiStageCompetitionDefinition.getStages().stream()
                        .map(stage -> buildCompetition(stage, year))
                        .filter(Objects::nonNull)
                        .toList();
                multiStageCompetition.setStages(stages);
                yield multiStageCompetition;
            }
            case null, default ->
                    throw new IllegalArgumentException("Unknown competition definition type: " + definition);
        };
    }

    private static GroupStage buildGroupStage(GroupStageDefinition groupStageDefinition) {
        var groupStage = new GroupStage(groupStageDefinition.getId(), groupStageDefinition.getName());
        var groups = buildGroups(groupStageDefinition.getGroupCount(),
                groupStageDefinition.getPlayersPerGroup(),
                groupStageDefinition.getStartDate());
        groupStage.setStages(groups);
        return groupStage;
    }

    private static List<Competition> buildGroups(int groupCount, int playersPerGroup, int startDate) {
        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            String groupId = String.valueOf((char) ('A' + i));
            GroupCompetition group = new GroupCompetition(groupId, "Group " + groupId, playersPerGroup);
            group.setStartDate(startDate);
            groups.add(group);
        }
        return groups;
    }

    private static PlayOffStage buildPlayOffStage(PlayOffStageDefinition playOffStageDefinition) {
        var playOffStage = new PlayOffStage(playOffStageDefinition.getId(),
                playOffStageDefinition.getName());
        var rounds = buildRounds(playOffStageDefinition.getPlayOffStageConfiguration(),
                playOffStageDefinition.getStartDate());
        playOffStage.setStages(rounds);
        return playOffStage;
    }

    private static List<Competition> buildRounds(PlayOffStageConfiguration configuration, int startDate) {
        List<Competition> stages = new ArrayList<>();
        int roundPlayerCount = configuration.getPlayerCount();
        for (int i = 0; i < configuration.getRounds(); i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            var stageId = "POS" + (i + 1);
            PlayOffRound stage = new PlayOffRound(stageId, subStageName, roundPlayerCount);
            if (i > 0) {
                var previousStageId = "POS" + i;
                var seedingEvent = new CompetitionEvent(
                        stage,
                        new Triggers.OnCompetitionEnd("../" + previousStageId),
                        new Actions.SeedPlayOffToPlayOff("../" + previousStageId));
                stage.setEvents(List.of(seedingEvent));
            }
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
}

package tm.lib.domain.competition;

import tm.lib.domain.competition.definition.CompetitionDefinition;
import tm.lib.domain.competition.definition.CompetitionSchedule;
import tm.lib.domain.competition.definition.GroupStageDefinition;
import tm.lib.domain.competition.definition.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.definition.PlayOffRoundDefinition;
import tm.lib.domain.competition.definition.PlayOffStageDefinition;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.MultiStageCompetition;
import tm.lib.domain.competition.impl.PlayOffRound;
import tm.lib.domain.competition.impl.PlayOffStage;

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
            case GroupStageDefinition groupStageDefinition -> {
                var groupStage = new GroupStage(groupStageDefinition.getId(),
                        groupStageDefinition.getName(),
                        groupStageDefinition.getGroupCount(),
                        groupStageDefinition.getPlayersPerGroup());
                var startDate = groupStageDefinition.getStartDate();
                groupStage.getGroups().forEach(g -> g.setStartDate(startDate));
                yield groupStage;
            }
            case PlayOffRoundDefinition playOffRoundDefinition -> {
                var playOffRound = new PlayOffRound(
                        playOffRoundDefinition.getId(),
                        playOffRoundDefinition.getName(),
                        playOffRoundDefinition.getParticipantCount());
                playOffRound.setStartDate(playOffRoundDefinition.getStartDate());
                yield playOffRound;
            }
            case PlayOffStageDefinition playOffStageDefinition -> {
                var playOffStage = new PlayOffStage(playOffStageDefinition.getId(),
                        playOffStageDefinition.getName(),
                        playOffStageDefinition.getPlayOffStageConfiguration());
                var startDate = playOffStageDefinition.getStartDate();
                var playOffRounds = playOffStage.getPlayOffRounds();
                for (int i = 0; i < playOffRounds.size(); ++i) {
                    playOffRounds.get(i).setStartDate(startDate + i * 2);
                }
                yield playOffStage;
            }
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
}

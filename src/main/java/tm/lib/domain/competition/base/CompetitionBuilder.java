package tm.lib.domain.competition.base;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayOffStageDefinition;
import tm.lib.domain.competition.base.events.CompetitionEvent;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayOffStage;

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
        switch (definition) {
            case GroupStageDefinition groupStageDefinition -> {
                var groupStage = new GroupStage(groupStageDefinition.getId(),
                        groupStageDefinition.getName(),
                        groupStageDefinition.getGroupCount(),
                        groupStageDefinition.getPlayersPerGroup());
                groupStage.setStartingDate(groupStageDefinition.getStartDate());
                return groupStage;
            }
            case PlayOffStageDefinition playOffStageDefinition -> {
                var playOffStage = new PlayOffStage(playOffStageDefinition.getId(),
                        playOffStageDefinition.getName(),
                        playOffStageDefinition.getPlayOffStageConfiguration());
                playOffStage.setStartingDate(playOffStageDefinition.getStartDate());
                return playOffStage;
            }
            case MultiStageCompetitionDefinition multiStageCompetitionDefinition -> {
                var multiStageCompetition = new MultiStageCompetition(multiStageCompetitionDefinition.getId(),
                        multiStageCompetitionDefinition.getName());
                var stages = multiStageCompetitionDefinition.getStages().stream()
                        .map(stage -> buildCompetition(stage, year))
                        .filter(Objects::nonNull)
                        .toList();
                multiStageCompetition.setStages(stages);
                return multiStageCompetition;
            }
            case null, default ->
                    throw new IllegalArgumentException("Unknown competition definition type: " + definition);
        }
    }
}

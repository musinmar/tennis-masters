package tm.lib.domain.competition.base;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayOffStageDefinition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayOffStage;

public class CompetitionBuilder {
    public static Competition buildCompetition(CompetitionDefinition definition) {
        switch (definition) {
            case GroupStageDefinition groupStageDefinition -> {
                var groupStage = new GroupStage(groupStageDefinition.getId(),
                        groupStageDefinition.getName(),
                        groupStageDefinition.getGroupCount(),
                        groupStageDefinition.getPlayersPerGroup());
                groupStage.setStartingDate(groupStageDefinition.getStartDate());
                groupStage.setSeedingTriggers(groupStageDefinition.getSeedingTriggers());
                return groupStage;
            }
            case PlayOffStageDefinition playOffStageDefinition -> {
                var playOffStage = new PlayOffStage(playOffStageDefinition.getId(),
                        playOffStageDefinition.getName(),
                        playOffStageDefinition.getPlayOffStageConfiguration());
                playOffStage.setStartingDate(playOffStageDefinition.getStartDate());
                playOffStage.setSeedingTriggers(playOffStageDefinition.getSeedingTriggers());
                return playOffStage;
            }
            case MultiStageCompetitionDefinition multiStageCompetitionDefinition -> {
                var multiStageCompetition = new MultiStageCompetition(multiStageCompetitionDefinition.getId(),
                        multiStageCompetitionDefinition.getName());
                var stages = multiStageCompetitionDefinition.getStages().stream()
                        .map(CompetitionBuilder::buildCompetition)
                        .toList();
                multiStageCompetition.setStages(stages);
                return multiStageCompetition;
            }
            case null, default ->
                    throw new IllegalArgumentException("Unknown competition definition type: " + definition);
        }
    }
}

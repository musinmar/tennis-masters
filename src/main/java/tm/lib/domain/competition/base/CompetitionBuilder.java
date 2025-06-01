package tm.lib.domain.competition.base;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayoffStageDefinition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayoffStage;

public class CompetitionBuilder {
    public static Competition buildCompetition(CompetitionDefinition definition) {
        switch (definition) {
            case GroupStageDefinition groupStageDefinition -> {
                var groupStage = new GroupStage(groupStageDefinition.getId(),
                        groupStageDefinition.getName(),
                        groupStageDefinition.getGroupCount(),
                        groupStageDefinition.getPlayersPerGroup());
                groupStage.setSeedingTrigger(groupStageDefinition.getSeedingTrigger());
                return groupStage;
            }
            case PlayoffStageDefinition playoffStageDefinition -> {
                var playoffStage = new PlayoffStage(playoffStageDefinition.getId(),
                        playoffStageDefinition.getName(),
                        playoffStageDefinition.getPlayoffStageConfiguration());
                playoffStage.setSeedingTrigger(playoffStageDefinition.getSeedingTrigger());
                return playoffStage;
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

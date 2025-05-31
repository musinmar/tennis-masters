package tm.lib.domain.competition.base;

import tm.lib.domain.competition.base.CompetitionDefinitions.CompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.GroupStageDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.MultiStageCompetitionDefinition;
import tm.lib.domain.competition.base.CompetitionDefinitions.PlayoffStageDefinition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayoffStage;

public class CompetitionBuilder {
    public static Competition buildCompetition(CompetitionDefinition definition) {
        if (definition instanceof GroupStageDefinition groupStageDefinition) {
            return new GroupStage(groupStageDefinition.getId(),
                    groupStageDefinition.getName(),
                    groupStageDefinition.getGroupCount(),
                    groupStageDefinition.getPlayersPerGroup());
        } else if (definition instanceof PlayoffStageDefinition playoffStageDefinition) {
            return new PlayoffStage(playoffStageDefinition.getId(),
                    playoffStageDefinition.getName(),
                    playoffStageDefinition.getPlayoffStageConfiguration());
        } else if (definition instanceof MultiStageCompetitionDefinition multiStageCompetitionDefinition) {
            var multiStageCompetition = new MultiStageCompetition(multiStageCompetitionDefinition.getId(),
                    multiStageCompetitionDefinition.getName());
            var stages = multiStageCompetitionDefinition.getStages().stream()
                    .map(CompetitionBuilder::buildCompetition)
                    .toList();
            multiStageCompetition.setStages(stages);
            return multiStageCompetition;
        } else {
            throw new IllegalArgumentException("Unknown competition definition type: " + definition);
        }
    }
}

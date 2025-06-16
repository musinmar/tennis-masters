package tm.lib.domain.competition.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.competition.standard.PlayOffStageConfiguration;

import java.util.List;
import java.util.Optional;

public class CompetitionDefinitions {

    public interface CompetitionDefinition {
        String getId();

        String getName();

        List<SeedingTrigger> getSeedingTriggers();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GroupStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private int startDate;
        private int groupCount;
        private int playersPerGroup;
        private List<SeedingTrigger> seedingTriggers;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlayOffStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private int startDate;
        private PlayOffStageConfiguration playOffStageConfiguration;
        private List<SeedingTrigger> seedingTriggers;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MultiStageCompetitionDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private List<CompetitionDefinition> stages;
        private List<SeedingTrigger> seedingTriggers;
    }
}

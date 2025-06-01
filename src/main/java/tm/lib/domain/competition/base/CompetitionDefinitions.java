package tm.lib.domain.competition.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;

import java.util.List;
import java.util.Optional;

public class CompetitionDefinitions {

    public interface CompetitionDefinition {
        String getId();

        String getName();

        Optional<SeedingTrigger> getSeedingTrigger();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GroupStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private int groupCount;
        private int playersPerGroup;
        private Optional<SeedingTrigger> seedingTrigger;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlayoffStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private PlayoffStageConfiguration playoffStageConfiguration;
        private Optional<SeedingTrigger> seedingTrigger;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MultiStageCompetitionDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private List<CompetitionDefinition> stages;
        private Optional<SeedingTrigger> seedingTrigger;
    }
}

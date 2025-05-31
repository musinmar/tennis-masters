package tm.lib.domain.competition.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.checkerframework.checker.units.qual.C;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;

import java.util.List;

public class CompetitionDefinitions {

    public interface CompetitionDefinition {
        String getId();
        String getName();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GroupStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private int groupCount;
        private int playersPerGroup;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlayoffStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private PlayoffStageConfiguration playoffStageConfiguration;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MultiStageCompetitionDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private List<CompetitionDefinition> stages;
    }
}

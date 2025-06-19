package tm.lib.domain.competition.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tm.lib.domain.competition.base.events.Action;
import tm.lib.domain.competition.base.events.Trigger;
import tm.lib.domain.competition.standard.PlayOffStageConfiguration;

import java.util.List;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.base.CompetitionSchedule.EVERY_YEAR;

public class CompetitionDefinitions {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CompetitionEventDefinition {
        private Trigger trigger;
        private Action action;
    }

    public interface CompetitionDefinition {
        String getId();
        String getName();
        List<CompetitionEventDefinition> getEvents();
        CompetitionSchedule getSchedule();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GroupStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private int startDate;
        private int groupCount;
        private int playersPerGroup;
        private List<CompetitionEventDefinition> events = emptyList();
        private CompetitionSchedule schedule = EVERY_YEAR;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlayOffStageDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private int startDate;
        private PlayOffStageConfiguration playOffStageConfiguration;
        private List<CompetitionEventDefinition> events = emptyList();
        private CompetitionSchedule schedule = EVERY_YEAR;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MultiStageCompetitionDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private List<CompetitionDefinition> stages;
        private List<CompetitionEventDefinition> events = emptyList();
        private CompetitionSchedule schedule = EVERY_YEAR;
    }
}

package tm.lib.domain.competition.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tm.lib.domain.competition.base.triggers.CompetitionEvent;
import tm.lib.domain.competition.standard.PlayOffStageConfiguration;

import java.util.List;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.base.CompetitionSchedule.EVERY_YEAR;

public class CompetitionDefinitions {

    public interface CompetitionDefinition {
        String getId();

        String getName();

        List<CompetitionEvent> getEvents();

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
        private List<CompetitionEvent> events = emptyList();
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
        private List<CompetitionEvent> events = emptyList();
        private CompetitionSchedule schedule = EVERY_YEAR;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MultiStageCompetitionDefinition implements CompetitionDefinition {
        private String id;
        private String name;
        private List<CompetitionDefinition> stages;
        private List<CompetitionEvent> events = emptyList();
        private CompetitionSchedule schedule = EVERY_YEAR;
    }
}

package tm.lib.domain.competition.definition;

import java.util.List;

public interface CompetitionDefinition {
    String getId();

    String getName();

    List<CompetitionEventDefinition> getEvents();

    CompetitionSchedule getSchedule();
}

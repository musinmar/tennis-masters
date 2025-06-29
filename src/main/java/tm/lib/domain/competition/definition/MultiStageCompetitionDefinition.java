package tm.lib.domain.competition.definition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.definition.CompetitionSchedule.EVERY_YEAR;

@Getter
@Setter
@NoArgsConstructor
public class MultiStageCompetitionDefinition implements CompetitionDefinition {
    private String id;
    private String name;
    private List<CompetitionDefinition> stages;
    private List<CompetitionEventDefinition> events = emptyList();
    private CompetitionSchedule schedule = EVERY_YEAR;
}

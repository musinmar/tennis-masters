package tm.lib.domain.competition.definition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tm.lib.domain.competition.impl.PlayOffStageConfiguration;

import java.util.List;

import static java.util.Collections.emptyList;
import static tm.lib.domain.competition.definition.CompetitionSchedule.EVERY_YEAR;

@Getter
@Setter
@NoArgsConstructor
public class PlayOffStageDefinition implements CompetitionDefinition {
    private String id;
    private String name;
    private int startDate;
    private PlayOffStageConfiguration playOffStageConfiguration;
    private List<CompetitionEventDefinition> events = emptyList();
    private CompetitionSchedule schedule = EVERY_YEAR;
}

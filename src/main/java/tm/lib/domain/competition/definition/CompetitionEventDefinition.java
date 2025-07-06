package tm.lib.domain.competition.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.definition.actions.Action;
import tm.lib.domain.competition.definition.triggers.Trigger;

@Getter
@Setter
@AllArgsConstructor
public class CompetitionEventDefinition {
    private Trigger trigger;
    private Action action;
}

package tm.lib.domain.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.definition.actions.Action;
import tm.lib.domain.competition.definition.triggers.Trigger;

@Getter
@Setter
@AllArgsConstructor
public class CompetitionEvent {
    private Competition competition;
    private Trigger trigger;
    private Action action;
}

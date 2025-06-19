package tm.lib.domain.competition.base.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.base.Competition;

@Getter
@Setter
@AllArgsConstructor
public class CompetitionEvent {
    private Competition competition;
    private Trigger trigger;
    private Action action;
}

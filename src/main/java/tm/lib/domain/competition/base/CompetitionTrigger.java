package tm.lib.domain.competition.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;

@Getter
@Setter
@AllArgsConstructor
public class CompetitionTrigger {
    private Competition competition;
    private SeedingTrigger trigger;
}
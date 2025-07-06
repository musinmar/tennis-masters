package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tm.lib.domain.core.Nation;

@Getter
@AllArgsConstructor
public class SeedAllFromNationRule implements Action {
    private Nation nation;
}
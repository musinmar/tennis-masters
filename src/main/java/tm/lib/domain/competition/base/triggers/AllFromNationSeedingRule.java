package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Nation;

@Getter
@Setter
@AllArgsConstructor
public class AllFromNationSeedingRule implements SeedingRule {

    private Nation nation;

    @Override
    public String getId() {
        return "ALL_FROM_NATION";
    }
}

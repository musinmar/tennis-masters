package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Nation;

@Getter
@Setter
public class SeedAllFromNationRuleDto extends ActionDto {
    private Nation nation;
}
package tm.lib.domain.core.dto;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Country;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;
import tm.lib.domain.core.SkillSet;

import java.util.List;

@Setter
@Getter
public class KnightDto {
    private int id;

    private String name;
    private String surname;

    private Nation nation;
    private Country country;

    private SkillSet skills;

    private List<Knight.Trophy> trophies;
}

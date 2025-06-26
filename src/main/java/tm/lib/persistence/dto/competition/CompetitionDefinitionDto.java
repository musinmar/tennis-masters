package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class CompetitionDefinitionDto {
    private String id;
    private String name;
    private List<CompetitionEventDefinitionDto> events;
    private CompetitionScheduleDto schedule;
}
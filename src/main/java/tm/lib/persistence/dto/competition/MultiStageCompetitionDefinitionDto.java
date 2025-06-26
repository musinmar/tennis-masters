package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiStageCompetitionDefinitionDto extends CompetitionDefinitionDto {
    private List<CompetitionDefinitionDto> stages;
}
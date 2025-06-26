package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiStageCompetitionDto extends AbstractCompetitionDto {
    private List<AbstractCompetitionDto> stages;
}
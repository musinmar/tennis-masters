package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupStageDefinitionDto extends CompetitionDefinitionDto {
    private int startDate;
    private int groupCount;
    private int playersPerGroup;
}
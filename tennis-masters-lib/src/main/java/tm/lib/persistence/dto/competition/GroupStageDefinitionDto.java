package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupStageDefinitionDto extends CompetitionDefinitionDto {
    public static final String TYPE = "groupStageDefinition";
    
    private int startDate;
    private int groupCount;
    private int playersPerGroup;
}
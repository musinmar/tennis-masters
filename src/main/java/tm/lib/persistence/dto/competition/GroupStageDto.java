package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupStageDto extends MultiStageCompetitionDto {
    private int groupCount;
    private int playersPerGroup;
}
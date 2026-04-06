package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupStageDto extends MultiStageCompetitionDto {
    public static final String TYPE = "groupStage";
}
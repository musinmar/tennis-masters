package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupSubStageDto extends SimpleCompetitionDto {
    public static final String TYPE = "groupSubStage";

    private List<GroupResultDto> groupResults;
}
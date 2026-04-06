package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupCompetitionDto extends SimpleCompetitionDto {
    public static final String TYPE = "group";

    private List<GroupResultDto> groupResults;
}
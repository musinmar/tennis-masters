package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiStageCompetitionDto extends AbstractCompetitionDto {
    public static final String TYPE = "multiStage";

    private String subType;
    private List<AbstractCompetitionDto> stages;
}
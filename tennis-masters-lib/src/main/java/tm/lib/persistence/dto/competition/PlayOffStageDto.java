package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayOffStageDto extends MultiStageCompetitionDto {
    public static final String TYPE = "playOffStage";
}
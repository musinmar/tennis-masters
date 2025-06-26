package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeedGroupStageToPlayOffDto extends ActionDto {
    public static final String TYPE = "seedGroupStageToPlayOff";
    
    private String groupStagePath;
}
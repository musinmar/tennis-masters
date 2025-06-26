package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnSeasonStartDto extends TriggerDto {
    public static final String TYPE = "onSeasonStart";
    
}
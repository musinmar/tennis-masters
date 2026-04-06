package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnCompetitionEndDto extends TriggerDto {
    public static final String TYPE = "onCompetitionEnd";
    
    private String competitionPath;
}
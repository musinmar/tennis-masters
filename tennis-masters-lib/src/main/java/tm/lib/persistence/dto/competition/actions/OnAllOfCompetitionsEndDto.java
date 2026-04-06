package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OnAllOfCompetitionsEndDto extends TriggerDto {
    public static final String TYPE = "onAllOfCompetitionsEnd";
    
    private List<String> competitionPaths;
}
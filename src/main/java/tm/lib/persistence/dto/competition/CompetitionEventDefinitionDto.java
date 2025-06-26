package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;
import tm.lib.persistence.dto.competition.actions.ActionDto;
import tm.lib.persistence.dto.competition.actions.TriggerDto;

@Getter
@Setter
public class CompetitionEventDefinitionDto {
    private TriggerDto trigger;
    private ActionDto action;
}
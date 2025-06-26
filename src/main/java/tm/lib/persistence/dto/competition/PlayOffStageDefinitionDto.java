package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayOffStageDefinitionDto extends CompetitionDefinitionDto {
    private int startDate;
    private PlayOffStageConfigurationDto playOffStageConfiguration;
}
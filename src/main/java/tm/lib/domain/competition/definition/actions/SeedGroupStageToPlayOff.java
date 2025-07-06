package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeedGroupStageToPlayOff implements Action {
    private String groupStagePath;
}
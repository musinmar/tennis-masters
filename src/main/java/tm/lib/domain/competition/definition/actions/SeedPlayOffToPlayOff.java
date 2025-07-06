package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeedPlayOffToPlayOff implements Action {
    private String playOffRoundPath;
}
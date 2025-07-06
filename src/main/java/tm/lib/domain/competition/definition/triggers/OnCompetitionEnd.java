package tm.lib.domain.competition.definition.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OnCompetitionEnd implements Trigger {
    private String competitionPath;
}
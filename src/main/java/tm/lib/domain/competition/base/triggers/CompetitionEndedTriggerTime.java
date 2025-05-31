package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompetitionEndedTriggerTime implements TriggerTime {

    private String competitionPath;

    @Override
    public String getId() {
        return "COMPETITION_ENDED";
    }
}

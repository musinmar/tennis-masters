package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SeasonStartTriggerTime implements TriggerTime {
    @Override
    public String getId() {
        return "SEASON_START";
    }
}

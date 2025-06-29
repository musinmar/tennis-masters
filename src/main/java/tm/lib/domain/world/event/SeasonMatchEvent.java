package tm.lib.domain.world.event;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.MatchEvent;

@Getter
@Setter
public class SeasonMatchEvent implements WorldEvent {
    private MatchEvent matchEvent;
}

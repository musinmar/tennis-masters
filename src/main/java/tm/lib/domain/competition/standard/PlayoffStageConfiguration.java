package tm.lib.domain.competition.standard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayoffStageConfiguration {
    private int playerCount;
    private int rounds;
}

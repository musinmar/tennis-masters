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

    public PlayoffStageConfiguration(int playerCount, int rounds) {
        this.playerCount = playerCount;
        if (rounds == 0) {
            if (playerCount == 4) {
                rounds = 2;
            } else if (playerCount == 8) {
                rounds = 3;
            }
        }
        this.rounds = rounds;
    }
}

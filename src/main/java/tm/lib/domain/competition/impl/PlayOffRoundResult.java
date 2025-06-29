package tm.lib.domain.competition.impl;

import lombok.Getter;
import tm.lib.domain.core.Knight;

import java.util.List;

@Getter
public class PlayOffRoundResult {
    private final List<Knight> winners;
    private final List<Knight> losers;

    public PlayOffRoundResult(List<Knight> winners, List<Knight> losers) {
        this.winners = winners;
        this.losers = losers;
    }
}

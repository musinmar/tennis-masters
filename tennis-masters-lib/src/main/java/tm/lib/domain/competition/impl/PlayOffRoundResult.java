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

    public Knight getWinner() {
        List<Knight> winners = getWinners();
        if (winners.size() == 1) {
            return winners.getFirst();
        } else {
            throw new IllegalStateException("Can't determine single winner of multi-pair play-off stage");
        }
    }

    public Knight getLoser() {
        List<Knight> losers = getLosers();
        if (losers.size() == 1) {
            return losers.getFirst();
        } else {
            throw new IllegalStateException("Can't determine single loser of multi-pair play-off stage");
        }
    }
}

package tm.lib.domain.competition.standard;

import tm.lib.domain.core.Knight;

import java.util.List;

public class PlayoffSubStageResult {
    private final List<Knight> winners;
    private final List<Knight> losers;

    public PlayoffSubStageResult(List<Knight> winners, List<Knight> losers) {
        this.winners = winners;
        this.losers = losers;
    }

    public List<Knight> getWinners() {
        return winners;
    }

    public List<Knight> getLosers() {
        return losers;
    }
}

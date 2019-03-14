package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.List;

public class PlayoffSubStageResult {
    private final List<Person> winners;
    private final List<Person> losers;

    public PlayoffSubStageResult(List<Person> winners, List<Person> losers) {
        this.winners = winners;
        this.losers = losers;
    }

    public List<Person> getWinners() {
        return winners;
    }

    public List<Person> getLosers() {
        return losers;
    }
}

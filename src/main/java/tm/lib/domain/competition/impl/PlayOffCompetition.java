package tm.lib.domain.competition.impl;

import tm.lib.domain.competition.Competition;
import tm.lib.domain.core.Knight;

import java.util.List;

public interface PlayOffCompetition extends Competition {

    void setActualParticipants(List<Knight> players);

    PlayOffRoundResult getResults();

    @Override
    default Knight getWinner() {
        return getResults().getWinner();
    }

    default Knight getLoser() {
        return getResults().getLoser();
    }
}

package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Getter
@Setter
public class PlayOffStage extends AbstractMultiStageCompetition {

    private List<PlayOffRound> rounds;

    public PlayOffStage(String id, String name) {
        super(id, name);
    }

    public void setRounds(List<PlayOffRound> rounds) {
        rounds.forEach(g -> g.setParent(this));
        this.rounds = unmodifiableList(rounds);
    }

    @Override
    public List<Competition> getChildren() {
        return new ArrayList<>(getRounds());
    }

    public int getParticipantCount() {
        return getRounds().getFirst().getParticipants().size();
    }

    public void setActualParticipants(List<Knight> players) {
        getRounds().getFirst().setActualParticipants(players);
    }

    public PlayOffRoundResult getLastRoundResults() {
        return getRounds().getLast().getResults();
    }

    @Override
    public void processChildCompetitionFinished(Competition child) {
        for (int i = 0; i < rounds.size() - 1; ++i) {
            var round = rounds.get(i);
            if (round == child) {
                var nextRound = rounds.get(i + 1);
                nextRound.setActualParticipants(round.getResults().getWinners());
                break;
            }
        }
    }
}

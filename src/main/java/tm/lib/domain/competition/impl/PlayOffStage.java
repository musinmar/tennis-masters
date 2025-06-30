package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Knight;

import java.util.List;

@Getter
@Setter
public class PlayOffStage extends MultiStageCompetition {

    public PlayOffStage() {
    }

    public PlayOffStage(String id, String name) {
        super(id, name);
    }

    public int getParticipantCount() {
        return getPlayOffRounds().getFirst().getParticipants().size();
    }

    @SuppressWarnings("unchecked")
    public List<PlayOffRound> getPlayOffRounds() {
        return (List<PlayOffRound>) (List<?>) getStages();
    }

    public void setActualParticipants(List<Knight> players) {
        getPlayOffRounds().get(0).setActualParticipants(players);
    }

    public PlayOffRoundResult getLastRoundResults() {
        return getPlayOffRounds().getLast().getResults();
    }
}

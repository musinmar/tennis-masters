package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.competition.base.SimpleCompetition;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

public class PlayoffSubStage extends SimpleCompetition {

    public PlayoffSubStage(String name, int playerCount) {
        super(name);
        var stageId = getStageParticipantId(playerCount);
        setParticipants(Participant.createNewList(stageId, playerCount));

        List<MatchEvent> matches = new ArrayList<>(playerCount / 2);
        for (int i = 0; i < playerCount / 2; i++) {
            MatchEvent match = new MatchEvent(this, getParticipants().get(i * 2), getParticipants().get(i * 2 + 1), 4, true);
            matches.add(match);
        }
        setMatches(matches);
    }

    private static String getStageParticipantId(int playerCount) {
        return switch (playerCount) {
            case 2 -> "F";
            case 4 -> "SF";
            case 8 -> "QF";
            default -> Participant.DEFAULT_STAGE_PARTICIPANT_ID;
        };
    }

    @Override
    public void setStartingDate(int date) {
        for (MatchEvent match : getAllMatches()) {
            match.setDate(date);
        }
    }

    public PlayoffSubStageResult getResults() {
        List<Knight> winners = new ArrayList<>();
        List<Knight> losers = new ArrayList<>();
        for (int i = 0; i < getAllMatches().size(); i++) {
            MatchEvent m = getAllMatches().get(i);
            if (m.getResult() != null) {
                BasicScore s = m.getResult().getScoreBySets();
                if (s.v1 > s.v2) {
                    winners.add(m.getHomePlayer().getPlayer());
                    losers.add(m.getAwayPlayer().getPlayer());
                } else {
                    winners.add(m.getAwayPlayer().getPlayer());
                    losers.add(m.getHomePlayer().getPlayer());
                }
            }
        }
        return new PlayoffSubStageResult(winners, losers);
    }

    @Override
    public Knight getWinner() {
        List<Knight> winners = getResults().getWinners();
        if (winners.size() == 1) {
            return winners.getFirst();
        } else {
            throw new IllegalStateException("Can't determine single winner of multi-pair play-off stage");
        }
    }
}

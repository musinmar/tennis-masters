package tm.lib.domain.competition;

import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Person;

import java.util.ArrayList;
import java.util.List;

public class PlayoffSubStage extends SimpleCompetition {
    public PlayoffSubStage(Competition parent, int playerCount) {
        super(parent);

        List<Match> matches = new ArrayList<Match>(playerCount / 2);
        for (int i = 0; i < playerCount / 2; i++) {
            Match match = new Match(this, null, null, 4, true);
            matches.add(match);
        }
        setMatches(matches);
    }

    @Override
    public void setStartingDate(int date) {
        /*if (matches.size() == 4) {
         matches.get(0).date = date;
         matches.get(1).date = date;
         matches.get(2).date = date + 1;
         matches.get(3).date = date + 1;
         }
         else {*/
        for (Match match : getAllMatches()) {
            match.setDate(date);
        }
        //}
    }

    @Override
    protected void setParticipants(List<Person> participants) {
        super.setParticipants(participants);
        List<Match> matches = getAllMatches();
        int playerIndex = 0;
        for (int i = 0; i < matches.size(); ++i) {
            matches.get(i).setFirstPlayer(participants.get(playerIndex++));
            matches.get(i).setSecondPlayer(participants.get(playerIndex++));
        }
    }

    public PlayoffSubStageResult getResults() {
        List<Person> winners = new ArrayList<>();
        List<Person> losers = new ArrayList<>();
        for (int i = 0; i < getAllMatches().size(); i++) {
            Match m = getAllMatches().get(i);
            if (m.getResult() != null) {
                BasicScore s = m.getResult().getScoreBySets();
                if (s.v1 > s.v2) {
                    winners.add(m.getFirstPlayer());
                    losers.add(m.getSecondPlayer());
                } else {
                    winners.add(m.getSecondPlayer());
                    losers.add(m.getFirstPlayer());
                }
            }
        }
        return new PlayoffSubStageResult(winners, losers);
    }
}

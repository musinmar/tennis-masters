package tm.lib.domain.competition;

import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Person;
import tm.lib.domain.world.Season;

import java.util.ArrayList;
import java.util.List;

public class PlayoffSubStage extends SimpleCompetition {
    public PlayoffSubStage(Season season, String name, int playerCount) {
        super(season, name);

        List<MatchEvent> matches = new ArrayList<MatchEvent>(playerCount / 2);
        for (int i = 0; i < playerCount / 2; i++) {
            MatchEvent match = new MatchEvent(this, null, null, 4, true);
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
        for (MatchEvent match : getAllMatches()) {
            match.setDate(date);
        }
        //}
    }

    @Override
    protected void setParticipants(List<Person> participants) {
        super.setParticipants(participants);
        List<MatchEvent> matches = getAllMatches();
        int playerIndex = 0;
        for (int i = 0; i < matches.size(); ++i) {
            matches.get(i).setHomePlayer(participants.get(playerIndex++));
            matches.get(i).setAwayPlayer(participants.get(playerIndex++));
        }
    }

    public PlayoffSubStageResult getResults() {
        List<Person> winners = new ArrayList<>();
        List<Person> losers = new ArrayList<>();
        for (int i = 0; i < getAllMatches().size(); i++) {
            MatchEvent m = getAllMatches().get(i);
            if (m.getResult() != null) {
                BasicScore s = m.getResult().getScoreBySets();
                if (s.v1 > s.v2) {
                    winners.add(m.getHomePlayer());
                    losers.add(m.getAwayPlayer());
                } else {
                    winners.add(m.getAwayPlayer());
                    losers.add(m.getHomePlayer());
                }
            }
        }
        return new PlayoffSubStageResult(winners, losers);
    }
}

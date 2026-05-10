package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.Participant;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SeriesPlayOffRound extends SimpleCompetition implements PlayOffCompetition {

    public static final int PARTICIPANT_COUNT = 2;

    private int winCount;

    public SeriesPlayOffRound(String id, String name) {
        super(id, name);
    }

    public SeriesPlayOffRound(String id, String name, int winCount) {
        super(id, name, createParticipants("S", PARTICIPANT_COUNT));

        this.winCount = winCount;

        var matches = new ArrayList<MatchEvent>();
        for (int j = 0; j < winCount; ++j) {
            var match = createNewMatch(getParticipants().getFirst(), getParticipants().getLast());
            matches.add(match);
        }
        setMatches(matches);
    }

    private MatchEvent createNewMatch(Participant home, Participant away) {
        return new MatchEvent(this, home, away, 4, true);
    }

    @Override
    public void setStartDate(int date) {
        for (int i = 0; i < getAllMatches().size(); ++i) {
            getAllMatches().get(i).setDate(date + i);
        }
    }

    public PlayOffRoundResult getResults() {
        List<Knight> winners = new ArrayList<>();
        List<Knight> losers = new ArrayList<>();
        var seriesScore = getSeriesScore();
        if (seriesScore.v1 > seriesScore.v2) {
            winners.add(getParticipants().getFirst().getPlayerOrFail());
            losers.add(getParticipants().getLast().getPlayerOrFail());
        } else {
            winners.add(getParticipants().getLast().getPlayerOrFail());
            losers.add(getParticipants().getFirst().getPlayerOrFail());
        }
        return new PlayOffRoundResult(winners, losers);
    }

    private BasicScore getSeriesScore() {
        int r1 = 0;
        int r2 = 0;
        for (var m : getAllMatches()) {
            if (m.getResult() != null) {
                if (m.getResult().getScore().isFirstPlayerWinner()) {
                    ++r1;
                } else {
                    ++r2;
                }
            }
        }
        return new BasicScore(r1, r2);
    }

    @Override
    protected void afterMatchResultAssigned(MatchEvent match, MatchScore score) {
        super.afterMatchResultAssigned(match, score);
        var seriesScore = getSeriesScore();
        var matchesToVictory = winCount - Math.max(seriesScore.v1, seriesScore.v2);
        var remainingMatches = getAllMatches().stream().filter(m -> m.getResult() == null).count();
        if (matchesToVictory > remainingMatches) {
            var newMatch = createNewMatch(getParticipants().getFirst(), getParticipants().getLast());
            newMatch.setDate(getAllMatches().getLast().getDate() + 1);
            getAllMatches().add(newMatch);
        }
    }
}

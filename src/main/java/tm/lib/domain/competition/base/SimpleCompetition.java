package tm.lib.domain.competition.base;

import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.List;

abstract public class SimpleCompetition extends Competition {
    private List<MatchEvent> matches;
    private int nextMatchIndex;

    protected SimpleCompetition(String name) {
        super(name);
        nextMatchIndex = 0;
    }

    protected int getNextMatchIndex() {
        return nextMatchIndex;
    }

    @Override
    public void print(PrintStream stream) {
        super.print(stream);
        for (MatchEvent match : getAllMatches()) {
            stream.println(match.toString());
        }
    }

    @Override
    public MatchEvent getNextMatch() {
        if (nextMatchIndex < matches.size()) {
            return matches.get(nextMatchIndex);
        } else {
            return null;
        }
    }

    @Override
    public List<MatchEvent> getAllMatches() {
        return matches;
    }

    protected void setMatches(List<MatchEvent> matches) {
        this.matches = matches;
    }

    @Override
     public final void processMatchResult(MatchEvent match, MatchScore score) {
        assert match == getNextMatch();
        match.setResult(score);
        doProcessMatchResult(match, score);
        ++nextMatchIndex;
        checkIfCompetitionFinished();
    }

    protected void doProcessMatchResult(MatchEvent match, MatchScore score) {
    }

    @Override
    public int getStartingDate() {
        return getAllMatches().get(0).getDate();
    }

    @Override
    public void setStartingDate(int date) {
        getAllMatches().forEach(match -> match.setDate(date));
    }

    @Override
    public int getLastDate() {
        return getAllMatches().get(getAllMatches().size() - 1).getDate();
    }
}

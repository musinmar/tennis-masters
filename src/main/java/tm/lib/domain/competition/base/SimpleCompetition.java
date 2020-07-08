package tm.lib.domain.competition.base;

import java.io.PrintStream;
import java.util.List;

abstract public class SimpleCompetition extends Competition {
    private List<MatchEvent> matches;
    private int nextMatchIndex;

    protected SimpleCompetition(String name) {
        super(name);
        nextMatchIndex = 0;
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

    @Override
    public void onMatchEnded(MatchEvent match) {
        assert match == getNextMatch();
        ++nextMatchIndex;
        super.onMatchEnded(match);
    }

    protected void initMatches(List<MatchEvent> matches) {
        this.matches = matches;
        matches.forEach(m -> m.addMatchEndListener(this));
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

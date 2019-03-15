package tm.lib.domain.competition;

import tm.lib.domain.world.Season;

import java.io.PrintStream;
import java.util.List;

abstract public class SimpleCompetition extends Competition {
    private List<MatchEvent> matches;
    private int nextMatchIndex;

    protected SimpleCompetition(Season season, String name) {
        super(season, name);
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

    protected void setMatches(List<MatchEvent> matches) {
        assert this.matches == null;
        this.matches = matches;
    }
}

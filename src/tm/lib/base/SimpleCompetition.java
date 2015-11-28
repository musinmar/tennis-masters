package tm.lib.base;

import java.io.PrintStream;
import java.util.List;

abstract public class SimpleCompetition extends Competition
{
    private List<Match> matches;
    private int nextMatchIndex;

    SimpleCompetition(Season season)
    {
        super(season);
        nextMatchIndex = 0;
    }

    SimpleCompetition(Competition parentCompetition)
    {
        super(parentCompetition);
        nextMatchIndex = 0;
    }

    @Override
    public void print(PrintStream stream)
    {
        super.print(stream);
        for (Match match : getAllMatches())
        {
            stream.println(match.toString());
        }
    }

    @Override
    public Match getNextMatch()
    {
        if (nextMatchIndex < matches.size())
        {
            return matches.get(nextMatchIndex);
        }
        else
        {
            return null;
        }
    }

    @Override
    public List<Match> getAllMatches()
    {
        return matches;
    }

    @Override
    public void onMatchEnded(Match match)
    {
        assert match == getNextMatch();
        ++nextMatchIndex;
        super.onMatchEnded(match);
    }

    protected void setMatches(List<Match> matches)
    {
        assert this.matches == null;
        this.matches = matches;
    }
}

package tm.lib.domain;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

abstract public class MultiStageCompetition extends Competition implements ICompetitionEndListener
{
    private Competition[] stages;

    MultiStageCompetition(Season season)
    {
        super(season);
    }

    MultiStageCompetition(Competition parentCompetition)
    {
        super(parentCompetition);
    }

    @Override
    public void print(PrintStream stream)
    {
        super.print(stream);
        for (Competition stage : stages)
        {
            stage.print(stream);
            if (stage != stages[stages.length - 1])
            {
                stream.println();
            }
        }
    }

    @Override
    public Match getNextMatch()
    {
        Match nextMatch = null;
        for (Competition stage : getStages())
        {
            Match stageNextMatch = stage.getNextMatch();
            if (nextMatch == null || stageNextMatch.getDate() < nextMatch.getDate())
            {
                nextMatch = stageNextMatch;
            }
        }
        return nextMatch;
    }

    @Override
    public List<Match> getAllMatches()
    {
        /*SortedSet<Match> sortedMatches = new TreeSet<Match>();
         for (Competition stage : getStages())
         {
         sortedMatches.addAll(stage.getAllMatches());
         }
         return new ArrayList<Match>(sortedMatches);*/
        List<Match> sortedMatches = new LinkedList<Match>();
        for (Competition stage : getStages())
        {
            List<Match> stageMatches = stage.getAllMatches();
            ListIterator<Match> stageIt = stageMatches.listIterator();
            ListIterator<Match> it = sortedMatches.listIterator();
            while (it.hasNext())
            {
                if (!stageIt.hasNext())
                {
                    break;
                }

                Match nextStageMatch = stageIt.next();
                if (it.next().getDate() > nextStageMatch.getDate())
                {
                    it.previous();
                    sortedMatches.add(it.nextIndex(), nextStageMatch);
                }
                else
                {
                    stageIt.previous();
                }
            }
            while (stageIt.hasNext())
            {
                sortedMatches.add(stageIt.next());
            }
        }

        return new ArrayList<Match>(sortedMatches);
    }

    public Competition[] getStages()
    {
        return stages;
    }

    protected void setStages(Competition[] stages)
    {
        this.stages = stages;
    }

    @Override
    public void onCompetitionEnded(Competition competition)
    {
        if (getNextMatch() == null)
        {
            endCompetition();
        }
    }
}

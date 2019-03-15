package tm.lib.domain.competition;

import com.google.common.collect.ImmutableList;
import tm.lib.domain.world.Season;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

abstract public class MultiStageCompetition extends Competition implements ICompetitionEndListener {
    private List<Competition> stages;

    protected MultiStageCompetition(Season season, String name) {
        super(season, name);
    }

    @Override
    public void print(PrintStream stream) {
        super.print(stream);
        for (int i = 0; i < stages.size(); ++i) {
            stages.get(i).print(stream);
            if (i != stages.size() - 1) {
                stream.println();
            }
        }
    }

    @Override
    public Match getNextMatch() {
        Match nextMatch = null;
        for (Competition stage : getStages()) {
            Match stageNextMatch = stage.getNextMatch();
            if (nextMatch == null || stageNextMatch.getDate() < nextMatch.getDate()) {
                nextMatch = stageNextMatch;
            }
        }
        return nextMatch;
    }

    @Override
    public List<Match> getAllMatches() {
        /*SortedSet<Match> sortedMatches = new TreeSet<Match>();
         for (Competition stage : getStages())
         {
         sortedMatches.addAll(stage.getAllMatches());
         }
         return new ArrayList<Match>(sortedMatches);*/
        List<Match> sortedMatches = new LinkedList<Match>();
        for (Competition stage : getStages()) {
            List<Match> stageMatches = stage.getAllMatches();
            ListIterator<Match> stageIt = stageMatches.listIterator();
            ListIterator<Match> it = sortedMatches.listIterator();
            while (it.hasNext()) {
                if (!stageIt.hasNext()) {
                    break;
                }

                Match nextStageMatch = stageIt.next();
                if (it.next().getDate() > nextStageMatch.getDate()) {
                    it.previous();
                    sortedMatches.add(it.nextIndex(), nextStageMatch);
                } else {
                    stageIt.previous();
                }
            }
            while (stageIt.hasNext()) {
                sortedMatches.add(stageIt.next());
            }
        }

        return new ArrayList<Match>(sortedMatches);
    }

    public List<Competition> getStages() {
        return stages;
    }

    protected void initStages(List<Competition> stages) {
        this.stages = ImmutableList.copyOf(stages);
        stages.forEach(stage -> stage.addCompetitionEndListener(this));
        stages.forEach(stage -> stage.setParent(this));
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        if (getNextMatch() == null) {
            endCompetition();
        }
    }
}

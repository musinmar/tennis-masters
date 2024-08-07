package tm.lib.domain.competition.base;

import com.google.common.collect.ImmutableList;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

abstract public class MultiStageCompetition extends Competition {
    private List<Competition> stages;

    protected MultiStageCompetition(String name) {
        super(name);
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
    public MatchEvent getNextMatch() {
        MatchEvent nextMatch = null;
        for (Competition stage : getStages()) {
            MatchEvent stageNextMatch = stage.getNextMatch();
            if (nextMatch == null || (stageNextMatch != null && stageNextMatch.getDate() < nextMatch.getDate())) {
                nextMatch = stageNextMatch;
            }
        }
        return nextMatch;
    }

    @Override
    public List<MatchEvent> getAllMatches() {
        /*SortedSet<MatchEvent> sortedMatches = new TreeSet<MatchEvent>();
         for (Competition stage : getStages())
         {
         sortedMatches.addAll(stage.getAllMatches());
         }
         return new ArrayList<MatchEvent>(sortedMatches);*/
        List<MatchEvent> sortedMatches = new LinkedList<MatchEvent>();
        for (Competition stage : getStages()) {
            List<MatchEvent> stageMatches = stage.getAllMatches();
            ListIterator<MatchEvent> stageIt = stageMatches.listIterator();
            ListIterator<MatchEvent> it = sortedMatches.listIterator();
            while (it.hasNext()) {
                if (!stageIt.hasNext()) {
                    break;
                }

                MatchEvent nextStageMatch = stageIt.next();
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

        return new ArrayList<MatchEvent>(sortedMatches);
    }

    public List<Competition> getStages() {
        return stages;
    }

    protected void initStages(List<Competition> stages) {
        this.stages = ImmutableList.copyOf(stages);
        stages.forEach(stage -> stage.registerOnFinishedCallback(this::checkIfCompetitionFinished));
        stages.forEach(stage -> stage.setParent(this));
    }

    @Override
    public void processMatchResult(MatchEvent match, MatchScore score) {
        throw new UnsupportedOperationException("Competition " + getName() + " does not have any own matches");
    }

    @Override
    public int getStartingDate() {
        return getStages().get(0).getStartingDate();
    }

    @Override
    public void setStartingDate(int date) {
        Competition previousStage = null;
        for (Competition stage : getStages()) {
            if (previousStage == null) {
                stage.setStartingDate(date);
            } else {
                stage.setStartingDate(previousStage.getLastDate() + 2);
            }
            previousStage = stage;
        }
    }

    @Override
    public int getLastDate() {
        return getLastStage().getLastDate();
    }

    @Override
    public Knight getWinner() {
        return getLastStage().getWinner();
    }

    private Competition getLastStage() {
        return getStages().getLast();
    }
}

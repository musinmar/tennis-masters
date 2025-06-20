package tm.lib.domain.competition.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class MultiStageCompetition extends AbstractCompetition {
    private List<Competition> stages;

    public MultiStageCompetition(String id, String name) {
        super(id, name);
    }

    public void setStages(List<Competition> stages) {
        this.stages = ImmutableList.copyOf(stages);
        stages.forEach(stage -> stage.setParent(this));
    }

    @Override
    public Competition getChild(String id) {
        return stages.stream().filter(s -> s.getId().equals(id)).findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("Competition " + getId() + " does not have child " + id));
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
    public Optional<MatchEvent> getNextMatch() {
        return getStages().stream().flatMap(c -> c.getNextMatch().stream())
                .min(Comparator.comparingInt(MatchEvent::getDate));
    }

    @Override
    public List<MatchEvent> getAllMatches() {
        List<MatchEvent> sortedMatches = new LinkedList<>();
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

        return new ArrayList<>(sortedMatches);
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
        for (var stage : getStages()) {
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

    @Override
    public List<CompetitionEvent> getAllEvents() {
        var events = stages.stream().flatMap(stage -> stage.getAllEvents().stream())
                .collect(Collectors.toList());
        events.addAll(getEvents());
        return events;
    }
}

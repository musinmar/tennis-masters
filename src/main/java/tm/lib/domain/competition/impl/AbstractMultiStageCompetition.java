package tm.lib.domain.competition.impl;

import lombok.Getter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.core.Knight;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractMultiStageCompetition extends AbstractCompetition {

    public AbstractMultiStageCompetition(String id, String name) {
        super(id, name);
    }

    public abstract List<Competition> getStages();

    @Override
    public Optional<Competition> getChild(String id) {
        return getStages().stream().filter(s -> s.getId().equals(id)).findAny();
    }

    @Override
    public void print(PrintStream stream) {
        super.print(stream);
        var stages = getStages();
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
    public Knight getWinner() {
        return getLastStage().getWinner();
    }

    private Competition getLastStage() {
        return getStages().getLast();
    }

    @Override
    public List<CompetitionEvent> getAllEvents() {
        var events = getStages().stream().flatMap(stage -> stage.getAllEvents().stream())
                .collect(Collectors.toList());
        events.addAll(getEvents());
        return events;
    }
}

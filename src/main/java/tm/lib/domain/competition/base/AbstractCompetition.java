package tm.lib.domain.competition.base;

import lombok.Getter;
import tm.lib.domain.competition.base.events.CompetitionEvent;
import tm.lib.domain.core.Knight;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

@Getter
abstract public class AbstractCompetition implements Competition {
    private Competition parent;
    private final String id;
    private final String name;
    private List<CompetitionEvent> events = emptyList();
    private final List<Knight> assignedParticipants = new ArrayList<>();

    private final List<Consumer<Competition>> competitionFinishedCallbacks = new ArrayList<>();

    protected AbstractCompetition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setParent(Competition parent) {
        this.parent = parent;
    }

    public Competition getChild(String id) {
        throw new IllegalArgumentException("Competition " + getId() + " does not have child " + id);
    }

    public void registerOnFinishedCallback(Consumer<Competition> callback) {
        competitionFinishedCallbacks.add(callback);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println(getName());
        stream.println();
    }

    public void setStartingDateAfter(Competition other) {
        setStartingDate(other.getLastDate() + 2);
    }

    private void runOnFinishedCallbacks() {
        for (var callback : competitionFinishedCallbacks) {
            callback.accept(this);
        }
    }

    protected void checkIfCompetitionFinished(Competition competition) {
        if (getNextMatch().isEmpty()) {
            runOnFinishedCallbacks();
        }
    }

    public String getFullName(boolean includeSeason) {
        String name = getName();
        Competition parent = getParent();
        while (true) {
            boolean isSeasonCompetition = (parent != null && parent.getParent() == null);
            if (includeSeason || !isSeasonCompetition) {
                name = parent.getName() + " - " + name;
            }
            if (isSeasonCompetition) {
                break;
            }
            parent = parent.getParent();
        }
        return name;
    }

    public void setEvents(List<CompetitionEvent> events) {
        this.events = events;
    }

    public List<CompetitionEvent> getAllEvents() {
        return getEvents();
    }

    public void assignParticipants(List<Knight> players) {
        assignedParticipants.addAll(players);
    }
}

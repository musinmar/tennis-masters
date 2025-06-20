package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.core.Knight;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Getter
@Setter
abstract public class AbstractCompetition implements Competition {
    private Competition parent;
    private final String id;
    private final String name;
    private List<CompetitionEvent> events = emptyList();
    private final List<Knight> assignedParticipants = new ArrayList<>();

    protected AbstractCompetition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void print(PrintStream stream) {
        stream.println(getName());
        stream.println();
    }

    public List<CompetitionEvent> getAllEvents() {
        return getEvents();
    }

    public void assignParticipants(List<Knight> players) {
        assignedParticipants.addAll(players);
    }
}

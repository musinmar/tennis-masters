package tm.lib.domain.competition.base;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.base.events.CompetitionEvent;
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

    public Competition getChild(String id) {
        throw new IllegalArgumentException("Competition " + getId() + " does not have child " + id);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println(getName());
        stream.println();
    }

    public String getFullName(boolean includeRoot) {
        String name = "";
        Competition current = this;
        while (true) {
            if (current == null) {
                break;
            }
            var rootCompetition = current.getParent() == null;
            if (rootCompetition && !includeRoot) {
                break;
            }
            name = name.isEmpty() ? current.getName() : current.getName() + " - " + name;
            current = current.getParent();
        }
        return name;
    }

    public List<CompetitionEvent> getAllEvents() {
        return getEvents();
    }

    public void assignParticipants(List<Knight> players) {
        assignedParticipants.addAll(players);
    }
}

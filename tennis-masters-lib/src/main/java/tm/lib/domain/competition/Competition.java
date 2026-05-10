package tm.lib.domain.competition;

import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public interface Competition {

    String getId();

    String getName();

    Competition getParent();

    void setParent(Competition parent);

    boolean hasChildren();

    List<Competition> getChildren();

    Optional<Competition> getChild(String id);

    void assignParticipants(List<Knight> players);

    List<Knight> getAssignedParticipants();

    Optional<MatchEvent> getNextMatch();

    List<MatchEvent> getAllMatches();

    default boolean isFinished() {
        return getNextMatch().isEmpty();
    }

    void processMatchResult(MatchEvent match, MatchResult result);

    void processChildCompetitionFinished(Competition child);

    void setEvents(List<CompetitionEvent> events);

    List<CompetitionEvent> getAllEvents();

    Knight getWinner();

    void print(PrintStream stream);

    default String getFullName(boolean includeRoot) {
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

    default Competition resolveCompetitionPath(String path) {
        return CompetitionPath.resolveCompetitionPath(this, path);
    }
}

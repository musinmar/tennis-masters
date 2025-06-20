package tm.lib.domain.competition.base;

import org.apache.commons.lang3.StringUtils;
import tm.lib.domain.competition.base.events.CompetitionEvent;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public interface Competition {

    String getId();

    String getName();

    Competition getParent();

    void setParent(Competition parent);

    Competition getChild(String id);

    int getStartingDate();

    void setStartingDate(int date);

    int getLastDate();

    void assignParticipants(List<Knight> players);

    List<Knight> getAssignedParticipants();

    Optional<MatchEvent> getNextMatch();

    List<MatchEvent> getAllMatches();

    void processMatchResult(MatchEvent match, MatchScore score);

    void setEvents(List<CompetitionEvent> events);

    List<CompetitionEvent> getAllEvents();

    Knight getWinner();

    void print(PrintStream stream);

    default String printToString() {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        print(printStream);
        return outputStream.toString();
    }

    default boolean isFinished() {
        return getNextMatch().isEmpty();
    }

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

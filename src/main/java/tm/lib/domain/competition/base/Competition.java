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
    String PATH_SEPARATOR = "/";
    String PARENT = "..";
    String SELF = "self";

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

    default boolean isFinished() {
        return getNextMatch().isEmpty();
    }

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

    default String getFullName(boolean includeSeason) {
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

    default Competition resolveCompetitionPath(String path) {
        var elements = StringUtils.split(path, PATH_SEPARATOR);
        Competition cur = this;
        for (var el : elements) {
            if (SELF.equals(el)) {
                return cur;
            } else if (PARENT.equals(el)) {
                cur = cur.getParent();
            } else {
                cur = cur.getChild(el);
            }
        }
        return cur;
    }

    static String buildPath(String... sections) {
        return StringUtils.join(sections, PATH_SEPARATOR);
    }
}

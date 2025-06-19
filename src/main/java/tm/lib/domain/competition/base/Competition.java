package tm.lib.domain.competition.base;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import tm.lib.domain.competition.base.events.CompetitionEvent;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

@Getter
abstract public class Competition {
    public static String PATH_SEPARATOR = "/";
    public static String PARENT = "..";
    public static String SELF = "self";

    private Competition parent;
    private final String id;
    private final String name;
    private List<CompetitionEvent> events = emptyList();
    private List<Knight> assignedParticipants = new ArrayList<>();

    private final List<Consumer<Competition>> competitionFinishedCallbacks = new ArrayList<>();

    protected Competition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected void setParent(Competition parent) {
        this.parent = parent;
    }

    public Competition getChild(String id) {
        throw new IllegalArgumentException("Competition " + getId() + " does not have child " + id);
    }

    public void registerOnFinishedCallback(Consumer<Competition> callback) {
        competitionFinishedCallbacks.add(callback);
    }

    public void print(PrintStream stream) {
        stream.println(getName());
        stream.println();
    }

    public String printToString() {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        print(printStream);
        return outputStream.toString();
    }

    abstract public Optional<MatchEvent> getNextMatch();

    abstract public List<MatchEvent> getAllMatches();

    abstract public void processMatchResult(MatchEvent match, MatchScore score);

    abstract public int getStartingDate();

    abstract public void setStartingDate(int date);

    public void setStartingDateAfter(Competition other) {
        setStartingDate(other.getLastDate() + 2);
    }

    abstract public int getLastDate();

    abstract public Knight getWinner();

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

    public boolean isFinished() {
        return getNextMatch().isEmpty();
    }

    public void setEvents(List<CompetitionEvent> events) {
        this.events = events;
    }

    public List<CompetitionEvent> getAllEvents() {
        return getEvents();
    }

    public Competition resolveCompetitionPath(String path) {
        var elements = StringUtils.split(path, PATH_SEPARATOR);
        var cur = this;
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

    public static String buildPath(String... sections) {
        return StringUtils.join(sections, PATH_SEPARATOR);
    }

    public void assignParticipants(List<Knight> players) {
        assignedParticipants.addAll(players);
    }
}

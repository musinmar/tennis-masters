package tm.lib.domain.competition.base;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
abstract public class Competition {
    public static String PARENT = "..";
    public static String PATH_SEPARATOR = "/";

    private Competition parent;
    private boolean isRoot;
    private final String id;
    private final String name;
    private Optional<SeedingTrigger> seedingTrigger = Optional.empty();

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

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
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

    public Competition getRootCompetition() {
        if (isRoot) {
            return this;
        } else {
            return parent.getRootCompetition();
        }
    }

    public void setSeedingTrigger(Optional<SeedingTrigger> seedingTrigger) {
        this.seedingTrigger = seedingTrigger;
    }

    public List<CompetitionTrigger> getAllTriggers() {
        return seedingTrigger.stream()
                .map(trigger -> new CompetitionTrigger(this, trigger))
                .toList();
    }

    public Competition resolveCompetitionPath(String path) {
        var elements = StringUtils.split(path, PATH_SEPARATOR);
        var cur = this;
        for (var el : elements) {
            if ("..".equals(el)) {
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
}

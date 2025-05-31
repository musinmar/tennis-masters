package tm.lib.domain.competition.base;

import lombok.Getter;
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
    private Competition parent;
    private boolean isRoot;
    private final String id;
    private final String name;

    private final List<Consumer<Competition>> competitionFinishedCallbacks = new ArrayList<>();

    protected Competition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected void setParent(Competition parent) {
        this.parent = parent;
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
}

package tm.lib.domain.competition.base;

import lombok.Getter;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
abstract public class Competition {
    private Competition parent;
    private boolean isRoot;
    private final String name;

    private List<Participant> participants;
    private final List<Consumer<Competition>> competitionFinishedCallbacks = new ArrayList<>();

    protected Competition(String name) {
        this.name = name;
    }

    protected void setParent(Competition parent) {
        this.parent = parent;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void registerOnFinishedCallback(Consumer<Competition> callback) {
        competitionFinishedCallbacks.add(callback);
    }

    public void setActualParticipants(List<Knight> players) {
        setActualParticipants(0, players);
    }

    public void setActualParticipants(int fromIndex, List<Knight> players) {
        for (int i = 0; i < players.size(); i++) {
            participants.get(fromIndex + i).setPlayer(players.get(i));
        }
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

    abstract public MatchEvent getNextMatch();

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
        if (getNextMatch() == null) {
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
        return getNextMatch() == null;
    }

    public Competition getRootCompetition() {
        if (isRoot) {
            return this;
        } else {
            return parent.getRootCompetition();
        }
    }
}

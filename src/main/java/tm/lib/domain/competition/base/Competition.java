package tm.lib.domain.competition.base;

import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

abstract public class Competition {
    private Competition parent;
    private final String name;

    private List<Participant> participants;
    private List<Runnable> competitionFinishedCallbacks = new LinkedList<>();

    protected Competition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Competition getParent() {
        return parent;
    }

    protected void setParent(Competition parent) {
        this.parent = parent;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void registerOnFinishedCallback(Runnable callback) {
        competitionFinishedCallbacks.add(callback);
    }

    public void setParticipantPrefix(String prefix) {
        for (int i = 0; i < participants.size(); i++) {
            participants.get(i).setId(prefix + (i + 1));
        }
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

    private void runOnFinishedCallbacks() {
        for (Runnable callback : competitionFinishedCallbacks) {
            callback.run();
        }
    }

    protected void checkIfCompetitionFinished() {
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
}

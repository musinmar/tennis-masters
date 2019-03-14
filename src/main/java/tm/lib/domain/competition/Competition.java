package tm.lib.domain.competition;

import tm.lib.domain.core.Person;
import tm.lib.domain.core.Stadium;
import tm.lib.domain.world.Season;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

abstract public class Competition implements IMatchEndListener {
    private Season season;
    private Competition parentCompetition;
    private String name;
    private List<Person> participants;
    private List<ICompetitionEndListener> listeners;

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

    abstract public Match getNextMatch();

    abstract public List<Match> getAllMatches();

    abstract public List<Person> getPositions();

    abstract public void setStartingDate(int date);

    protected void endCompetition() {
        for (ICompetitionEndListener listener : listeners) {
            listener.onCompetitionEnded(this);
        }
    }

    @Override
    public void onMatchEnded(Match match) {
        if (getNextMatch() == null) {
            endCompetition();
        }
    }

    public void setVenue(Stadium venue) {
        List<Match> matches = getAllMatches();
        for (Match match : matches) {
            match.setVenue(venue);
        }
    }

    Competition(Season season) {
        this.name = "Unnamed competition";
        this.season = season;
        parentCompetition = null;
        participants = null;
    }

    Competition(Competition parentCompetition) {
        this.parentCompetition = parentCompetition;
        this.name = "Unnamed competition";
        this.season = parentCompetition.season;
        participants = null;

        listeners = new LinkedList<ICompetitionEndListener>();
        if (parentCompetition instanceof ICompetitionEndListener) {
            listeners.add((ICompetitionEndListener) parentCompetition);
        }
    }

    public Season getSeason() {
        return season;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Competition getParentCompetition() {
        return parentCompetition;
    }

    public List<Person> getParticipants() {
        return participants;
    }

    protected void setParticipants(List<Person> participants) {
        this.participants = participants;
    }

    public void addCompetitionEndListener(ICompetitionEndListener listener) {
        listeners.add(listener);
    }
}

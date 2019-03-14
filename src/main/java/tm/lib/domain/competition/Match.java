package tm.lib.domain.competition;

import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Person;
import tm.lib.domain.core.Stadium;

import java.util.LinkedList;
import java.util.List;

public class Match {
    private int date;
    private final Competition competition;
    private Person player_1;
    private Person player_2;
    private int sets;
    private boolean playoff;
    private Stadium venue;
    private MatchScore result;
    private List<IMatchEndListener> listeners;

    public Match(Competition parent, Person p1, Person p2) {
        competition = parent;
        listeners = new LinkedList<IMatchEndListener>();
        listeners.add(competition);
        player_1 = p1;
        player_2 = p2;
        sets = 2;
        playoff = false;
        result = null;
    }

    public Match(Competition parent, Person p1, Person p2, int s, boolean is_playoff) {
        this(parent, p1, p2);
        sets = s;
        playoff = is_playoff;
    }

    /*public boolean is_playoff() {
     if (sets % 2 == 0)
     return false;
     else 
     return true;
     }*/
    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Competition getCompetition() {
        return competition;
    }

    public Person getFirstPlayer() {
        return player_1;
    }

    public void setFirstPlayer(Person person) {
        player_1 = person;
    }

    public Person getSecondPlayer() {
        return player_2;
    }

    public void setSecondPlayer(Person person) {
        player_2 = person;
    }

    public int getSets() {
        return sets;
    }

    public boolean isPlayoff() {
        return playoff;
    }

    public Stadium getVenue() {
        return venue;
    }

    public void setVenue(Stadium venue) {
        this.venue = venue;
    }

    public MatchScore getResult() {
        return result;
    }

    @Override
    public String toString() {
        String resultString = getResult() == null ? "" : " " + getResult().toString();
        String firstPlayerName = getFirstPlayer() == null ? "TBD" : getFirstPlayer().getFullName();
        String secondPlayerName = getSecondPlayer() == null ? "TBD" : getSecondPlayer().getFullName();
        return firstPlayerName + " - " + secondPlayerName + resultString;
    }

    public void addMatchEndListener(IMatchEndListener listener) {
        listeners.add(listener);
    }

    public void setResult(MatchScore result) {
        this.result = result;
        for (IMatchEndListener listener : listeners) {
            listener.onMatchEnded(this);
        }
    }
}

package tm.lib.domain.competition;

import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.Person;
import tm.lib.domain.core.Stadium;

import java.util.LinkedList;
import java.util.List;

public class MatchEvent {
    private int date;
    private final Competition competition;
    private Person homePlayer;
    private Person awayPlayer;
    private int sets;
    private boolean playoff;
    private Stadium venue;
    private MatchScore result;
    private List<IMatchEndListener> listeners = new LinkedList<>();

    public MatchEvent(Competition parent, Person p1, Person p2) {
        competition = parent;
        listeners.add(competition);
        homePlayer = p1;
        awayPlayer = p2;
        sets = 2;
        playoff = false;
        result = null;
    }

    public MatchEvent(Competition parent, Person p1, Person p2, int sets, boolean isPlayoff) {
        this(parent, p1, p2);
        this.sets = sets;
        playoff = isPlayoff;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Competition getCompetition() {
        return competition;
    }

    public Person getHomePlayer() {
        return homePlayer;
    }

    public void setHomePlayer(Person homePlayer) {
        this.homePlayer = homePlayer;
    }

    public Person getAwayPlayer() {
        return awayPlayer;
    }

    public void setAwayPlayer(Person awayPlayer) {
        this.awayPlayer = awayPlayer;
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
        String firstPlayerName = getHomePlayer() == null ? "TBD" : getHomePlayer().getFullName();
        String secondPlayerName = getAwayPlayer() == null ? "TBD" : getAwayPlayer().getFullName();
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

    public Match createMatchSpec() {
        Match match = new Match();
        match.setHomePlayer(homePlayer);
        match.setAwayPlayer(awayPlayer);
        match.setSets(sets);
        match.setPlayoff(playoff);
        match.setVenue(venue);
        return match;
    }
}

package tm.lib.domain.competition.base;

import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Stadium;

public class MatchEvent {
    private static boolean PRINT_DATE = true;

    private int date;
    private final Competition competition;
    private final Participant homePlayer;
    private final Participant awayPlayer;
    private int sets;
    private boolean playOff;
    private Stadium venue = Stadium.standard();
    private MatchScore result;

    public MatchEvent(Competition parent, Participant p1, Participant p2, int sets, boolean isPlayoff) {
        this(parent, p1, p2);
        this.sets = sets;
        playOff = isPlayoff;
    }

    public MatchEvent(Competition parent, Participant p1, Participant p2) {
        competition = parent;
        homePlayer = p1;
        awayPlayer = p2;
        sets = 2;
        playOff = false;
        result = null;
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

    public Participant getHomePlayer() {
        return homePlayer;
    }

    public Participant getAwayPlayer() {
        return awayPlayer;
    }

    public int getSets() {
        return sets;
    }

    public boolean isPlayOff() {
        return playOff;
    }

    public Stadium getVenue() {
        return venue;
    }

    public MatchScore getResult() {
        return result;
    }

    @Override
    public String toString() {
        String resultString = getResult() == null ? "" : " " + getResult().toString();
        String firstPlayerName = getHomePlayer() == null ? "TBD" : getHomePlayer().getFullNameOrId();
        String secondPlayerName = getAwayPlayer() == null ? "TBD" : getAwayPlayer().getFullNameOrId();
        String date = PRINT_DATE ? "(" + getDate() + ") " : "";
        return date + firstPlayerName + " - " + secondPlayerName + resultString;
    }

    public void setResult(MatchScore result) {
        this.result = result;
    }

    public Match createMatchSpec() {
        Match match = new Match();
        match.setHomePlayer(homePlayer.getPlayerOrFail());
        match.setAwayPlayer(awayPlayer.getPlayerOrFail());
        match.setSets(sets);
        match.setPlayOff(playOff);
        match.setVenue(venue);
        return match;
    }
}

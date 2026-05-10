package tm.lib.domain.competition;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.Stadium;

@Getter
@Setter
public class MatchEvent {
    private static final boolean PRINT_DATE = true;

    private int date;
    private final Competition competition;
    private final Participant homePlayer;
    private final Participant awayPlayer;
    private int sets;
    private boolean playOff;
    private final Stadium venue = Stadium.standard();
    private MatchResult result;

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

    @Override
    public String toString() {
        String resultString = getResult() == null ? "" : " " + getResult();
        String firstPlayerName = getHomePlayer() == null ? "TBD" : getHomePlayer().getFullNameOrId();
        String secondPlayerName = getAwayPlayer() == null ? "TBD" : getAwayPlayer().getFullNameOrId();
        String date = PRINT_DATE ? "(" + getDate() + ") " : "";
        return date + firstPlayerName + " - " + secondPlayerName + resultString;
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

package tm.lib.domain.core;

public class Match {
    private Knight homePlayer;
    private Knight awayPlayer;
    private int sets;
    private boolean isPlayOff;
    private Stadium venue;

    public Knight getHomePlayer() {
        return homePlayer;
    }

    public void setHomePlayer(Knight homePlayer) {
        this.homePlayer = homePlayer;
    }

    public Knight getAwayPlayer() {
        return awayPlayer;
    }

    public void setAwayPlayer(Knight awayPlayer) {
        this.awayPlayer = awayPlayer;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public boolean isPlayOff() {
        return isPlayOff;
    }

    public void setPlayOff(boolean playOff) {
        isPlayOff = playOff;
    }

    public Stadium getVenue() {
        return venue;
    }

    public void setVenue(Stadium venue) {
        this.venue = venue;
    }
}

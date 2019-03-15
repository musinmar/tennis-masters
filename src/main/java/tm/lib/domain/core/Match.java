package tm.lib.domain.core;

public class Match {
    private Person homePlayer;
    private Person awayPlayer;
    private int sets;
    private boolean isPlayoff;
    private Stadium venue;

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

    public void setSets(int sets) {
        this.sets = sets;
    }

    public boolean isPlayoff() {
        return isPlayoff;
    }

    public void setPlayoff(boolean playoff) {
        isPlayoff = playoff;
    }

    public Stadium getVenue() {
        return venue;
    }

    public void setVenue(Stadium venue) {
        this.venue = venue;
    }
}

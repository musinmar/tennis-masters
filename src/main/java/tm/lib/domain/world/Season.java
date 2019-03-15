package tm.lib.domain.world;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.SeasonCompetition;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;

public class Season {
    private static final int SEASON_DAYS = 200;
    private GameWorld world;
    private Competition seasonCompetition;
    private int year;

    public Season(GameWorld world, int year) {
        this.world = world;
        this.year = year;

        initSeasonCompetition();
    }

    public Competition getSeasonCompetition() {
        return seasonCompetition;
    }

    public MatchEvent getNextMatch() {
        return seasonCompetition.getNextMatch();
    }

    public void processMatch(MatchEvent m, MatchScore s) {
        m.setResult(s);
        print(System.out);
    }

    public void print(PrintStream stream) {
        seasonCompetition.print(stream);
    }

    private void initSeasonCompetition() {
        seasonCompetition = new SeasonCompetition(this,"Сезон " + String.valueOf(year + 1), world.getPlayers());
    }
}

package tm.lib.domain.world;

import tm.lib.domain.competition.SeasonCompetition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;

public class Season {
    private static final int SEASON_DAYS = 200;
    private GameWorld gameWorld;
    private MultiStageCompetition seasonCompetition;
    private int year;

    public Season(GameWorld gameWorld, int year) {
        this.gameWorld = gameWorld;
        this.year = year;

        initSeasonCompetition();
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public MultiStageCompetition getSeasonCompetition() {
        return seasonCompetition;
    }

    public MatchEvent getNextMatch() {
        return seasonCompetition.getNextMatch();
    }

    public void processMatch(MatchEvent match, MatchScore score) {
        match.setResult(score);
        gameWorld.getEloRating().updateRatings(match.getHomePlayer().getPlayer(), match.getAwayPlayer().getPlayer(), score.getScoreBySets());

//        print(System.out);
    }

    public void print(PrintStream stream) {
        seasonCompetition.print(stream);
    }

    private void initSeasonCompetition() {
        seasonCompetition = new SeasonCompetition(this, "Сезон " + String.valueOf(year + 1), gameWorld.getPlayers());
    }
}

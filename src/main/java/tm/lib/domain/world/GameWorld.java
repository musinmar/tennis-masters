package tm.lib.domain.world;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tm.lib.domain.competition.SeasonCompetition;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.standard.GroupSubStage;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

import static tm.lib.domain.world.GameWorldLogger.NoopLogger;

public class GameWorld {
    private GameWorldLogger logger = NoopLogger;

    private List<SeasonCompetition> seasons = new ArrayList<SeasonCompetition>();
    private List<Knight> players = new ArrayList<Knight>();

    private EloRating eloRating;
    private NationRating nationRating;

    private int year = 0;

    private Competition latestCompetition;

    public GameWorld() {
        init();
        eloRating = new EloRating(players);
        nationRating = new NationRating();
        nationRating.initDefault();
        nationRating.calculateRankingsAndPrint();
        seasons.add(new SeasonCompetition( "Сезон " + (year + 1), this, getPlayers()));
    }

    public GameWorldLogger getLogger() {
        return logger;
    }

    public void setLogger(GameWorldLogger logger) {
        this.logger = logger;
    }

    public List<SeasonCompetition> getSeasons() {
        return seasons;
    }

    public SeasonCompetition getCurrentSeason() {
        return seasons.get(seasons.size() - 1);
    }

    public List<Knight> getPlayers() {
        return players;
    }

    public EloRating getEloRating() {
        return eloRating;
    }

    public NationRating getNationRating() {
        return nationRating;
    }

    private void init() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse("players.xml");
            NodeList playerNodes = document.getElementsByTagName("player");
            for (int i = 0; i < playerNodes.getLength(); ++i) {
                Element playerElement = (Element) playerNodes.item(i);
                Knight knight = new Knight();
                knight.init(playerElement);
                players.add(knight);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void processMatch(MatchEvent match, MatchScore score) {
        if (match.getCompetition() != latestCompetition) {
            logger.println();
            logger.println(match.getCompetition().getFullName(false));
            logger.println();
        }
        latestCompetition = match.getCompetition();

        match.getCompetition().processMatchResult(match, score);
        getEloRating().updateRatings(match.getHomePlayer().getPlayer(), match.getAwayPlayer().getPlayer(), score.getScoreBySets());

        logger.println(match.toString());

        if (match.getCompetition() instanceof GroupSubStage && match.getCompetition().isFinished()) {
            GroupSubStage groupSubStage = (GroupSubStage) match.getCompetition();
            logger.println();
            logger.println(groupSubStage.printGroupResultsToString());
        }
    }
}

package tm.lib.domain.world;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static tm.lib.domain.world.GameWorldLogger.NoopLogger;

public class GameWorld {
    private static final String FOLDER = "season";
    //    private static final String FILE_NAME_SEASON_JSON = "season.json";
//    private static final String FILE_NAME_RATING = "rating";
    private static final String FILE_NAME_RATING_CHANGE = "rating change";
//    private static final String FILE_NAME_STATS = "stats";

    private GameWorldLogger logger = NoopLogger;

    private List<SeasonCompetition> seasons = new ArrayList<SeasonCompetition>();
    private List<Knight> players = new ArrayList<Knight>();

    private EloRating eloRating;
    private NationRating nationRating;

    private int year = 0;

    private Competition latestCompetition;
    private Map<Competition, Integer> competitionPointValues;

    private boolean isSeasonFinished = false;

    public GameWorld() {
        initNewGame();

        SeasonCompetition season = new SeasonCompetition("Сезон " + (year + 1), this, getPlayers());
        season.setStartingDate(0);
        seasons.add(season);

        initCompetitionPointValues();
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

    public boolean isSeasonFinished() {
        return isSeasonFinished;
    }

    private void initNewGame() {
        year = 0;
        players = loadDefaultPlayers();
        eloRating = new EloRating(players);

        nationRating = new NationRating();
        nationRating.initDefault();
        nationRating.calculateRankingsAndPrint();
    }

    private static List<Knight> loadDefaultPlayers() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse("players.xml");
            NodeList playerNodes = document.getElementsByTagName("player");
            List<Knight> knights = new ArrayList<>();
            for (int i = 0; i < playerNodes.getLength(); ++i) {
                Element playerElement = (Element) playerNodes.item(i);
                Knight knight = new Knight();
                knight.init(playerElement);
                knights.add(knight);
            }
            return knights;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initCompetitionPointValues() {
        SeasonCompetition s = getCurrentSeason();
        competitionPointValues = ImmutableMap.<Competition, Integer>builder()
                .put(s.getChampionsLeague().getFirstQualifyingStage(), 2)
                .put(s.getFederationsCup().getFirstQualifyingStage(), 1)
                .put(s.getChampionsLeague().getSecondQualifyingStage(), 2)
                .put(s.getFederationsCup().getSecondQualifyingStage(), 1)
                .put(s.getFederationsCup().getGroupStage(), 2)
                .put(s.getChampionsLeague().getGroupStage(), 4)
                .put(s.getFederationsCup().getPlayoffStage(), 2)
                .put(s.getChampionsLeague().getPlayoffStage(), 4)
                .build();
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
        updateNationRating(match, score);

        logger.println(match.toString());

        if (match.getCompetition() instanceof GroupSubStage) {
            GroupSubStage groupSubStage = (GroupSubStage) match.getCompetition();
            if (groupSubStage.doesNextMatchStartNewRound()) {
                if (groupSubStage.isFinished()) {
                    logger.println();
                    logger.println("Итоговое положение");
                }
                logger.println();
                logger.println(groupSubStage.printGroupResultsToString());
            }
        }
    }

    private void updateNationRating(MatchEvent match, MatchScore score) {
        Integer points = getCompetitionPoints(match.getCompetition());
        if (points != null) {
            nationRating.updateRatings(
                    match.getHomePlayer().getPlayer(),
                    match.getAwayPlayer().getPlayer(),
                    score.getScoreBySets(),
                    points);
        }
    }

    private Integer getCompetitionPoints(Competition competition) {
        Integer points = competitionPointValues.get(competition);
        if (points == null) {
            points = competitionPointValues.get(competition.getParent());
        }
        return points;
    }

    public void finishSeason() {
        Validate.isTrue(!isSeasonFinished);

        saveToFile(makeFilename(FILE_NAME_RATING_CHANGE, true, true),
                writer -> getEloRating().print(writer, true));

        year += 1;
        getNationRating().advanceYear();

        logger.println();
        getNationRating().printPointHistory(logger);
        logger.println("Season finished");

        isSeasonFinished = true;
    }

    private String makeFilename(String s, boolean withYear, boolean txtExtension) {
        Path folderPath = Paths.get(FOLDER);
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String ret = s;
        if (withYear) {
            ret += " " + (year + 1);
        }
        if (txtExtension) {
            ret += ".txt";
        }
        return folderPath.resolve(ret).toAbsolutePath().toString();
    }

    private void saveToFile(String filename, Consumer<PrintWriter> writerConsumer) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writerConsumer.accept(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package tm.lib.domain.world;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.impl.GroupSubStage;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableMap;
import static tm.lib.domain.competition.CompetitionBuilder.buildCompetition;
import static tm.lib.domain.world.StandardSeasonBuilder.buildCompetitionPoints;
import static tm.lib.domain.world.StandardSeasonBuilder.buildSeasonCompetitionDefinition;
import static tm.lib.domain.world.WorldLogger.NoopLogger;
import static tm.lib.persistence.PersistenceManager.loadDefaultPlayers;

@Getter
@Setter
public class World {
    private static final String FOLDER = "season";
    //    private static final String FILE_NAME_SEASON_JSON = "season.json";
//    private static final String FILE_NAME_RATING = "rating";
    private static final String FILE_NAME_RATING_CHANGE = "rating change";
//    private static final String FILE_NAME_STATS = "stats";

    private WorldLogger logger = NoopLogger;

    private int year = 0;
    private boolean isSeasonFinished = false;

    private Competition seasonCompetition;
    private Map<Competition, Integer> competitionPointValues;

    private KnightRepository knightRepository;
    private EloRating eloRating;
    private NationRating nationRating;

    private WorldEventProcessor eventProcessor;

    private Competition latestCompetition;


    public World() {
    }

    public static World createNewWorld() {
        World world = new World();
        world.initNewGame();
        world.startNewSeason();
        return world;
    }

    public Competition getCurrentSeason() {
        return seasonCompetition;
    }

    private void initNewGame() {
        year = 0;
        knightRepository = new KnightRepository();
        var knights = loadDefaultPlayers();
        knights.forEach(Knight::randomizeSkills);
        knightRepository.setKnights(knights);

        eloRating = new EloRating(knightRepository.getAll());
        eloRating.setPreviousYearValues();

        nationRating = new NationRating();
        nationRating.initializeDefault();
    }

    public void startNewSeason() {
        nationRating.printStartOfSeasonFederationsRanking(logger);
        initCompetitions();
        isSeasonFinished = false;
        initializeEventProcessor();
        eventProcessor.processOnSeasonStartEvents();
    }

    private void initCompetitions() {
        seasonCompetition = buildCompetition(buildSeasonCompetitionDefinition(year), year);
        initCompetitionPointValues(buildCompetitionPoints());
    }

    public void initializeEventProcessor() {
        eventProcessor = new WorldEventProcessor(year, knightRepository, nationRating, eloRating, seasonCompetition);
    }

    public void initCompetitionPointValues(Map<String, Integer> pointsByPath) {
        Competition season = getCurrentSeason();
        var pointsByCompetition = new HashMap<Competition, Integer>();
        pointsByPath.forEach((path, value) -> pointsByCompetition.put(season.resolveCompetitionPath(path), value));
        competitionPointValues = unmodifiableMap(pointsByCompetition);
    }

    public void processMatch(MatchEvent match, MatchScore score) {
        if (match.getCompetition() != latestCompetition) {
            logger.println();
            logger.println(match.getCompetition().getFullName(false));
            logger.println();
        }
        latestCompetition = match.getCompetition();

        match.getCompetition().processMatchResult(match, score);
        eventProcessor.processOnCompetitionEndEvents(match.getCompetition());
        getEloRating().updateRatings(match.getHomePlayer().getPlayerOrFail(), match.getAwayPlayer().getPlayerOrFail(), score.getScoreBySets());
        updateNationRating(match, score);

        logger.println(match.toString());

        if (match.getCompetition() instanceof GroupSubStage groupSubStage) {
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
        nationRating.updateRatings(
                match.getHomePlayer().getPlayerOrFail(),
                match.getAwayPlayer().getPlayerOrFail(),
                score.getScoreBySets(),
                points);
    }

    private Integer getCompetitionPoints(Competition competition) {
        var current = competition;
        while (current != null) {
            Integer points = competitionPointValues.get(current);
            if (points != null) {
                return points;
            }
            current = current.getParent();
        }
        return 0;
    }

    public void finishSeason() {
        Validate.isTrue(!isSeasonFinished);

        saveToFile(makeFilename(FILE_NAME_RATING_CHANGE, true, true),
                writer -> getEloRating().print(writer, true));

        year += 1;
        logger.println();
        getNationRating().printTotalPointsReceived(logger);
        getNationRating().advanceYear();

        logger.println();
        getNationRating().printPointHistory(logger);
        logger.println("Сезон завершён");

        getEloRating().setPreviousYearValues();

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

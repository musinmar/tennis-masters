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
import static java.util.stream.Collectors.toList;
import static tm.lib.domain.competition.CompetitionBuilder.buildCompetition;
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

    private Competition seasonCompetition;

    private List<Knight> players = new ArrayList<Knight>();
    private EloRating eloRating;
    private NationRating nationRating;

    private WorldEventProcessor eventProcessor;

    private int year = 0;

    private Competition latestCompetition;
    private Map<Competition, Integer> competitionPointValues;

    private boolean isSeasonFinished = false;

    public World() {
    }

    public static World createNewWorld() {
        World world = new World();
        world.initNewGame();
        world.startNewSeason();
//        PersistenceManager.saveDefaultPlayers(world.getPlayers());
        return world;
    }


    public Competition getCurrentSeason() {
        return seasonCompetition;
    }

    private void initNewGame() {
        year = 0;
        players = loadDefaultPlayers();
        players.forEach(Knight::randomizeSkills);

        eloRating = new EloRating(players);

        nationRating = new NationRating();
        nationRating.initDefault();
    }

    public void startNewSeason() {
        nationRating.calculateRankingsAndPrint();
        initCompetitions();
        isSeasonFinished = false;
        eventProcessor.processOnSeasonStartEvents();
    }

    private void initCompetitions() {
//        seasonCompetition = new SeasonCompetition("Сезон " + (year + 1), this, getPlayers(), year);
//        seasonCompetition.setStartingDate(0);
        seasonCompetition = buildCompetition(buildSeasonCompetitionDefinition(), year);
        eventProcessor = new WorldEventProcessor(year, getPlayers(), nationRating, eloRating, seasonCompetition);
        initCompetitionPointValues();
    }

    private void initCompetitionPointValues() {
        Competition season = getCurrentSeason();
        var pointsByCompetition = new HashMap<Competition, Integer>();
        var pointsByPath = StandardSeasonBuilder.buildCompetitionPoints();
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
                    match.getHomePlayer().getPlayerOrFail(),
                    match.getAwayPlayer().getPlayerOrFail(),
                    score.getScoreBySets(),
                    points);
        }
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
        getNationRating().advanceYear();

        logger.println();
        getNationRating().printPointHistory(logger);
        logger.println("Season finished");

        getEloRating().advanceYear();

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

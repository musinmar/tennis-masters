package tm.lib.domain.world;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.impl.GroupCompetition;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.event.SeasonConclusion;
import tm.lib.domain.world.event.SeasonMatchEvent;
import tm.lib.domain.world.event.SeasonStart;
import tm.lib.domain.world.event.WorldEvent;
import tm.lib.domain.world.event.YearProgression;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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

    private int year;
    private WorldState state;

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
        return world;
    }

    public Competition getCurrentSeason() {
        return seasonCompetition;
    }

    private void initNewGame() {
        knightRepository = new KnightRepository();
        var knights = loadDefaultPlayers();
        knights.forEach(Knight::randomizeSkills);
        knightRepository.setKnights(knights);

        eloRating = new EloRating(knightRepository.getAll());

        nationRating = new NationRating();
        nationRating.initializeDefault();

        year = 0;
        initializeNewYear();
    }

    private void startNewSeason() {
        logger.println("Starting new season " + (year + 1));
        logger.println();
        nationRating.printStartOfSeasonFederationsRanking(logger);
        eventProcessor.processOnSeasonStartEvents();
        state = WorldState.SEASON_IN_PROGRESS;
    }

    private void concludeSeason() {
        saveToFile(makeFilename(FILE_NAME_RATING_CHANGE, true, true),
                writer -> getEloRating().print(writer, true));

        logger.println();
        getNationRating().printTotalPointsReceived(logger);

        logger.println();
        getNationRating().printPointHistory(logger);
        logger.println("Season completed");
        logger.println();

        state = WorldState.SEASON_CONCLUDED;
    }

    private void advanceYear() {
        year += 1;
        getNationRating().advanceYear();
        initializeNewYear();
    }

    private void initializeNewYear() {
        getEloRating().setPreviousYearValues();
        initCompetitions();
        initializeEventProcessor();
        state = WorldState.SEASON_INITIALIZED;
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

    public WorldEvent getNextEvent() {
        switch (state) {
            case SEASON_IN_PROGRESS -> {
                var nextMatch = seasonCompetition.getNextMatch();
                if (nextMatch.isPresent()) {
                    var seasonMatchEvent = new SeasonMatchEvent();
                    seasonMatchEvent.setMatchEvent(nextMatch.get());
                    return seasonMatchEvent;
                } else {
                    return SeasonConclusion.INSTANCE;
                }
            }
            case SEASON_CONCLUDED -> {
                return YearProgression.INSTANCE;
            }
            case SEASON_INITIALIZED -> {
                return SeasonStart.INSTANCE;
            }
            case null, default -> throw new IllegalStateException("Unknown world state: " + state);
        }
    }

    public void progressState() {
        var nextEvent = getNextEvent();
        switch (nextEvent) {
            case SeasonStart _ -> startNewSeason(); // start season
            case SeasonConclusion _ -> concludeSeason(); // conclude season
            case YearProgression _ -> advanceYear(); // progress year
            case MatchEvent _ ->
                    throw new IllegalStateException("Can't progress state because not all matches were played");
            case null, default -> throw new IllegalStateException("Unknown world event type: " + nextEvent);
        }
    }

    public void processMatch(MatchEvent match, MatchScore score) {
        var nextEvent = getNextEvent();
        if (!(nextEvent instanceof SeasonMatchEvent seasonMatchEvent) || seasonMatchEvent.getMatchEvent() != match) {
            throw new IllegalStateException("Next event is expected to be: " + nextEvent);
        }

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

        if (match.getCompetition() instanceof GroupCompetition groupCompetition) {
            if (groupCompetition.doesNextMatchStartNewRound()) {
                if (groupCompetition.isFinished()) {
                    logger.println();
                    logger.println("Final standings");
                }
                logger.println();
                logger.println(groupCompetition.printGroupResultsToString());
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

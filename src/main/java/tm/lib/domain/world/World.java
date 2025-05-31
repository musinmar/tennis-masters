package tm.lib.domain.world;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import tm.lib.domain.competition.SeasonCompetition;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.standard.GroupSubStage;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.dto.WorldDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static tm.lib.domain.world.PersistenceManager.loadDefaultPlayers;
import static tm.lib.domain.world.WorldLogger.NoopLogger;

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

    private int year = 0;

    private Competition latestCompetition;
    private Map<Competition, Integer> competitionPointValues;

    private boolean isSeasonFinished = false;

    private World() {
    }

    public static World createNewWorld() {
        World world = new World();
        world.initNewGame();
        world.startNewSeason();
//        PersistenceManager.saveDefaultPlayers(world.getPlayers());
        return world;
    }

    public static World fromDto(WorldDto dto) {
        World world = new World();

        world.year = dto.getYear();
        world.players = dto.getKnights().stream().map(Knight::fromDto).collect(toList());
        world.eloRating = EloRating.fromDto(dto.getEloRating(), world.players);
        world.nationRating = NationRating.fromDto(dto.getNationRating());

        world.startNewSeason();

        return world;
    }

    public WorldDto toDto() {
        WorldDto dto = new WorldDto();

        dto.setYear(year);
        dto.setKnights(players.stream().map(Knight::toDto).collect(toList()));
        dto.setEloRating(eloRating.toDto());
        dto.setNationRating(nationRating.toDto());

        return dto;
    }

    public WorldLogger getLogger() {
        return logger;
    }

    public void setLogger(WorldLogger logger) {
        this.logger = logger;
    }

    public Competition getCurrentSeason() {
        return seasonCompetition;
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

    public int getYear() {
        return year;
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
    }

    private void initCompetitions() {
        seasonCompetition = new SeasonCompetition("Сезон " + (year + 1), this, getPlayers(), year);
        seasonCompetition.setStartingDate(0);

        initCompetitionPointValues();
    }

    private void initCompetitionPointValues() {
        Competition s = getCurrentSeason();
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
            Integer points = competitionPointValues.get(competition);
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

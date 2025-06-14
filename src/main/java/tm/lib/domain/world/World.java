package tm.lib.domain.world;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.CompetitionTrigger;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.triggers.SeedingRules;
import tm.lib.domain.competition.base.triggers.TriggerTimes;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupSubStage;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.competition.standard.PlayoffSubStage;
import tm.lib.domain.competition.standard.PlayoffUtils;
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

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static tm.lib.domain.competition.StandardSeasonBuilder.buildSeasonCompetitionDefinition;
import static tm.lib.domain.competition.base.CompetitionBuilder.buildCompetition;
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
    private List<CompetitionTrigger> seasonStartTriggers;
    private Map<Competition, List<CompetitionTrigger>> onCompetitionEndTriggers;

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
        processStartOfSeasonTriggers();
    }

    private void initCompetitions() {
//        seasonCompetition = new SeasonCompetition("Сезон " + (year + 1), this, getPlayers(), year);
//        seasonCompetition.setStartingDate(0);
        seasonCompetition = buildCompetition(buildSeasonCompetitionDefinition());
        seasonCompetition.setStartingDate(0);
        var allTriggers = seasonCompetition.getAllTriggers();
        this.seasonStartTriggers = allTriggers.stream()
                .filter(t -> t.getTrigger().getTriggerTime() instanceof TriggerTimes.SeasonStartTriggerTime)
                .toList();
        this.onCompetitionEndTriggers = allTriggers.stream()
                .filter(t -> t.getTrigger().getTriggerTime() instanceof TriggerTimes.CompetitionEndedTriggerTime)
                .collect(groupingBy((CompetitionTrigger t) -> {
                    var competitionEnd = (TriggerTimes.CompetitionEndedTriggerTime) t.getTrigger().getTriggerTime();
                    var path = competitionEnd.getCompetitionPath();
                    return resolveCompetitionPath(path, t.getCompetition());
                }, toList()));
        initCompetitionPointValues();
    }

    private Competition resolveCompetitionPath(String path, Competition from) {
        var elements = StringUtils.split(path, '/');
        var cur = from;
        for (var el : elements) {
            if ("..".equals(el)) {
                cur = cur.getParent();
            } else {
                cur = cur.getChild(el);
            }
        }
        return cur;
    }

    private void initCompetitionPointValues() {
        Competition s = getCurrentSeason();
        competitionPointValues = ImmutableMap.<Competition, Integer>builder()
//                .put(s.getChampionsLeague().getFirstQualifyingStage(), 2)
//                .put(s.getFederationsCup().getFirstQualifyingStage(), 1)
//                .put(s.getChampionsLeague().getSecondQualifyingStage(), 2)
//                .put(s.getFederationsCup().getSecondQualifyingStage(), 1)
//                .put(s.getFederationsCup().getGroupStage(), 2)
//                .put(s.getChampionsLeague().getGroupStage(), 4)
//                .put(s.getFederationsCup().getPlayoffStage(), 2)
//                .put(s.getChampionsLeague().getPlayoffStage(), 4)
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
        processOnCompetitionEndTriggers(match.getCompetition());
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

    private void processStartOfSeasonTriggers() {
        seasonStartTriggers.forEach(this::processTrigger);
    }

    private void processOnCompetitionEndTriggers(Competition competition) {
        var cur = competition;
        while (cur != null) {
            if (!cur.isFinished()) {
                break;
            }
            var triggers = onCompetitionEndTriggers.get(cur);
            if (triggers != null) {
                triggers.forEach(this::processTrigger);
            }
            cur = cur.getParent();
        }
    }

    private void processTrigger(CompetitionTrigger trigger) {
        switch (trigger.getTrigger().getSeedingRule()) {
            case SeedingRules.RandomSelection _ -> processRandomSelectionTrigger(trigger.getCompetition());
            case SeedingRules.GroupStageToPlayOff groupStageToPlayOff ->
                    processGroupStageToPlayOffTrigger(trigger.getCompetition(), groupStageToPlayOff);
            case SeedingRules.PlayOffToPlayOff playOffToPlayOff ->
                processPlayOffToPlayOffTrigger(trigger.getCompetition(), playOffToPlayOff);
            default -> throw new IllegalArgumentException("Unknown seeding trigger rule: " +
                    trigger.getTrigger().getSeedingRule());
        }
    }

    private void processPlayOffToPlayOffTrigger(Competition competition, SeedingRules.PlayOffToPlayOff playOffToPlayOff) {
        var source = resolveCompetitionPath(playOffToPlayOff.getPlayOffSubStagePath(), competition);
        if (!(source instanceof PlayoffSubStage sourceStage)) {
            var message = String.format("Path %s is not pointing to a play-off sub stage",
                    playOffToPlayOff.getPlayOffSubStagePath());
            throw new IllegalArgumentException(message);
        } else if (!(competition instanceof PlayoffSubStage targetStage)) {
            var message = String.format("Competition %s is not a play-off stage", competition.getId());
            throw new IllegalArgumentException(message);
        } else {
            targetStage.setActualParticipants(sourceStage.getResults().getWinners());
        }
    }

    private void processGroupStageToPlayOffTrigger(Competition competition, SeedingRules.GroupStageToPlayOff groupStageToPlayOff) {
        var source = resolveCompetitionPath(groupStageToPlayOff.getGroupStagePath(), competition);
        if (!(source instanceof GroupStage groupStage)) {
            var message = String.format("Path %s is not pointing to a group stage",
                    groupStageToPlayOff.getGroupStagePath());
            throw new IllegalArgumentException(message);
        } else if (!(competition instanceof PlayoffStage playOff)) {
            var message = String.format("Competition %s is not a play-off stage", competition.getId());
            throw new IllegalArgumentException(message);
        } else {
            var pairs = PlayoffUtils.drawPlayersInPairsFromGroupResults(groupStage.getResults());
            playOff.setActualParticipants(pairs);
        }
    }

    private void processRandomSelectionTrigger(Competition competition) {
        var playerPool = new ArrayList<>(this.players);
        shuffle(playerPool);
        switch (competition) {
            case GroupStage groupStage -> {
                var selected = playerPool.subList(0, groupStage.getParticipantCount());
                groupStage.setActualParticipants(selected);
            }
            case PlayoffStage playoffStage -> {
                var selected = playerPool.subList(0, playoffStage.getParticipantCount());
                playoffStage.setActualParticipants(selected);
            }
            default -> {
                var message = "Random selection trigger does not support competition type: " + competition.getClass();
                throw new IllegalArgumentException(message);
            }
        }
    }
}

package tm.lib.domain.world;

import lombok.Getter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.CompetitionEvent;
import tm.lib.domain.competition.definition.actions.AddAssignedParticipantsFromEloRankings;
import tm.lib.domain.competition.definition.actions.AwardTitle;
import tm.lib.domain.competition.definition.actions.GiveTrophy;
import tm.lib.domain.competition.definition.actions.SeedAllFromNationRule;
import tm.lib.domain.competition.definition.actions.SeedBasedOnNationalRankingRule;
import tm.lib.domain.competition.definition.actions.SeedGroupStageToPlayOff;
import tm.lib.domain.competition.definition.actions.SeedPlayOffToPlayOff;
import tm.lib.domain.competition.definition.actions.SeedRandomPlayers;
import tm.lib.domain.competition.definition.actions.SeedTitlePlayOffParticipants;
import tm.lib.domain.competition.definition.actions.SeedWorldCupRound;
import tm.lib.domain.competition.definition.triggers.OnCompetitionEnd;
import tm.lib.domain.competition.definition.triggers.OnSeasonStart;
import tm.lib.domain.competition.impl.GroupCompetition;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.PlayOffCompetition;
import tm.lib.domain.competition.impl.PlayOffStage;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;
import tm.lib.domain.core.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static tm.lib.domain.world.GroupStageUtils.makeSeededGroupsTwoByFour;

@Getter
public class WorldEventProcessor {

    private final World world;
    private final Map<Competition, List<CompetitionEvent>> onCompetitionEndEvents;
    private final List<CompetitionEvent> onSeasonStartEvents;

    public WorldEventProcessor(World world) {
        this.world = world;

        var allEvents = world.getSeasonCompetition().getAllEvents();
        this.onSeasonStartEvents = allEvents.stream()
                .filter(t -> t.getTrigger() instanceof OnSeasonStart)
                .toList();
        this.onCompetitionEndEvents = allEvents.stream()
                .filter(t -> t.getTrigger() instanceof OnCompetitionEnd)
                .collect(groupingBy((CompetitionEvent t) -> {
                    var competitionEnd = (OnCompetitionEnd) t.getTrigger();
                    var path = competitionEnd.getCompetitionPath();
                    return t.getCompetition().resolveCompetitionPath(path);
                }, toList()));
    }

    private KnightRepository getKnightRepository() {
        return world.getKnightRepository();
    }

    private EloRating getEloRating() {
        return world.getEloRating();
    }

    private NationRating getNationRating() {
        return world.getNationRating();
    }

    private Competition getRootCompetition() {
        return world.getSeasonCompetition();
    }

    private WorldLogger getWorldLogger() {
        return world.getLogger();
    }

    void processOnSeasonStartEvents() {
        onSeasonStartEvents.forEach(this::processEvent);
    }

    void processOnCompetitionEndEvents(Competition competition) {
        var cur = competition;
        while (cur != null) {
            if (!cur.isFinished()) {
                break;
            }
            var events = onCompetitionEndEvents.get(cur);
            if (events != null) {
                events.forEach(this::processEvent);
            }
            cur = cur.getParent();
        }
    }

    private void processEvent(CompetitionEvent event) {
        final var competition = event.getCompetition();
        switch (event.getAction()) {
            case SeedAllFromNationRule rule -> processAllFromNationSeedingRule(competition, rule);
            case SeedRandomPlayers _ -> processRandomSelectionTrigger(competition);
            case SeedGroupStageToPlayOff seedGroupStageToPlayOff ->
                    processGroupStageToPlayOffTrigger(competition, seedGroupStageToPlayOff);
            case SeedPlayOffToPlayOff seedPlayOffToPlayOff ->
                    processPlayOffToPlayOffTrigger(competition, seedPlayOffToPlayOff);
            case SeedBasedOnNationalRankingRule rule -> process(rule, competition);
            case AddAssignedParticipantsFromEloRankings rule -> process(rule, competition);
            case SeedWorldCupRound rule -> process(rule, competition);
            case GiveTrophy action -> processGiveTrophy(competition);
            case SeedTitlePlayOffParticipants seedTitlePlayOffParticipants ->
                    process(competition, seedTitlePlayOffParticipants);
            case AwardTitle awardTitle -> process(competition, awardTitle);
            default -> throw new IllegalArgumentException("Unknown event: " + event.getAction());
        }
    }

    private void processAllFromNationSeedingRule(Competition competition, SeedAllFromNationRule rule) {
        var nationPlayers = getKnightRepository().getAll().stream()
                .filter(p -> p.getNation() == rule.getNation())
                .toList();
        if (!(competition instanceof GroupCompetition group)) {
            var message = String.format("Competition %s is not a group stage competition", competition.getName());
            throw new IllegalArgumentException(message);
        } else {
            group.setActualParticipants(nationPlayers);
        }
    }

    private void processPlayOffToPlayOffTrigger(Competition competition, SeedPlayOffToPlayOff seedPlayOffToPlayOff) {
        var source = competition.resolveCompetitionPath(seedPlayOffToPlayOff.getPlayOffRoundPath());
        if (!(source instanceof PlayOffCompetition sourceStage)) {
            var message = String.format("Path %s is not pointing to a play-off sub stage",
                    seedPlayOffToPlayOff.getPlayOffRoundPath());
            throw new IllegalArgumentException(message);
        } else if (!(competition instanceof PlayOffCompetition targetStage)) {
            var message = String.format("Competition %s is not a play-off stage", competition.getId());
            throw new IllegalArgumentException(message);
        } else {
            targetStage.setActualParticipants(sourceStage.getResults().getWinners());
        }
    }

    private void processGroupStageToPlayOffTrigger(Competition competition, SeedGroupStageToPlayOff seedGroupStageToPlayOff) {
        var source = competition.resolveCompetitionPath(seedGroupStageToPlayOff.getGroupStagePath());
        if (!(source instanceof GroupStage groupStage)) {
            var message = String.format("Path %s is not pointing to a group stage",
                    seedGroupStageToPlayOff.getGroupStagePath());
            throw new IllegalArgumentException(message);
        } else if (!(competition instanceof PlayOffStage playOff)) {
            var message = String.format("Competition %s is not a play-off stage", competition.getId());
            throw new IllegalArgumentException(message);
        } else {
            var pairs = PlayOffUtils.drawPlayersInPairsFromGroupResults(groupStage.getResults());
            playOff.setActualParticipants(pairs);
        }
    }

    private void processRandomSelectionTrigger(Competition competition) {
        var playerPool = new ArrayList<>(getKnightRepository().getAll());
        shuffle(playerPool);
        switch (competition) {
            case GroupStage groupStage -> {
                var selected = playerPool.subList(0, groupStage.getParticipantCount());
                groupStage.setActualParticipants(selected);
            }
            case PlayOffStage playOffStage -> {
                var selected = playerPool.subList(0, playOffStage.getParticipantCount());
                playOffStage.setActualParticipants(selected);
            }
            default -> {
                var message = "Random selection event does not support competition type: " + competition.getClass();
                throw new IllegalArgumentException(message);
            }
        }
    }

    private void process(SeedBasedOnNationalRankingRule rule, Competition competition) {
        var participants = new ArrayList<Knight>();
        for (var nationalRankingParticipant : rule.getSeedNationalRankingParticipants()) {
            participants.add(getPlayerByRanks(
                    nationalRankingParticipant.getNationRank(),
                    nationalRankingParticipant.getPlayerRankInNation())
            );
        }
        for (var previousRound : rule.getPreviousRounds()) {
            var previousCompetition = competition.resolveCompetitionPath(previousRound.getPath());
            if (!(previousCompetition instanceof PlayOffCompetition playOffRound)) {
                throw new IllegalArgumentException("Can't assign winners or losers from competition type: " +
                        previousCompetition.getClass());
            } else {
                var results = playOffRound.getResults();
                var toAdd = previousRound.isWinners() ? results.getWinners() : results.getLosers();
                participants.addAll(toAdd);
            }
        }

        if (competition instanceof PlayOffStage playOffStage) {
            var shuffled = PlayOffUtils.drawPlayersInPairs(participants, true);
            playOffStage.setActualParticipants(shuffled);
        } else if (competition instanceof PlayOffCompetition playOffRound) {
            var shuffled = PlayOffUtils.drawPlayersInPairs(participants, true);
            playOffRound.setActualParticipants(shuffled);
        } else if (competition instanceof GroupStage groupStage) {
            var seeded = makeSeededGroupsTwoByFour(participants, getEloRating());
            groupStage.setActualParticipantsByGroups(seeded);
        } else {
            var message = "Can't assign participants based on rankings for competition type " + competition.getClass();
            throw new IllegalArgumentException(message);
        }
    }

    private Knight getPlayerByRanks(int nationRank, int playerRank) {
        Nation nation = getNationRating().getRankedNation(nationRank - 1);
        var nationCup = (GroupCompetition) getRootCompetition().getChild(nation.name().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Nation cup not found for: " + nation.name()));
        return nationCup.getResults().get(playerRank - 1);
    }

    private void process(AddAssignedParticipantsFromEloRankings rule, Competition competition) {
        var playersByRating = getEloRating().getKnightsByRating();
        competition.assignParticipants(playersByRating.subList(rule.getFromEloRank(), rule.getToEloRank()));
    }

    private void process(SeedWorldCupRound rule, Competition competition) {
        var participants = new ArrayList<>(competition.getAssignedParticipants());
        if (!(competition instanceof GroupStage groupStage)) {
            throw new IllegalArgumentException("Can't seed World Cup round for competition type "
                    + competition.getClass());
        } else {
            rule.getPreviousRoundPath().ifPresent(path -> {
                var previousRound = competition.resolveCompetitionPath(path);
                if (previousRound instanceof PlayOffStage previousPlayOffStage) {
                    participants.addAll(previousPlayOffStage.getLastRoundResults().getWinners());
                } else if (previousRound instanceof GroupStage previousGroupStage) {
                    var winners = previousGroupStage.getResults().getGroupResults().stream()
                            .flatMap(group -> group.stream().limit(2))
                            .toList();
                    participants.addAll(winners);
                } else {
                    throw new IllegalArgumentException("Unknown previous round type: " + previousRound.getClass());
                }
            });

            var seeded = GroupStageUtils.performPotBasedDraw(participants);
            groupStage.setActualParticipantsByGroups(seeded);
        }
    }

    private void processGiveTrophy(Competition competition) {
        competition.getWinner().addTrophy(world.getYear(), competition.getName());
    }

    private void process(Competition competition, SeedTitlePlayOffParticipants rule) {
        if (!(competition instanceof PlayOffCompetition playOffRound)) {
            throw new IllegalArgumentException("Can't seed title play-off for competition type: "
                    + competition.getClass());
        } else {
            var participants = switch (rule.getContendedTitle()) {
                case SIR -> selectTitlePlayOffParticipants(Title.SIR, Title.NONE, World.SIR_COUNT);
                case LORD -> selectTitlePlayOffParticipants(Title.LORD, Title.SIR, World.LORD_COUNT);
                case null, default -> throw new IllegalArgumentException("Can't create title play-off for title: "
                        + rule.getContendedTitle());
            };
            playOffRound.setActualParticipants(participants);
        }
    }

    private List<Knight> selectTitlePlayOffParticipants(Title contestedTitle,
                                                        Title contenderTitle,
                                                        int allowedHolderCount) {
        long holderCount = getKnightRepository().getAll().stream()
                .filter(p -> p.getTitle() == contestedTitle)
                .count();

        var playersByRating = getEloRating().getKnightsByRating();
        if (holderCount >= allowedHolderCount) {
            var holder = playersByRating.reversed().stream()
                    .filter(p -> p.getTitle() == contestedTitle)
                    .findFirst().orElseThrow(RuntimeException::new);

            var contender = playersByRating.stream()
                    .filter(p -> p.getTitle() == contenderTitle)
                    .findFirst().orElseThrow(RuntimeException::new);

            return asList(holder, contender);
        } else {
            return playersByRating.stream()
                    .filter(p -> p.getTitle() == contenderTitle)
                    .limit(2)
                    .collect(toList());
        }
    }

    private void process(Competition competition, AwardTitle action) {
        if (!(competition instanceof PlayOffCompetition playOffRound)) {
            throw new IllegalArgumentException("Can't award title based on competition type: "
                    + competition.getClass());
        } else {
            var winner = playOffRound.getWinner();
            var loser = playOffRound.getLoser();
            var logger = getWorldLogger();
            logger.println();
            if (winner.getTitle() != action.getTitle()) {
                loser.setTitle(winner.getTitle());
                winner.setTitle(action.getTitle());
                logger.println("Knight %s has gained the %s title!", winner.getSimplifiedName(),
                        action.getTitle().getText());
            } else {
                logger.println("Knight %s has defended the %s title!", winner.getSimplifiedName(),
                        action.getTitle().getText());
            }
        }
    }
}

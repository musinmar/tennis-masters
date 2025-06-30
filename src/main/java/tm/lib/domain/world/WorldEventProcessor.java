package tm.lib.domain.world;

import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.definition.actions.Actions;
import tm.lib.domain.competition.definition.actions.Actions.AddAssignedParticipantsFromEloRankings;
import tm.lib.domain.competition.definition.actions.Actions.GiveTrophy;
import tm.lib.domain.competition.definition.actions.Actions.SeedBasedOnNationalRankingRule;
import tm.lib.domain.competition.definition.actions.Actions.SeedWorldCupRound;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.competition.definition.actions.Triggers;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.PlayOffStage;
import tm.lib.domain.competition.impl.PlayOffRound;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static tm.lib.domain.world.GroupStageUtils.makeSeededGroupsTwoByFour;

public class WorldEventProcessor {

    private final int year;
    private final KnightRepository knightRepository;
    private final NationRating nationRating;
    private final EloRating eloRating;
    private final Competition rootCompetition;
    private final Map<Competition, List<CompetitionEvent>> onCompetitionEndEvents;
    private final List<CompetitionEvent> onSeasonStartEvents;

    public WorldEventProcessor(int year,
                               KnightRepository knightRepository,
                               NationRating nationRating,
                               EloRating eloRating,
                               Competition rootCompetition) {
        this.year = year;
        this.knightRepository = knightRepository;
        this.nationRating = nationRating;
        this.eloRating = eloRating;
        this.rootCompetition = rootCompetition;

        var allEvents = rootCompetition.getAllEvents();
        this.onSeasonStartEvents = allEvents.stream()
                .filter(t -> t.getTrigger() instanceof Triggers.OnSeasonStart)
                .toList();
        this.onCompetitionEndEvents = allEvents.stream()
                .filter(t -> t.getTrigger() instanceof Triggers.OnCompetitionEnd)
                .collect(groupingBy((CompetitionEvent t) -> {
                    var competitionEnd = (Triggers.OnCompetitionEnd) t.getTrigger();
                    var path = competitionEnd.getCompetitionPath();
                    return t.getCompetition().resolveCompetitionPath(path);
                }, toList()));
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
        switch (event.getAction()) {
            case Actions.SeedAllFromNationRule rule -> processAllFromNationSeedingRule(event.getCompetition(), rule);
            case Actions.SeedRandomPlayers _ -> processRandomSelectionTrigger(event.getCompetition());
            case Actions.SeedGroupStageToPlayOff seedGroupStageToPlayOff ->
                    processGroupStageToPlayOffTrigger(event.getCompetition(), seedGroupStageToPlayOff);
            case Actions.SeedPlayOffToPlayOff seedPlayOffToPlayOff ->
                    processPlayOffToPlayOffTrigger(event.getCompetition(), seedPlayOffToPlayOff);
            case SeedBasedOnNationalRankingRule rule -> process(rule, event.getCompetition());
            case AddAssignedParticipantsFromEloRankings rule -> process(rule, event.getCompetition());
            case SeedWorldCupRound rule -> process(rule, event.getCompetition());
            case GiveTrophy action -> processGiveTrophy(event.getCompetition());
            default -> throw new IllegalArgumentException("Unknown event: " +
                    event.getAction());
        }
    }

    private void processAllFromNationSeedingRule(Competition competition, Actions.SeedAllFromNationRule rule) {
        var nationPlayers = knightRepository.getAll().stream()
                .filter(p -> p.getNation() == rule.getNation())
                .toList();
        if (!(competition instanceof GroupStage groupStage)) {
            var message = String.format("Competition %s is not a group stage competition", competition.getName());
            throw new IllegalArgumentException(message);
        } else {
            groupStage.setActualParticipants(nationPlayers);
        }
    }

    private void processPlayOffToPlayOffTrigger(Competition competition, Actions.SeedPlayOffToPlayOff seedPlayOffToPlayOff) {
        var source = competition.resolveCompetitionPath(seedPlayOffToPlayOff.getPlayOffRoundPath());
        if (!(source instanceof PlayOffRound sourceStage)) {
            var message = String.format("Path %s is not pointing to a play-off sub stage",
                    seedPlayOffToPlayOff.getPlayOffRoundPath());
            throw new IllegalArgumentException(message);
        } else if (!(competition instanceof PlayOffRound targetStage)) {
            var message = String.format("Competition %s is not a play-off stage", competition.getId());
            throw new IllegalArgumentException(message);
        } else {
            targetStage.setActualParticipants(sourceStage.getResults().getWinners());
        }
    }

    private void processGroupStageToPlayOffTrigger(Competition competition, Actions.SeedGroupStageToPlayOff seedGroupStageToPlayOff) {
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
        var playerPool = new ArrayList<>(this.knightRepository.getAll());
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
            if (!(previousCompetition instanceof PlayOffRound playOffRound)) {
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
        } else if (competition instanceof PlayOffRound playOffRound) {
            var shuffled = PlayOffUtils.drawPlayersInPairs(participants, true);
            playOffRound.setActualParticipants(shuffled);
        } else if (competition instanceof GroupStage groupStage) {
            var seeded = makeSeededGroupsTwoByFour(participants, eloRating);
            groupStage.setActualParticipantsByGroups(seeded);
        } else {
            var message = "Can't assign participants based on rankings for competition type " + competition.getClass();
            throw new IllegalArgumentException(message);
        }
    }

    private Knight getPlayerByRanks(int nationRank, int playerRank) {
        Nation nation = nationRating.getRankedNation(nationRank - 1);
        var nationCup = (GroupStage) rootCompetition.getChild(nation.name().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Nation cup not found for: " + nation.name()));
        return nationCup.getResults().getGroupPosition(0, playerRank - 1);
    }

    private void process(AddAssignedParticipantsFromEloRankings rule, Competition competition) {
        var playersByRating = eloRating.getPersonsByRating();
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
        competition.getWinner().addTrophy(year, competition.getName());
    }
}

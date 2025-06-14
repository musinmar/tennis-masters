package tm.lib.domain.world;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.CompetitionTrigger;
import tm.lib.domain.competition.base.triggers.SeedingRules;
import tm.lib.domain.competition.base.triggers.TriggerTimes;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayOffStage;
import tm.lib.domain.competition.standard.PlayOffSubStage;
import tm.lib.domain.competition.standard.PlayOffUtils;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class WorldTriggerProcessor {

    private final List<Knight> players;
    private final List<CompetitionTrigger> seasonStartTriggers;
    private final Map<Competition, List<CompetitionTrigger>> onCompetitionEndTriggers;

    public WorldTriggerProcessor(List<Knight> players, Competition rootCompetition) {
        this.players = players;
        var allTriggers = rootCompetition.getAllTriggers();
        this.seasonStartTriggers = allTriggers.stream()
                .filter(t -> t.getTrigger().getTriggerTime() instanceof TriggerTimes.SeasonStartTriggerTime)
                .toList();
        this.onCompetitionEndTriggers = allTriggers.stream()
                .filter(t -> t.getTrigger().getTriggerTime() instanceof TriggerTimes.CompetitionEndedTriggerTime)
                .collect(groupingBy((CompetitionTrigger t) -> {
                    var competitionEnd = (TriggerTimes.CompetitionEndedTriggerTime) t.getTrigger().getTriggerTime();
                    var path = competitionEnd.getCompetitionPath();
                    return t.getCompetition().resolveCompetitionPath(path);
                }, toList()));
    }

    private List<Knight> getPlayers() {
        return players;
    }

    void processStartOfSeasonTriggers() {
        seasonStartTriggers.forEach(this::processTrigger);
    }

    void processOnCompetitionEndTriggers(Competition competition) {
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
            case SeedingRules.AllFromNationSeedingRule rule ->
                    processAllFromNationSeedingRule(trigger.getCompetition(), rule);
            case SeedingRules.RandomSelection _ -> processRandomSelectionTrigger(trigger.getCompetition());
            case SeedingRules.GroupStageToPlayOff groupStageToPlayOff ->
                    processGroupStageToPlayOffTrigger(trigger.getCompetition(), groupStageToPlayOff);
            case SeedingRules.PlayOffToPlayOff playOffToPlayOff ->
                    processPlayOffToPlayOffTrigger(trigger.getCompetition(), playOffToPlayOff);
            default -> throw new IllegalArgumentException("Unknown seeding trigger rule: " +
                    trigger.getTrigger().getSeedingRule());
        }
    }

    private void processAllFromNationSeedingRule(Competition competition, SeedingRules.AllFromNationSeedingRule rule) {
        var nationPlayers = getPlayers().stream().filter(p -> p.getNation() == rule.getNation()).toList();
        if (!(competition instanceof GroupStage groupStage)) {
            var message = String.format("Competition %s is not a group stage competition", competition.getName());
            throw new IllegalArgumentException(message);
        } else {
            groupStage.setActualParticipants(nationPlayers);
        }
    }

    private void processPlayOffToPlayOffTrigger(Competition competition, SeedingRules.PlayOffToPlayOff playOffToPlayOff) {
        var source = competition.resolveCompetitionPath(playOffToPlayOff.getPlayOffSubStagePath());
        if (!(source instanceof PlayOffSubStage sourceStage)) {
            var message = String.format("Path %s is not pointing to a play-off sub stage",
                    playOffToPlayOff.getPlayOffSubStagePath());
            throw new IllegalArgumentException(message);
        } else if (!(competition instanceof PlayOffSubStage targetStage)) {
            var message = String.format("Competition %s is not a play-off stage", competition.getId());
            throw new IllegalArgumentException(message);
        } else {
            targetStage.setActualParticipants(sourceStage.getResults().getWinners());
        }
    }

    private void processGroupStageToPlayOffTrigger(Competition competition, SeedingRules.GroupStageToPlayOff groupStageToPlayOff) {
        var source = competition.resolveCompetitionPath(groupStageToPlayOff.getGroupStagePath());
        if (!(source instanceof GroupStage groupStage)) {
            var message = String.format("Path %s is not pointing to a group stage",
                    groupStageToPlayOff.getGroupStagePath());
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
        var playerPool = new ArrayList<>(this.players);
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
                var message = "Random selection trigger does not support competition type: " + competition.getClass();
                throw new IllegalArgumentException(message);
            }
        }
    }
}

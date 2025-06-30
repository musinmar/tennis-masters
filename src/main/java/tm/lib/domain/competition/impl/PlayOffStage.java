package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.definition.actions.Actions;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.competition.definition.actions.Triggers;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayOffStage extends MultiStageCompetition {

    public PlayOffStage() {
    }

    public PlayOffStage(String id, String name, PlayOffStageConfiguration configuration) {
        super(id, name);

        List<Competition> stages = new ArrayList<>();
        int roundPlayerCount = configuration.getPlayerCount();
        for (int i = 0; i < configuration.getRounds(); i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            var stageId = "POS" + (i + 1);
            PlayOffRound stage = new PlayOffRound(stageId, subStageName, roundPlayerCount);
            if (i > 0) {
                var previousStageId = "POS" + i;
                var seedingEvent = new CompetitionEvent(
                        stage,
                        new Triggers.OnCompetitionEnd("../" + previousStageId),
                        new Actions.SeedPlayOffToPlayOff("../" + previousStageId));
                stage.setEvents(List.of(seedingEvent));
            }
            stages.add(stage);
            roundPlayerCount /= 2;
        }
        setStages(stages);
    }

    public int getParticipantCount() {
        return getPlayOffRounds().getFirst().getParticipants().size();
    }

    private String getDefaultRoundName(int playerCount) {
        if (playerCount == 2) {
            return "Final";
        } else if (playerCount == 4) {
            return "Semi-Finals";
        } else if (playerCount == 8) {
            return "Quarter-Finals";
        } else {
            return "Play-off";
        }
    }

    @SuppressWarnings("unchecked")
    public List<PlayOffRound> getPlayOffRounds() {
        return (List<PlayOffRound>) (List<?>) getStages();
    }

    public void setActualParticipants(List<Knight> players) {
        getPlayOffRounds().get(0).setActualParticipants(players);
    }

    public PlayOffRoundResult getLastRoundResults() {
        return getPlayOffRounds().getLast().getResults();
    }
}

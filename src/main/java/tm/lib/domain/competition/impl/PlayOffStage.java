package tm.lib.domain.competition.impl;

import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.definition.actions.Actions;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.competition.definition.actions.Triggers;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

public class PlayOffStage extends MultiStageCompetition {
    private PlayOffStageConfiguration configuration;

    public PlayOffStage(String id, String name, int playerCount) {
        this(id, name, PlayOffStageConfiguration.builder().playerCount(playerCount).build());
    }

    public PlayOffStage(String id, String name, PlayOffStageConfiguration configuration) {
        super(id, name);
        this.configuration = configuration;

        List<Competition> stages = new ArrayList<>();
        int roundPlayerCount = configuration.getPlayerCount();
        for (int i = 0; i < configuration.getRounds(); i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            var stageId = "POS" + (i + 1);
            PlayOffSubStage stage = new PlayOffSubStage(stageId, subStageName, roundPlayerCount);
            if (i > 0) {
                var previousStageId = "POS" + i;
                var seedingEvent = new CompetitionEvent(
                        stage,
                        new Triggers.OnCompetitionEnd("../" + previousStageId),
                        new Actions.SeedPlayOffToPlayOff("../" + previousStageId));
                stage.setEvents(List.of(seedingEvent));
            }
//            stage.registerOnFinishedCallback(this::onPlayoffSubStageFinished);
            stages.add(stage);
            roundPlayerCount /= 2;
        }
        setStages(stages);
    }

    public int getParticipantCount() {
        return configuration.getPlayerCount();
    }

    private String getDefaultRoundName(int playerCount) {
        if (playerCount == 2) {
            return "Финал";
        } else if (playerCount == 4) {
            return "1/2 Финала";
        } else if (playerCount == 8) {
            return "1/4 Финала";
        } else {
            return "Плей-офф";
        }
    }


    @Override
    public void setStartingDate(int date) {
        for (int i = 0; i < getStages().size(); ++i) {
            getStages().get(i).setStartingDate(date + i * 2);
        }
    }

    private void onPlayOffSubStageFinished(Competition competition) {
        PlayOffSubStage finishedStage = (PlayOffSubStage) competition;
        PlayOffSubStage nextStage = null;
        for (int i = 0; i < getStages().size() - 1; ++i) {
            if (getStages().get(i) == finishedStage) {
                nextStage = (PlayOffSubStage) getStages().get(i + 1);
                break;
            }
        }
        if (nextStage != null) {
            nextStage.setActualParticipants(finishedStage.getResults().getWinners());
        }
    }

    @SuppressWarnings("unchecked")
    public List<PlayOffSubStage> getPlayoffSubStages() {
        return (List<PlayOffSubStage>) (List<?>) getStages();
    }

    public void setActualParticipants(List<Knight> players) {
        getPlayoffSubStages().get(0).setActualParticipants(players);
    }

    public PlayOffSubStageResult getLastRoundResults() {
        PlayOffSubStage lastRound = (PlayOffSubStage) getStages().get(getStages().size() - 1);
        return lastRound.getResults();
    }
}

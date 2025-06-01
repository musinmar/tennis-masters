package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.base.triggers.SeedingRules;
import tm.lib.domain.competition.base.triggers.SeedingTrigger;
import tm.lib.domain.competition.base.triggers.TriggerTimes;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayoffStage extends MultiStageCompetition {
    private PlayoffStageConfiguration configuration;

    public PlayoffStage(String id, String name, int playerCount) {
        this(id, name, PlayoffStageConfiguration.builder().playerCount(playerCount).build());
    }

    public PlayoffStage(String id, String name, PlayoffStageConfiguration configuration) {
        super(id, name);
        this.configuration = configuration;

        List<Competition> stages = new ArrayList<>();
        int roundPlayerCount = configuration.getPlayerCount();
        for (int i = 0; i < configuration.getRounds(); i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            var stageId = "POS" + (i + 1);
            PlayoffSubStage stage = new PlayoffSubStage(stageId, subStageName, roundPlayerCount);
            if (i > 0) {
                var previousStageId = "POS" + i;
                var seedingTrigger = new SeedingTrigger(
                        new TriggerTimes.CompetitionEndedTriggerTime("../" + previousStageId),
                        new SeedingRules.PlayOffToPlayOff("../" + previousStageId));
                stage.setSeedingTrigger(Optional.of(seedingTrigger));
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

    private void onPlayoffSubStageFinished(Competition competition) {
        PlayoffSubStage finishedStage = (PlayoffSubStage) competition;
        PlayoffSubStage nextStage = null;
        for (int i = 0; i < getStages().size() - 1; ++i) {
            if (getStages().get(i) == finishedStage) {
                nextStage = (PlayoffSubStage) getStages().get(i + 1);
                break;
            }
        }
        if (nextStage != null) {
            nextStage.setActualParticipants(finishedStage.getResults().getWinners());
        }
    }

    @SuppressWarnings("unchecked")
    public List<PlayoffSubStage> getPlayoffSubStages() {
        return (List<PlayoffSubStage>) (List<?>) getStages();
    }

    public void setActualParticipants(List<Knight> players) {
        getPlayoffSubStages().get(0).setActualParticipants(players);
    }

    public PlayoffSubStageResult getLastRoundResults() {
        PlayoffSubStage lastRound = (PlayoffSubStage) getStages().get(getStages().size() - 1);
        return lastRound.getResults();
    }
}

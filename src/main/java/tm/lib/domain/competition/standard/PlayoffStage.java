package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

public class PlayoffStage extends MultiStageCompetition {

    public PlayoffStage(String id, String name, int playerCount) {
        this(id, name, playerCount, new PlayoffStageConfiguration());
    }

    public PlayoffStage(String id, String name, int playerCount, PlayoffStageConfiguration configuration) {
        super(id, name);

        int rounds = configuration.getRounds();
        if (rounds == 0) {
            if (playerCount == 4) {
                rounds = 2;
            } else if (playerCount == 8) {
                rounds = 3;
            }
        }

        List<Competition> stages = new ArrayList<>();
        int roundPlayerCount = playerCount;
        for (int i = 0; i < rounds; i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            PlayoffSubStage stage = new PlayoffSubStage("POS" + (i + 1), subStageName, roundPlayerCount);
            stage.registerOnFinishedCallback(this::onPlayoffSubStageFinished);
            stages.add(stage);
            roundPlayerCount /= 2;
        }
        initStages(stages);
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

package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.Season;

import java.util.ArrayList;
import java.util.List;

public class PlayoffStage extends MultiStageCompetition {

    public PlayoffStage(Season season, String name, int playerCount) {
        this(season, name, playerCount, new PlayoffStageConfiguration());
    }

    public PlayoffStage(Season season, String name, int playerCount, PlayoffStageConfiguration configuration) {
        super(season, name);

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
            stages.add(new PlayoffSubStage(season, subStageName, roundPlayerCount));
            roundPlayerCount /= 2;
        }
        initStages(stages);
        setParticipants(stages.get(0).getParticipants());
    }

    private String getDefaultRoundName(int playerCount) {
        if (playerCount == 2) {
            return "Финал";
        } else if (playerCount == 4) {
            return "1/2 Финала";
        } else if (playerCount == 8) {
            return "1/4 Финала";
        } else {
            return null;
        }
    }


    @Override
    public void setStartingDate(int date) {
        for (int i = 0; i < getStages().size(); ++i) {
            getStages().get(i).setStartingDate(date + i * 2);
        }
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        PlayoffSubStage nextStage = null;
        for (int i = 0; i < getStages().size() - 1; ++i) {
            if (getStages().get(i) == competition) {
                nextStage = (PlayoffSubStage) getStages().get(i + 1);
                break;
            }
        }
        if (nextStage != null) {
            PlayoffSubStage currentStage = (PlayoffSubStage) competition;
            nextStage.setActualParticipants(currentStage.getResults().getWinners());
        }
        super.onCompetitionEnded(competition);
    }

    @Override
    public void setActualParticipants(List<Knight> players) {
        getStages().get(0).setActualParticipants(players);
    }

    public PlayoffSubStageResult getLastRoundResults() {
        PlayoffSubStage lastRound = (PlayoffSubStage) getStages().get(getStages().size() - 1);
        return lastRound.getResults();
    }
}

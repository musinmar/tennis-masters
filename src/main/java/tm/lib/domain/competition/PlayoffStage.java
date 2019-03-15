package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.ArrayList;
import java.util.List;

public class PlayoffStage extends MultiStageCompetition {
    public PlayoffStage(Competition parent, String name, int playerCount) {
        super(parent, name);

        int roundCount = 0;
        if (playerCount == 4) {
            roundCount = 2;
        } else {
            if (playerCount == 8) {
                roundCount = 3;
            }
        }

        List<Competition> stages = new ArrayList<>();
        int roundPlayerCount = playerCount;
        for (int i = 0; i < roundCount; i++) {
            String subStageName = getDefaultRoundName(roundPlayerCount);
            stages.add(new PlayoffSubStage(this, subStageName, roundPlayerCount));
            roundPlayerCount /= 2;
        }
        setStages(stages);
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
    protected void setParticipants(List<Person> participants) {
        super.setParticipants(participants);
        getStages().get(0).setParticipants(participants);
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
            nextStage.setParticipants(currentStage.getResults().getWinners());
        }
        super.onCompetitionEnded(competition);
    }
}

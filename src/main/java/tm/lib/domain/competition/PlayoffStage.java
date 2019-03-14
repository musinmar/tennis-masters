package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.ArrayList;
import java.util.List;

public class PlayoffStage extends MultiStageCompetition {
    public PlayoffStage(Competition parent, int playerCount) {
        super(parent);

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
            stages.add(new PlayoffSubStage(this, roundPlayerCount));
            roundPlayerCount /= 2;
        }

        stages.get(roundCount - 1).setName("Финал");
        stages.get(roundCount - 2).setName("1/2 Финала");
        if (roundCount - 3 > 0) {
            stages.get(roundCount - 3).setName("1/4 Финала");
        }

        setStages(stages);
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

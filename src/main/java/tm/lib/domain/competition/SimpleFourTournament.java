package tm.lib.domain.competition;

import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.core.Knight;

import java.util.Arrays;
import java.util.List;

public class SimpleFourTournament extends MultiStageCompetition {

    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public SimpleFourTournament(List<Knight> players) {
        super("Тестовый турнир");
        //setParticipants(Participant.createNewList(players));

        groupStage = new GroupStage("Групповой этап", 4);
        groupStage.setActualParticipants(players);
        groupStage.registerOnFinishedCallback(this::onGroupStageFinished);
        playoffStage = new PlayoffStage("Плей-офф", 4);
        initStages(Arrays.asList(groupStage, playoffStage));
    }

    @Override
    public void setStartingDate(int date) {
        groupStage.setStartingDate(date);
        playoffStage.setStartingDate(date + 4);
    }

    private void onGroupStageFinished() {
        playoffStage.setActualParticipants(groupStage.getResults().getGroupResults().get(0));
    }
}

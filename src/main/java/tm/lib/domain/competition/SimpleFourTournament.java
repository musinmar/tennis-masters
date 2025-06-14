package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayOffStage;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleFourTournament extends MultiStageCompetition {

    private final GroupStage groupStage;
    private final PlayOffStage playOffStage;

    public SimpleFourTournament(List<Knight> players) {
        super("TEST", "Тестовый турнир");
        //setParticipants(Participant.createNewList(players));

        groupStage = new GroupStage("GS", "Групповой этап", 1, 4);
        List<List<Knight>> participantsByGroup = new ArrayList<>();
        participantsByGroup.add(players);
        groupStage.setActualParticipantsByGroups(participantsByGroup);
        groupStage.registerOnFinishedCallback(this::onGroupStageFinished);
        playOffStage = new PlayOffStage("PO", "Плей-офф", 4);
        setStages(Arrays.asList(groupStage, playOffStage));
    }

    @Override
    public void setStartingDate(int date) {
        groupStage.setStartingDate(date);
        playOffStage.setStartingDate(date + 4);
    }

    private void onGroupStageFinished(Competition competition) {
        playOffStage.setActualParticipants(groupStage.getResults().getGroupResults().get(0));
    }
}

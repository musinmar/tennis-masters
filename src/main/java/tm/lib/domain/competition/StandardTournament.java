package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupStageResult;
import tm.lib.domain.competition.standard.PlayOffStage;
import tm.lib.domain.core.Knight;

import java.util.Arrays;
import java.util.List;

import static tm.lib.domain.competition.standard.PlayOffUtils.drawPlayersInPairsFromGroupResults;

public class StandardTournament extends MultiStageCompetition {
    private final GroupStage groupStage;
    private final PlayOffStage playOffStage;

    public StandardTournament(String id, List<Knight> players, int index) {
        super(id, "Стандартный турнир " + index);

        groupStage = new GroupStage("GS", "Групповой этап", 2, 4);
        groupStage.setActualParticipants(players);
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
        GroupStageResult groupResults = groupStage.getResults();
        List<Knight> playOffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playOffStage.setActualParticipants(playOffParticipants);
    }
}

package tm.lib.domain.competition;

import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupStageResult;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.core.Knight;

import java.util.Arrays;
import java.util.List;

import static tm.lib.domain.competition.standard.PlayoffUtils.drawPlayersInPairsFromGroupResults;

public class StandardTournament extends MultiStageCompetition {
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public StandardTournament(List<Knight> players, int index) {
        super("Стандартный турнир " + index);

        groupStage = new GroupStage("Групповой этап", 8);
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
        GroupStageResult groupResults = groupStage.getResults();
        List<Knight> playoffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playoffStage.setActualParticipants(playoffParticipants);
    }
}

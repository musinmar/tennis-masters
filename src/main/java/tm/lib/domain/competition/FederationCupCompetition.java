package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupStageResult;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;
import tm.lib.domain.competition.standard.PlayoffSubStage;
import tm.lib.domain.core.Knight;

import java.util.List;

import static java.util.Arrays.asList;
import static tm.lib.domain.competition.standard.PlayoffUtils.drawPlayersInPairsFromGroupResults;

public class FederationCupCompetition extends MultiStageCompetition {

    private final PlayoffSubStage firstQualifyingStage;
    private final PlayoffSubStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public FederationCupCompetition() {
        super("FC", "Кубок Федераций");
        setIsRoot(true);

        firstQualifyingStage = new PlayoffSubStage("FQR", "Первый квалификационный раунд", 4);
        secondQualifyingStage = new PlayoffSubStage("SQR", "Второй квалификационный раунд", 10);

        groupStage = new GroupStage("GS", "Групповой раунд", 8);
        groupStage.registerOnFinishedCallback(this::onGroupStageFinished);

        PlayoffStageConfiguration playoffStageConfiguration = new PlayoffStageConfiguration();
        playoffStageConfiguration.setRounds(2);
        playoffStage = new PlayoffStage("PO", "Плей-офф", 4, playoffStageConfiguration);

        initStages(asList(
                firstQualifyingStage,
                secondQualifyingStage,
                groupStage,
                playoffStage
        ));
    }

    public PlayoffSubStage getFirstQualifyingStage() {
        return firstQualifyingStage;
    }

    public PlayoffSubStage getSecondQualifyingStage() {
        return secondQualifyingStage;
    }

    public GroupStage getGroupStage() {
        return groupStage;
    }

    public PlayoffStage getPlayoffStage() {
        return playoffStage;
    }

    public PlayoffSubStage getPlayoffSemifinals() {
        return (PlayoffSubStage) playoffStage.getStages().get(0);
    }

    public PlayoffSubStage getPlayoffFinal() {
        return (PlayoffSubStage) playoffStage.getStages().get(1);
    }

    private void onGroupStageFinished(Competition competition) {
        GroupStageResult groupResults = groupStage.getResults();
        List<Knight> playoffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playoffStage.setActualParticipants(playoffParticipants);
    }
}

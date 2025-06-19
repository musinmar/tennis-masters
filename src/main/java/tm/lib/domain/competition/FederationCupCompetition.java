package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupStageResult;
import tm.lib.domain.competition.standard.PlayOffStage;
import tm.lib.domain.competition.standard.PlayOffStageConfiguration;
import tm.lib.domain.competition.standard.PlayOffSubStage;
import tm.lib.domain.core.Knight;

import java.util.List;

import static java.util.Arrays.asList;
import static tm.lib.domain.competition.standard.PlayOffUtils.drawPlayersInPairsFromGroupResults;

public class FederationCupCompetition extends MultiStageCompetition {

    private final PlayOffSubStage firstQualifyingStage;
    private final PlayOffSubStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayOffStage playOffStage;

    public FederationCupCompetition() {
        super("FC", "Кубок Федераций");

        firstQualifyingStage = new PlayOffSubStage("FQR", "Первый квалификационный раунд", 4);
        secondQualifyingStage = new PlayOffSubStage("SQR", "Второй квалификационный раунд", 10);

        groupStage = new GroupStage("GS", "Групповой раунд", 2, 4);
        groupStage.registerOnFinishedCallback(this::onGroupStageFinished);

        PlayOffStageConfiguration playoffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        playOffStage = new PlayOffStage("PO", "Плей-офф", playoffStageConfiguration);

        setStages(asList(
                firstQualifyingStage,
                secondQualifyingStage,
                groupStage,
                playOffStage
        ));
    }

    public PlayOffSubStage getFirstQualifyingStage() {
        return firstQualifyingStage;
    }

    public PlayOffSubStage getSecondQualifyingStage() {
        return secondQualifyingStage;
    }

    public GroupStage getGroupStage() {
        return groupStage;
    }

    public PlayOffStage getPlayOffStage() {
        return playOffStage;
    }

    public PlayOffSubStage getPlayoffSemifinals() {
        return (PlayOffSubStage) playOffStage.getStages().get(0);
    }

    public PlayOffSubStage getPlayoffFinal() {
        return (PlayOffSubStage) playOffStage.getStages().get(1);
    }

    private void onGroupStageFinished(Competition competition) {
        GroupStageResult groupResults = groupStage.getResults();
        List<Knight> playoffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playOffStage.setActualParticipants(playoffParticipants);
    }
}

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

public class ChampionsLeagueCompetition extends MultiStageCompetition {

    private final PlayOffSubStage firstQualifyingStage;
    private final PlayOffSubStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayOffStage playOffStage;

    public ChampionsLeagueCompetition() {
        super("CL", "Лига Чемпионов");

        firstQualifyingStage = new PlayOffSubStage("FQR", "Первый квалификационный раунд", 6);
        secondQualifyingStage = new PlayOffSubStage("SQR", "Второй квалификационный раунд", 8);

        groupStage = new GroupStage("GS", "Групповой раунд", 2, 4);
        groupStage.registerOnFinishedCallback(this::onGroupStageFinished);

        PlayOffStageConfiguration playOffStageConfiguration = PlayOffStageConfiguration.builder().playerCount(4).build();
        playOffStage = new PlayOffStage("PO", "Плей-офф", playOffStageConfiguration);

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
        List<Knight> playOffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playOffStage.setActualParticipants(playOffParticipants);
    }
}

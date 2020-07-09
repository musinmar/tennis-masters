package tm.lib.domain.competition;

import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupStageResult;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;
import tm.lib.domain.core.Knight;

import java.util.List;

import static java.util.Arrays.asList;
import static tm.lib.domain.competition.standard.PlayoffUtils.drawPlayersInPairsFromGroupResults;

public class FederationCupCompetition extends MultiStageCompetition {

    private final PlayoffStage firstQualifyingStage;
    private final PlayoffStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public FederationCupCompetition() {
        super("Кубок Федераций");

        PlayoffStageConfiguration firstQualifyingRoundConfiguration = new PlayoffStageConfiguration();
        firstQualifyingRoundConfiguration.setRounds(1);
        firstQualifyingStage = new PlayoffStage("Первый квалификационный раунд", 4, firstQualifyingRoundConfiguration);

        PlayoffStageConfiguration secondQualifyingRoundConfiguration = new PlayoffStageConfiguration();
        secondQualifyingRoundConfiguration.setRounds(1);
        secondQualifyingStage = new PlayoffStage("Второй квалификационный раунд", 10, secondQualifyingRoundConfiguration);

        groupStage = new GroupStage("Групповой раунд", 8);
        groupStage.registerOnFinishedCallback(this::onGroupStageFinished);

        PlayoffStageConfiguration playoffStageConfiguration = new PlayoffStageConfiguration();
        playoffStageConfiguration.setRounds(2);
        playoffStage = new PlayoffStage("Плей-офф", 4, playoffStageConfiguration);

        initStages(asList(
                firstQualifyingStage,
                secondQualifyingStage,
                groupStage,
                playoffStage
        ));
    }

    public PlayoffStage getFirstQualifyingStage() {
        return firstQualifyingStage;
    }

    public PlayoffStage getSecondQualifyingStage() {
        return secondQualifyingStage;
    }

    public GroupStage getGroupStage() {
        return groupStage;
    }

    public PlayoffStage getPlayoffStage() {
        return playoffStage;
    }

    private void onGroupStageFinished() {
        GroupStageResult groupResults = groupStage.getResults();
        List<Knight> playoffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playoffStage.setActualParticipants(playoffParticipants);
    }
}

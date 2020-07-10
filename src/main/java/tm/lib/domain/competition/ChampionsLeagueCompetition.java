package tm.lib.domain.competition;

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

public class ChampionsLeagueCompetition extends MultiStageCompetition {

    private final PlayoffSubStage firstQualifyingStage;
    private final PlayoffSubStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public ChampionsLeagueCompetition() {
        super("Лига Чемпионов");

        firstQualifyingStage = new PlayoffSubStage("Первый квалификационный раунд", 6);
        secondQualifyingStage = new PlayoffSubStage("Второй квалификационный раунд", 8);

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

    private void onGroupStageFinished() {
        GroupStageResult groupResults = groupStage.getResults();
        List<Knight> playoffParticipants = drawPlayersInPairsFromGroupResults(groupResults);
        playoffStage.setActualParticipants(playoffParticipants);
    }
}

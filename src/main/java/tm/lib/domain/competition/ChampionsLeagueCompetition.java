package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;
import tm.lib.domain.core.Knight;

import java.util.List;

import static java.util.Arrays.asList;

public class ChampionsLeagueCompetition extends MultiStageCompetition {

    private final PlayoffStage firstQualifyingStage;
    private final PlayoffStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public ChampionsLeagueCompetition() {
        super("Лига Чемпионов");

        PlayoffStageConfiguration firstQualifyingRoundConfiguration = new PlayoffStageConfiguration();
        firstQualifyingRoundConfiguration.setRounds(1);
        firstQualifyingStage = new PlayoffStage("Первый квалификационный раунд", 6, firstQualifyingRoundConfiguration);

        PlayoffStageConfiguration secondQualifyingRoundConfiguration = new PlayoffStageConfiguration();
        secondQualifyingRoundConfiguration.setRounds(1);
        secondQualifyingStage = new PlayoffStage("Второй квалификационный раунд", 8, secondQualifyingRoundConfiguration);

        groupStage = new GroupStage("Групповой раунд", 8);

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

    @Override
    public void onCompetitionEnded(Competition competition) {
        super.onCompetitionEnded(competition);

        if (competition == groupStage) {
            List<List<Knight>> groupResults = groupStage.getResults().getGroupResults();
            List<Knight> playoffParticipants = asList(
                    groupResults.get(0).get(0),
                    groupResults.get(1).get(1),
                    groupResults.get(1).get(0),
                    groupResults.get(0).get(1)
            );
            playoffStage.setActualParticipants(playoffParticipants);
        }
    }
}

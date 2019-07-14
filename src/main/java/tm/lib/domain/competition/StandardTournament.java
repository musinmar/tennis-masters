package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.Season;

import java.util.Arrays;
import java.util.List;

public class StandardTournament extends MultiStageCompetition {
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public StandardTournament(Season season, List<Knight> players, int index) {
        super(season, "Стандартный турнир " + index);
        //setParticipants(players);

        groupStage = new GroupStage(season, "Групповой этап", 8);
        groupStage.setActualParticipants(players);
        playoffStage = new PlayoffStage(season, "Плей-офф", 4);
        initStages(Arrays.asList(groupStage, playoffStage));
    }

    @Override
    public void setStartingDate(int date) {
        groupStage.setStartingDate(date);
        playoffStage.setStartingDate(date + 4);
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        if (competition == groupStage) {
            List<List<Knight>> groupResults = groupStage.getResults().getGroupResults();
            List<Knight> playoffParticipants = Arrays.asList(
                    groupResults.get(0).get(0),
                    groupResults.get(1).get(1),
                    groupResults.get(1).get(0),
                    groupResults.get(0).get(1)
            );
            playoffStage.setActualParticipants(playoffParticipants);
        }
    }
}

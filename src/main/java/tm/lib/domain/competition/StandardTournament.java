package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.Arrays;
import java.util.List;

public class StandardTournament extends MultiStageCompetition {
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public StandardTournament(Competition parentCompetition, List<Person> players) {
        super(parentCompetition, "Стандартный турнир");
        setParticipants(players);

        groupStage = new GroupStage(this, "Групповой этап", players);
        playoffStage = new PlayoffStage(this, "Плей-офф", 4);
        setStages(Arrays.asList(groupStage, playoffStage));
    }

    @Override
    public void setStartingDate(int date) {
        groupStage.setStartingDate(date);
        playoffStage.setStartingDate(date + 4);
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        if (competition == groupStage) {
            List<List<Person>> groupResults = groupStage.getResults().getGroupResults();
            List<Person> playoffParticipants = Arrays.asList(
                    groupResults.get(0).get(0),
                    groupResults.get(1).get(1),
                    groupResults.get(1).get(0),
                    groupResults.get(0).get(1)
            );
            playoffStage.setParticipants(playoffParticipants);
        }
    }
}

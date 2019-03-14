package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.Arrays;
import java.util.List;

public class StandardTournament extends MultiStageCompetition {
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public StandardTournament(Competition parentCompetition, List<Person> players) {
        super(parentCompetition);
        setName("Стандартный турнир");
        setParticipants(players);

        Competition[] stages = new Competition[2];
        groupStage = new GroupStage(this, players);
        groupStage.setName("Групповой этап");
        stages[0] = groupStage;
        playoffStage = new PlayoffStage(this, 4);
        playoffStage.setName("Плей-офф");
        stages[1] = playoffStage;
        setStages(stages);
    }

    @Override
    public void setStartingDate(int date) {
        getStages()[0].setStartingDate(date);
        getStages()[1].setStartingDate(date + 4);
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
            getStages()[1].setParticipants(playoffParticipants);
        }
    }
}

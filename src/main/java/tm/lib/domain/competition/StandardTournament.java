package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.Arrays;
import java.util.List;

public class StandardTournament extends MultiStageCompetition {
    public StandardTournament(Competition parentCompetition, List<Person> players) {
        super(parentCompetition);
        setName("Стандартный турнир");
        setParticipants(players);

        Competition[] stages = new Competition[2];
        stages[0] = new GroupStage(this, players);
        stages[0].setName("Групповой этап");
        stages[1] = new PlayoffStage(this, 4);
        stages[1].setName("Плей-офф");
        setStages(stages);
    }

    @Override
    public List<Person> getPositions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartingDate(int date) {
        getStages()[0].setStartingDate(date);
        getStages()[1].setStartingDate(date + 4);
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        if (competition == getStages()[0]) {
            List<Person> positions = competition.getPositions();
            List<Person> playoffParticipants = Arrays.asList(
                    positions.get(0),
                    positions.get(5),
                    positions.get(4),
                    positions.get(1)
            );
            getStages()[1].setParticipants(playoffParticipants);
        }
    }
}

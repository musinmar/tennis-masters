package tm.lib.domain.competition;

import tm.lib.domain.core.Person;
import tm.lib.domain.world.Season;

import java.util.List;

public class SimpleFourTournament extends MultiStageCompetition {

    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public SimpleFourTournament(Season season, List<Person> players) {
        super(season);
        setName("Тестовый турнир");
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
            playoffStage.setParticipants(groupStage.getResults().getGroupResults().get(0));
        }
    }
}

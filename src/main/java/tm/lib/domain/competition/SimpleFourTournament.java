package tm.lib.domain.competition;

import tm.lib.domain.core.Person;
import tm.lib.domain.world.Season;

import java.util.Arrays;
import java.util.List;

public class SimpleFourTournament extends MultiStageCompetition {

    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    public SimpleFourTournament(Season season, List<Person> players) {
        super(season);
        setName("Тестовый турнир");
        setParticipants(players);

        groupStage = new GroupStage(this, players);
        groupStage.setName("Групповой этап");
        playoffStage = new PlayoffStage(this, 4);
        playoffStage.setName("Плей-офф");
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
            playoffStage.setParticipants(groupStage.getResults().getGroupResults().get(0));
        }
    }
}

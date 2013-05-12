package tm.lib.base;

public class SimpleFourTournament extends MultiStageCompetition
{
    public SimpleFourTournament(Season season, Person[] players)
    {
        super(season);
        setName("Тестовый турнир");
        setParticipants(players);

        Competition[] stages = new Competition[2];
        stages[0] = new GroupStage(this, players);
        stages[0].setName("Групповой этап");
        stages[1] = new PlayoffStage(this, 4);
        stages[1].setName("Плей-офф");
        setStages(stages);
    }

    @Override
    public Person[] getPositions()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartingDate(int date)
    {
        getStages()[0].setStartingDate(date);
        getStages()[1].setStartingDate(date + 4);
    }
    
    @Override
    public void onCompetitionEnded(Competition competition)
    {
        if (competition == getStages()[0])
        {
            getStages()[1].setParticipants(competition.getPositions());
        }
    }
}

package tm.lib.base;

public class StandardTournament extends MultiStageCompetition
{
    public StandardTournament(Competition parentCompetition, Person[] players)
    {
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
            Person[] positions = competition.getPositions();
            Person[] playoffParticipants = new Person[]
            {
                positions[0], positions[5], positions[4], positions[1]
            };
            getStages()[1].setParticipants(playoffParticipants);
        }
    }
}

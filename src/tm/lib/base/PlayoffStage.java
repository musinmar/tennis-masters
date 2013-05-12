package tm.lib.base;

public class PlayoffStage extends MultiStageCompetition
{
    public PlayoffStage(Competition parent, int playerCount)
    {
        super(parent);

        int roundCount = 0;
        if (playerCount == 4)
        {
            roundCount = 2;
        }
        else if (playerCount == 8)
        {
            roundCount = 3;
        }

        Competition[] stages = new Competition[roundCount];
        int roundPlayerCount = playerCount;
        for (int i = 0; i < roundCount; i++)
        {
            stages[i] = new PlayoffSubStage(this, roundPlayerCount);
            roundPlayerCount /= 2;
        }
        
        stages[roundCount - 1].setName("Финал");
        stages[roundCount - 2].setName("1/2 Финала");
        if (roundCount - 3 > 0)
        {
            stages[roundCount - 3].setName("1/4 Финала");
        }
        
        setStages(stages);
    }

    @Override
    public Person[] getPositions()
    {
        Person[] pos = new Person[getParticipants().length];
        for (int i = 0; i < getStages().length; ++i)
        {
            //Person[] group_pos = sub_stages[i].get_positions();
            //System.arraycopy(group_pos, 0, pos, i * 4, 4);
        }
        return pos;
    }

    @Override
    public void setStartingDate(int date)
    {
        for (int i = 0; i < getStages().length; ++i)
        {
            getStages()[i].setStartingDate(date + i * 2);
        }
    }
    
    @Override
    protected void setParticipants(Person[] participants)
    {
        super.setParticipants(participants);
        getStages()[0].setParticipants(participants);
    }
    
    
    @Override
    public void onCompetitionEnded(Competition competition)
    {
        for (int i = 0; i < getStages().length - 1; ++i)
        {
            if (getStages()[i] == competition)
            {
                int winnerCount = competition.getParticipants().length / 2;
                Person[] nextStageParticipants = new Person[winnerCount];
                System.arraycopy(getStages()[i].getPositions(), 0, nextStageParticipants, 0, winnerCount);
                getStages()[i + 1].setParticipants(nextStageParticipants);                
            }
        }
        super.onCompetitionEnded(competition);
    }
}

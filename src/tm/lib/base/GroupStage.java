package tm.lib.base;

public class GroupStage extends MultiStageCompetition
{
    public GroupStage(Competition parentCompetition, Person[] players)
    {
        super(parentCompetition);
        setName("Групповой этап");
        setParticipants(players);

        int groupCount = players.length / 4;
        Competition[] groups = new Competition[groupCount];
        
        for (int i = 0; i < groupCount; ++i)
        {
            Person[] groupPlayers = new Person[4];
            System.arraycopy(players, i * 4, groupPlayers, 0, 4);
            GroupSubStage group = new GroupSubStage(this, groupPlayers);
            group.setName("Группа " + (i + 1));
            groups[i] = group;
        }
        setStages(groups);
    }

    @Override
    public Person[] getPositions()
    {
        Person[] pos = new Person[getParticipants().length];
        for (int i = 0; i < getStages().length; ++i)
        {
            Person[] groupPositions = getStages()[i].getPositions();
            System.arraycopy(groupPositions, 0, pos, i * 4, 4);
        }
        return pos;
    }

    @Override
    public void setStartingDate(int date)
    {
        for (int i = 0; i < getStages().length; i++)
        {
            getStages()[i].setStartingDate(date);
        }
    }
}

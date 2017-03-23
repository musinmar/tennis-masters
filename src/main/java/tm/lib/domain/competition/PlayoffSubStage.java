package tm.lib.domain.competition;

import tm.lib.domain.core.Person;
import tm.lib.domain.core.SetScore;
import java.util.ArrayList;
import java.util.List;

public class PlayoffSubStage extends SimpleCompetition
{
    public PlayoffSubStage(Competition parent, int playerCount)
    {
        super(parent);

        List<Match> matches = new ArrayList<Match>(playerCount / 2);
        for (int i = 0; i < playerCount / 2; i++)
        {
            Match match = new Match(this, null, null, 4, true);
            matches.add(match);
        }
        setMatches(matches);
    }

    @Override
    public void setStartingDate(int date)
    {
        /*if (matches.size() == 4) {
         matches.get(0).date = date;
         matches.get(1).date = date;
         matches.get(2).date = date + 1;
         matches.get(3).date = date + 1;
         }
         else {*/
        for (Match match : getAllMatches())
        {
            match.setDate(date);
        }
        //}
    }

    @Override
    protected void setParticipants(Person[] participants)
    {
        super.setParticipants(participants);
        List<Match> matches = getAllMatches();
        int playerIndex = 0;
        for (int i = 0; i < matches.size(); ++i)
        {
            matches.get(i).setFirstPlayer(participants[playerIndex++]);
            matches.get(i).setSecondPlayer(participants[playerIndex++]);
        }
    }

    @Override
    public Person[] getPositions()
    {
        Person[] positions = new Person[getParticipants().length];
        int matchCount = getAllMatches().size();
        for (int i = 0; i < getAllMatches().size(); i++)
        {
            Match m = getAllMatches().get(i);
            if (m.getResult() != null)
            {
                SetScore s = m.getResult().getScoreBySets();
                if (s.v1 > s.v2)
                {
                    positions[i] = m.getFirstPlayer();
                    positions[i + matchCount] = m.getSecondPlayer();
                }
                else
                {
                    positions[i] = m.getSecondPlayer();
                    positions[i + matchCount] = m.getFirstPlayer();
                }
            }
            else
            {
                positions[i] = null;
            }
        }

        return positions;
    }
}

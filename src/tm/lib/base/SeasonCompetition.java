package tm.lib.base;

public class SeasonCompetition extends MultiStageCompetition
{
    private static final int TOURNAMENT_COUNT = 8;

    public SeasonCompetition(Season season, Person[] players)
    {
        super(season);
        setParticipants(players);

        Competition[] tournaments = new Competition[TOURNAMENT_COUNT];

        for (int i = 0; i < 8; ++i)
        {
            Competition tournament = new StandardTournament(this, players);
            tournament.setVenue(Stadium.test_stadium());
            tournament.setStartingDate(8 * i);
            tournaments[i] = tournament;
        }

        initParticipants();
        setStages(tournaments);
    }

    @Override
    public Person[] getPositions()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartingDate(int date)
    {
    }

    @Override
    public void onCompetitionEnded(Competition competition)
    {
        super.onCompetitionEnded(competition);
    }

    private void initParticipants()
    {
        //Person[][] tournamentPlayers = new Person[TOURNAMENT_COUNT][8];
        //Person[] players = getParticipants();

        //tournamentPlayers[0][0] = players[0];
        //tournamentPlayers[5][0] = players[1];
        /*boolean done = false;
         while (!done)
         {
         for (int i = 0; i < players.length; ++i)
         {
                

         }
         }*/
    }
}

package tm.lib.base;

public class SeasonCompetition extends MultiStageCompetition
{
    public SeasonCompetition(Season season, Person[] players)
    {
        super(season);
        setName("Сезон");
        setParticipants(players);

        Competition tournament = new StandardTournament(this, players);
        tournament.setVenue(Stadium.test_stadium());
        tournament.setStartingDate(0);
        
        Competition[] tournaments = new Competition[]
        {
            tournament
        };
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
}

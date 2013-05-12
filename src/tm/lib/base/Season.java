package tm.lib.base;

import java.io.PrintStream;
import java.util.List;

public class Season
{
    private static final int SEASON_DAYS = 200;
    private GameWorld world;
    private Competition seasonCompetition;

    public Season(GameWorld world)
    {
        this.world = world;
        
        List<Person> players = world.getPlayers();
        Person[] p = new Person[]
        {
            players.get(1),
            players.get(5),
            players.get(7),
            players.get(12),
            players.get(18),
            players.get(24),
            players.get(28),
            players.get(29)
        };
        
        seasonCompetition = new SeasonCompetition(this, p);
        seasonCompetition.setName("Сезон 1");
    }
    
    public Competition getSeasonCompetition()
    {
        return seasonCompetition;
    }

    public Match getNextMatch()
    {
        return seasonCompetition.getNextMatch();
    }

    public void processMatch(Match m, Score s)
    {
        m.setResult(s);
        print(System.out);
    }
    
    public void print(PrintStream stream)
    {
        seasonCompetition.print(stream);
    }
}

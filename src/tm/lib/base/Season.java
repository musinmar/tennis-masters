package tm.lib.base;

import java.io.PrintStream;
import java.util.List;

public class Season
{
    private static final int SEASON_DAYS = 200;
    private GameWorld world;
    private Competition seasonCompetition;
    private int year;

    public Season(GameWorld world, int year)
    {
        this.world = world;
        this.year = year;
        
        initSeasonCompetition();
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
    
    private void initSeasonCompetition()
    {
        Person[] players = new Person[world.getPlayers().size()];
        this.world.getPlayers().toArray(players);
        seasonCompetition = new SeasonCompetition(this, players);
        seasonCompetition.setName("Сезон " + String.valueOf(year + 1));
    }
}

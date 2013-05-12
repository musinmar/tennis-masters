package tm.lib.base;

import java.io.PrintStream;
import java.util.*;

abstract public class Competition implements IMatchEndListener
{
    private Season season;
    private Competition parentCompetition;
    private String name;
    private Person[] participants;
    private List<ICompetitionEndListener> listeners;

    public void print(PrintStream stream)
    {
        stream.println(getName());
        stream.println();
    }
    
    abstract public Match getNextMatch();
    abstract public List<Match> getAllMatches();
    
    abstract public Person[] getPositions();
    
    abstract public void setStartingDate(int date);
    
    protected void endCompetition()
    {
        for (ICompetitionEndListener listener : listeners)
        {
            listener.onCompetitionEnded(this);
        }
    }
    
    @Override
    public void onMatchEnded(Match match)
    {
        if (getNextMatch() == null)
        {
            endCompetition();
        }
    }
    
    public void setVenue(Stadium venue)
    {
        List<Match> matches = getAllMatches();
        for (Match match : matches)
        {
            match.setVenue(venue);
        }
    }    
    
    Competition(Season season)
    {
        this.name = "Unnamed competition";
        this.season = season;
        parentCompetition = null;
        participants = null;
    }
    
    Competition(Competition parentCompetition)
    {
        this.parentCompetition = parentCompetition;
        this.name = "Unnamed competition";
        this.season = parentCompetition.season;
        participants = null;
        
        listeners = new LinkedList<ICompetitionEndListener>();
        if (parentCompetition instanceof ICompetitionEndListener)
        {
            listeners.add((ICompetitionEndListener)parentCompetition);
        }
    }        

    public Season getSeason()
    {
        return season;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public Competition getParentCompetition()
    {
        return parentCompetition;
    }
    
    public Person[] getParticipants()
    {
        return participants;
    }

    protected void setParticipants(Person[] participants)
    {
        this.participants = participants;
    }
    
    public void addCompetitionEndListener(ICompetitionEndListener listener)
    {
        listeners.add(listener);
    }
}

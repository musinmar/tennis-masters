/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm.lib.engine;

import tm.lib.domain.competition.Match;
import tm.lib.domain.core.Score;
import tm.lib.domain.core.SetScore;

/**
 *
 * @author Corwine
 */
public class MatchSimulator
{
    private final Match match;
    private final MatchEngine matchEngine;
    private final Score currentScore;
    private int matchTime = 0;
    
    private int currentSet = 0;
    private boolean isAdditionalTime = false;
    private Side servingSide = Side.HOME;
    private Side lastGameResult;
    private Side lastSetResult;

    public MatchSimulator(Match match)
    {
        this.match = match;
        matchEngine = new MatchEngine(match);
        currentScore = new Score(match.getSets());
    }
    
    public int getMatchTime()
    {
        return matchTime;
    }
    
    public Score getScore()
    {
        return currentScore;
    }
    
    public Side getLastGameResult()
    {
        assert lastGameResult != null;
        return lastGameResult;
    }

    public Side getLastSetResult()
    {
        assert lastSetResult != null;
        return lastSetResult;
    }
    
    public int getCurrentSet()
    {
        return currentSet;
    }
    
    public Pitch getPitch()
    {
        return matchEngine.getPitch();
    }

    public static enum State {
        PLAYING,
        GAME_ENDED,
        SET_ENDED,
        MATCH_ENDED
    }
    
    public State proceed()
    {
        matchEngine.next();
        matchTime += (int) (MatchEngine.TIME_STEP * 1000);
        if (matchEngine.getWinningSide() == null)   
        {
            return State.PLAYING;
        }
        
        return performEndOfGameActions();
    }

    private State performEndOfGameActions()
    {
        lastGameResult = matchEngine.getWinningSide();
        currentScore.addPoint(matchEngine.getWinningSide().ordinal(), currentSet, isAdditionalTime);        
        matchEngine.performEndOfGameActions();
        if (!checkSetEnd())
        {
            if (isAdditionalTime)
            {
                switchServingPlayer();
            }
            matchEngine.reset(servingSide);
            return State.GAME_ENDED;
        }
        
        return performEndOfSetActions();
    }
    
    private void switchServingPlayer()
    {
        servingSide = servingSide.getOpposite();
    }
    
    private boolean checkSetEnd()
    {
        if (!isAdditionalTime)
        {
            int v1 = currentScore.sets[currentSet].v1;
            int v2 = currentScore.sets[currentSet].v2;
            if (v1 + v2 == Score.BASE_SET_LENGTH)
            {
                return true;
            }
            int rest = Score.BASE_SET_LENGTH - v1 - v2;
            int dif = Math.abs(v1 - v2);
            return dif > rest;
        }
        else
        {
            int v1 = currentScore.additionalTime.v1;
            int v2 = currentScore.additionalTime.v2;
            if (v1 + v2 < Score.ADDITIONAL_SET_LENGTH)
            {
                int rest = Score.ADDITIONAL_SET_LENGTH - v1 - v2;
                int dif = Math.abs(v1 - v2);
                return dif > rest;
            }
            else
            {
                if (v1 + v2 == Score.ADDITIONAL_SET_LENGTH)
                {
                    return v1 != v2;
                }
                else
                {
                    /*if (v1 == Score.MAX_SET_VALUE || v2 == Score.MAX_SET_VALUE) {
                     return true;
                     }
                     else {*/
                    if (Math.abs(v1 - v2) == 2)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                    //}					
                }
            }
        }
    }
    
    private State performEndOfSetActions()
    {
        if (!isAdditionalTime)
        {
            if (currentScore.sets[currentSet].v1 > currentScore.sets[currentSet].v2)
            {
                lastSetResult = Side.HOME;
            }
            else
            {
                lastSetResult = Side.AWAY;
            }
        }
        else
        {
            if (currentScore.additionalTime.v1 > currentScore.additionalTime.v2)
            {
                lastSetResult = Side.HOME;
            }
            else
            {
                lastSetResult = Side.AWAY;
            }
        }
        
        matchEngine.performEndOfSetActions();
        if (checkMatchEnd())
        {
            return State.MATCH_ENDED;
        }
        
        if (currentSet != match.getSets() - 1)
        {
            switchServingPlayer();
            currentSet++;
        }
        else
        {
            isAdditionalTime = true;
            currentScore.additionalTime = new SetScore();
            servingSide = Side.HOME;
        }
        matchEngine.reset(servingSide);
        return State.SET_ENDED;
    }
    
    private boolean checkMatchEnd()
    {
        SetScore s = currentScore.get_set_score();
        if (match.isPlayoff())
        {
            if (s.v1 + s.v2 < match.getSets())
            {
                int dif = Math.abs(s.v1 - s.v2);
                int left = match.getSets() - s.v1 - s.v2;
                return dif > left;
            }
            else
            {
                if (s.v1 + s.v2 == match.getSets())
                {
                    return s.v1 != s.v2;
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            return s.v1 + s.v2 == match.getSets();
        }
    }
}

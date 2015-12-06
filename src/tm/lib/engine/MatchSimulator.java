/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm.lib.engine;

import tm.lib.domain.Match;
import tm.lib.domain.Score;
import tm.lib.domain.SetScore;

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
    private int servingPlayer = 1;
    private int lastGameResult = -1;
    private int lastSetResult = -1;

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
    
    public int getLastGameResult()
    {
        assert lastGameResult != -1;
        return lastGameResult;
    }

    public int getLastSetResult()
    {
        assert lastSetResult != -1;
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
        if (matchEngine.getGameResult() == 0)   
        {
            return State.PLAYING;
        }
        
        return performEndOfGameActions();
    }

    private State performEndOfGameActions()
    {
        lastGameResult = matchEngine.getGameResult();
        currentScore.addPoint(matchEngine.getGameResult(), currentSet, isAdditionalTime);        
        matchEngine.performEndOfGameActions();
        if (!checkSetEnd())
        {
            if (isAdditionalTime)
            {
                switchServingPlayer();
            }
            matchEngine.reset(servingPlayer);
            return State.GAME_ENDED;
        }
        
        return performEndOfSetActions();
    }
    
    private void switchServingPlayer()
    {
        servingPlayer = (servingPlayer == 1) ? 2 : 1;
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
                lastSetResult = 1;
            }
            else
            {
                lastSetResult = 2;
            }
        }
        else
        {
            if (currentScore.additionalTime.v1 > currentScore.additionalTime.v2)
            {
                lastSetResult = 1;
            }
            else
            {
                lastSetResult = 2;
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
            servingPlayer = 1;
        }
        matchEngine.reset(servingPlayer);
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

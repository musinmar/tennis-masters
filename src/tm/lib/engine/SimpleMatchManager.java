package tm.lib.engine;

import tm.lib.base.Match;
import tm.lib.base.Score;
import tm.lib.base.SetScore;

public class SimpleMatchManager
{
    private Match match;
    private MatchEngine match_engine;
    private Score active_score;
    private int set;
    private boolean additional;
    int active_player;
    int match_time;

    public SimpleMatchManager(Match match)
    {
        this.match = match;
        match_engine = new MatchEngine(match);
        active_score = new Score(match.getSets());
        set = 0;
        additional = false;
        active_player = 1;
        match_time = 0;
    }

    public void start()
    {
        match_engine.reset(active_player);

        boolean matchEnded = false;
        while (!matchEnded)
        {
            match_engine.next();
            match_time += (int) (MatchEngine.TIME_STEP * 1000);
            if (match_engine.getGameResult() != 0)
            {
                game_has_ended();
                if (check_set_end())
                {
                    match_engine.performEndOfSetActions();
                    if (!check_match_end())
                    {
                        if (set != match.getSets() - 1)
                        {
                            switch_active_player();
                            set++;
                        }
                        else
                        {
                            additional = true;
                            active_score.additionalTime = new SetScore();
                            active_player = 1;
                        }
                        match_engine.reset(active_player);
                    }
                    else
                    {
                        matchEnded = true;
                    }
                }
                else
                {
                    if (additional)
                    {
                        switch_active_player();
                    }
                    match_engine.reset(active_player);
                }
            }
        }
    }

    public Score getScore()
    {
        return active_score;
    }

    private void game_has_ended()
    {
        if (!additional)
        {
            if (match_engine.getGameResult() == 1)
            {
                active_score.sets[set].v1 += 1;
            }
            else
            {
                active_score.sets[set].v2 += 1;
            }
        }
        else
        {
            if (match_engine.getGameResult() == 1)
            {
                active_score.additionalTime.v1 += 1;
            }
            else
            {
                active_score.additionalTime.v2 += 1;
            }
        }

        match_engine.performEndOfGameActions();
    }

    private boolean check_set_end()
    {
        if (!additional)
        {
            int v1 = active_score.sets[set].v1;
            int v2 = active_score.sets[set].v2;
            if (v1 + v2 == Score.BASE_SET_LENGTH)
            {
                return true;
            }
            int rest = Score.BASE_SET_LENGTH - v1 - v2;
            int dif = Math.abs(v1 - v2);
            if (dif > rest)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            int v1 = active_score.additionalTime.v1;
            int v2 = active_score.additionalTime.v2;
            if (v1 + v2 < Score.ADDITIONAL_SET_LENGTH)
            {
                int rest = Score.ADDITIONAL_SET_LENGTH - v1 - v2;
                int dif = Math.abs(v1 - v2);
                if (dif > rest)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (v1 + v2 == Score.ADDITIONAL_SET_LENGTH)
                {
                    if (v1 == v2)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
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

    private void switch_active_player()
    {
        if (active_player == 1)
        {
            active_player = 2;
        }
        else
        {
            active_player = 1;
        }
    }

    private boolean check_match_end()
    {
        SetScore s = active_score.get_set_score();
        if (match.isPlayoff())
        {
            if (s.v1 + s.v2 < match.getSets())
            {
                int dif = Math.abs(s.v1 - s.v2);
                int left = match.getSets() - s.v1 - s.v2;
                if (dif > left)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (s.v1 + s.v2 == match.getSets())
                {
                    if (s.v1 == s.v2)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            if (s.v1 + s.v2 == match.getSets())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}

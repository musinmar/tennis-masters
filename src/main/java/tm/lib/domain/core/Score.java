package tm.lib.domain.core;

public class Score
{
    public static final int BASE_SET_LENGTH = 9;
    public static final int ADDITIONAL_SET_LENGTH = 6;
    public static final int MAX_SET_VALUE = 20;

    public SetScore[] sets;
    public SetScore additionalTime;

    public Score(int sets_count)
    {
        sets = new SetScore[sets_count];
        for (int i = 0; i < sets_count; i++)
        {
            sets[i] = new SetScore();
        }
        additionalTime = null;
    }

    public Score(Score other)
    {
        sets = new SetScore[other.sets_count()];
        for (int i = 0; i < other.sets.length; i++)
        {
            sets[i] = new SetScore(other.sets[i]);
        }
        if (other.additionalTime != null)
        {
            additionalTime = new SetScore(other.additionalTime);
        }
        else
        {
            additionalTime = null;
        }
    }

    public int sets_count()
    {
        return sets.length;
    }

    public boolean isFirstPlayerWinner()
    {
        SetScore scoreBySets = get_set_score();
        return scoreBySets.v1 > scoreBySets.v2;
    }

    public boolean isDraw()
    {
        SetScore scoreBySets = get_set_score();
        return scoreBySets.v1 == scoreBySets.v2;
    }

    public Score reverse()
    {
        Score reversedScore = new Score(this);
        for (int i = 0; i < reversedScore.sets.length; ++i)
        {
            int temp = reversedScore.sets[i].v1;
            reversedScore.sets[i].v1 = reversedScore.sets[i].v2;
            reversedScore.sets[i].v2 = temp;
        }
        if (reversedScore.additionalTime != null)
        {
            int temp = additionalTime.v1;
            additionalTime.v1 = additionalTime.v2;
            additionalTime.v2 = temp;
        }
        return reversedScore;
    }

    @Override
    public String toString()
    {
        String buf = "";
        buf += sets[0];
        for (int i = 1; i < sets.length; i++)
        {
            buf += " / " + sets[i];
        }
        if (additionalTime != null)
        {
            buf += " / д.в. " + additionalTime;
        }
        return buf;
    }

    public String get_short_score(int set, boolean additional)
    {
        String buf = "";
        buf += sets[0];
        for (int i = 1; i <= set; i++)
        {
            buf += " / " + sets[i];
        }
        if (additional && additionalTime != null)
        {
            buf += " / a. " + additionalTime;
        }
        return buf;
    }

    public SetScore get_set_score()
    {
        SetScore ret = new SetScore();
        for (int i = 0; i < sets.length; i++)
        {
            if (sets[i].v1 > sets[i].v2)
            {
                ret.v1++;
            }
            else
            {
                if (sets[i].v1 < sets[i].v2)
                {
                    ret.v2++;
                }
            }
        }
        if (additionalTime != null)
        {
            if (additionalTime.v1 > additionalTime.v2)
            {
                ret.v1++;
            }
            else
            {
                if (additionalTime.v1 < additionalTime.v2)
                {
                    ret.v2++;
                }
            }
        }
        return ret;
    }

    public SetScore getGameScore()
    {
        SetScore ret = new SetScore();
        for (int i = 0; i < sets.length; i++)
        {
            ret.v1 += sets[i].v1;
            ret.v2 += sets[i].v2;
        }
        if (additionalTime != null)
        {
            ret.v1 += additionalTime.v1;
            ret.v2 += additionalTime.v2;
        }
        return ret;
    }
    
    public void addPoint(int player, int set, boolean isAdditionalTime) {
        assert player == 0 || player == 1;
        if (!isAdditionalTime)
        {
            if (player == 0)
            {
                sets[set].v1 += 1;
            }
            else
            {
                sets[set].v2 += 1;
            }
        }
        else
        {
            if (player == 0)
            {
                additionalTime.v1 += 1;
            }
            else
            {
                additionalTime.v2 += 1;
            }
        }
    }
}

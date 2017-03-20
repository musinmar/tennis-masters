package tm.lib.domain.core;

public class SetScore
{
    public int v1;
    public int v2;

    public SetScore()
    {
        this(0, 0);
    }

    public SetScore(int value1, int value2)
    {
        v1 = value1;
        v2 = value2;
    }

    public SetScore(SetScore other)
    {
        this(other.v1, other.v2);
    }

    @Override
    public String toString()
    {
        return v1 + ":" + v2;
    }
}

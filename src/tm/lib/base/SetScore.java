package tm.lib.base;

public class SetScore
{
    public int v1;
    public int v2;

    public SetScore()
    {
        v1 = 0;
        v2 = 0;
    }

    public SetScore(int value1, int value2)
    {
        v1 = value1;
        v2 = value2;
    }

    public SetScore(SetScore other)
    {
        v1 = other.v1;
        v2 = other.v2;
    }

    public String toString()
    {
        return v1 + ":" + v2;
    }
}

package tm.lib.domain;

public enum Nation
{
    ALMAGEST("Альмагест"),
    BELEROFON("Белерофон"),
    GALILEO("Галилео"),
    KAMELEOPARD("Камелеопард"),
    OBERON22("Оберон-22");
    private final String text;

    Nation(String name)
    {
        text = name;
    }

    @Override
    public String toString()
    {
        return text;
    }

    static Nation fromString(String text)
    {
        for (Nation nation : Nation.values())
        {
            if (nation.text.equals(text))
            {
                return nation;
            }
        }
        return Nation.ALMAGEST;
    }
}

package tm.lib.base;

public enum Country
{
    ALDORUM("Альдорум"),
    CONJUCTION("Коньюкция"),
    TWOCITIES("Двугородье"),
    NORTH_ALLIANCE("Северный Альянс");
    private final String text;

    Country(String name)
    {
        text = name;
    }

    @Override
    public String toString()
    {
        return text;
    }

    public static Country fromString(String text)
    {
        for (Country country : Country.values())
        {
            if (country.text.equals(text))
            {
                return country;
            }
        }
        return ALDORUM;
    }
}

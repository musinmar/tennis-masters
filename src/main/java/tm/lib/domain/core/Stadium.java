package tm.lib.domain.core;

import static tm.lib.domain.core.Country.ALDORUM;

public class Stadium {
    private final String name;
    private final Country country;

    private final double netHeight;
    private final double roughness;
    private final double slippery;

    public Stadium(String name, Country country) {
        this.name = name;
        this.country = country;
        netHeight = 50;
        roughness = 50;
        slippery = 50;
    }

    public static Stadium standard() {
        return new Stadium("Ириф", ALDORUM);
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public double getNetHeight() {
        return netHeight;
    }

    public double getRoughness() {
        return roughness;
    }

    public double getSlippery() {
        return slippery;
    }
}

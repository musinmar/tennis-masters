package tm.lib.domain.core;

public class Stadium {
    private String name;
    private Country country;

    private double netHeight;
    private double roughness;
    private double slippery;

    public Stadium() {
        name = "Nil_stadium";
        country = Country.ALDORUM;

        netHeight = 50;
        roughness = 50;
        slippery = 50;
    }

    public static Stadium test_stadium() {
        Stadium s = new Stadium();
        s.name = "Ириф";
        s.country = Country.ALDORUM;

        //s.net_height = 100;
        //s.roughness = 0;
        //s.slippery = 100;	
        return s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public double getNetHeight() {
        return netHeight;
    }

    public void setNetHeight(double netHeight) {
        this.netHeight = netHeight;
    }

    public double getRoughness() {
        return roughness;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }

    public double getSlippery() {
        return slippery;
    }

    public void setSlippery(double slippery) {
        this.slippery = slippery;
    }
}

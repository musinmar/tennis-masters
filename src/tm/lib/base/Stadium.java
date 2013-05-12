package tm.lib.base;

public class Stadium {
	public String name;
	public Country country;
	
	public double net_height;
	public double roughness;
	public double slippery;
	
	public Stadium() {
		name = "Nil_stadium";
		country = Country.ALDORUM;
		
		net_height = 50;
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
}

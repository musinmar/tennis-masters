package tm.lib.engine;

import tm.lib.base.*;

public class Player {
	public Person person;	
	public DPoint position;
	public DPoint direction;
	public double speed;
	public int id;
	public boolean lying;
	public double lying_time;
	public double energy;
	
	public Player(Person person, int index) {
		this.person = person;
		id = index;
		position = new DPoint();
		direction = new DPoint();
		speed = 0;
		lying = false;
		lying_time = 0;
		energy = 100;
	}
	
	public void loose_energy(double value) {
		energy -= value;
		if (energy < 0)
			energy = 0;
	}
	
	public void regain_energy(double value) {
		energy += value;
		if (energy > 100)
			energy = 100;
	}
}

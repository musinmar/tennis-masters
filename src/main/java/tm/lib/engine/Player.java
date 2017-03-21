package tm.lib.engine;

import tm.lib.domain.core.Person;

public class Player {

    private final Person person;
    private final Side side;

    public Point2d position;
    public Point2d direction;
    public double speed;
    public boolean lying;
    public double lying_time;
    private double energy;

    public Player(Person person, Side side) {
        this.person = person;
        this.side = side;
        
        position = new Point2d();
        direction = new Point2d();
        speed = 0;
        lying = false;
        lying_time = 0;
        energy = 100;
    }

    public Person getPerson() {
        return person;
    }

    public Side getSide() {
        return side;
    }

    public double getEnergy() {
        return energy;
    }
    
    public void changeEnergy(double dif) {
        double newEnergy = energy + dif;
        energy = Math.max(Math.min(newEnergy, 100), 0);
    }
}

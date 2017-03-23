package tm.lib.engine;

import tm.lib.domain.core.Person;

public class Player {

    private final Person person;
    private final Side side;

    private Point2d position;
    private Point2d direction;
    private double speed;
    private boolean lying;
    private double lyingTime;
    private double energy;

    public Player(Person person, Side side) {
        this.person = person;
        this.side = side;

        position = new Point2d();
        direction = new Point2d();
        speed = 0;
        lying = false;
        lyingTime = 0;
        energy = 100;
    }

    public Person getPerson() {
        return person;
    }

    public Side getSide() {
        return side;
    }

    public Point2d getPosition() {
        return position;
    }

    public void setPosition(Point2d position) {
        this.position = position;
    }

    public Point2d getDirection() {
        return direction;
    }

    public void setDirection(Point2d direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isLying() {
        return lying;
    }

    public void setLying(boolean lying) {
        this.lying = lying;
    }

    public double getLyingTime() {
        return lyingTime;
    }

    private void setLyingTime(double lyingTime) {
        this.lyingTime = lyingTime;
    }
    
    public void addLyingTime(double time) {
        this.lyingTime += time;
    }

    public double getEnergy() {
        return energy;
    }

    public void changeEnergy(double dif) {
        double newEnergy = energy + dif;
        energy = Math.max(Math.min(newEnergy, 100), 0);
    }
    
    public void lieDown() {
        setLying(true);
        setLyingTime(0);
    }

    public void resetState(Point2d position, Point2d direction) {
        setPosition(position);
        setDirection(direction);
        setSpeed(0);
        setLying(false);
        setLyingTime(0);
    }
}

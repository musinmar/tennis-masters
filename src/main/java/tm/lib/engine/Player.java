package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.domain.core.Knight;

public class Player {

    private final Knight knight;
    private final Side side;

    private Vector2D position = Vector2D.ZERO;
    private Vector2D direction = Vector2D.ZERO;
    private double speed = 0;
    private boolean lying = false;
    private double lyingTime = 0;
    private double energy = 100;

    public Player(Knight knight, Side side) {
        this.knight = knight;
        this.side = side;
    }

    public Knight getKnight() {
        return knight;
    }

    public Side getSide() {
        return side;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public void setDirection(Vector2D direction) {
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
        setSpeed(0);
    }

    public void resetState(Vector2D position, Vector2D direction) {
        setPosition(position);
        setDirection(direction);
        setSpeed(0);
        setLying(false);
        setLyingTime(0);
    }
}

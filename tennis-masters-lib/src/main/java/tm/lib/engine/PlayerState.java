package tm.lib.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import static tm.lib.engine.VectorUtils.mirror;

@Getter
@Setter
public class PlayerState {
    private Vector2D position;
    private Vector2D direction;
    private double speed;
    private boolean lying;
    private double lyingTime;
    private double energy;

    public PlayerState() {
        reset(Vector2D.ZERO, Vector2D.ZERO);
        energy = 100.0;
    }

    public void reset(Vector2D position, Vector2D direction) {
        setPosition(position);
        setDirection(direction);
        setSpeed(0);
        setLying(false);
        setLyingTime(0);
    }

    public void lieDown() {
        setLying(true);
        setLyingTime(0);
        setSpeed(0);
    }

    public void addLyingTime(double time) {
        this.lyingTime = this.lyingTime + time;
    }

    public void changeEnergy(double dif) {
        double newEnergy = energy + dif;
        energy = Math.clamp(newEnergy, 0, 100);
    }

    public PlayerState createMirrored() {
        PlayerState mirroredState = new PlayerState();
        mirroredState.setPosition(mirror(position));
        mirroredState.setDirection(mirror(direction));
        mirroredState.setSpeed(speed);
        mirroredState.setLying(lying);
        mirroredState.setLyingTime(lyingTime);
        mirroredState.setEnergy(energy);
        return mirroredState;
    }

    public PlayerState copy() {
        PlayerState copy = new PlayerState();
        copy.setPosition(position);
        copy.setDirection(direction);
        copy.setSpeed(speed);
        copy.setLying(lying);
        copy.setLyingTime(lyingTime);
        copy.setEnergy(energy);
        return copy;
    }
}
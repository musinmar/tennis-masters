package tm.lib.engine;

public interface Strategy {
    Decision makeDecision(Pitch pitch, double timeStep);
}

package tm.lib.engine;

public enum Side {
    HOME,
    AWAY;
    
    public Side getOpposite() {
        return this == HOME ? AWAY : HOME;
    }
    
    public double getModifier() {
        return this == HOME ? 1 : -1;
    }
}

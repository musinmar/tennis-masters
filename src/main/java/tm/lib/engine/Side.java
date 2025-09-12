package tm.lib.engine;

import org.apache.commons.lang3.StringUtils;

public enum Side {
    HOME,
    AWAY;

    public Side getOpposite() {
        return this == HOME ? AWAY : HOME;
    }

    public double getModifier() {
        return this == HOME ? 1 : -1;
    }

    public String getDisplayName() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}

package tm.lib.domain.core;

public enum Title {
    NONE(""),
    SIR("Sir"),
    LORD("Lord");

    private final String text;

    Title(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
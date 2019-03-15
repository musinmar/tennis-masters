package tm.lib.domain.core;

import java.util.Arrays;

public enum Nation {
    ALMAGEST("Альмагест"),
    BELLEROFON("Беллерофон"),
    GALILEO("Галилео"),
    KAMELEOPARD("Камелеопард"),
    OBERON_22("Оберон-22");

    public static final int COUNT = 5;

    private final String name;

    Nation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Nation fromName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.getName().equals(name))
                .findAny().orElseThrow(IllegalArgumentException::new);
    }
}

package tm.lib.domain.core;

import java.util.Arrays;

public enum Nation {
    ALMAGEST("Альмагест", "Альмагеста"),
    BELLEROFON("Беллерофон", "Беллерофона"),
    GALILEO("Галилео", "Галилео"),
    KAMELEOPARD("Камелеопард", "Камелеопарда"),
    OBERON_22("Оберон-22", "Оберона-22");

    public static final int COUNT = 5;

    private final String name;
    private final String nameGenitive;

    Nation(String name, String nameGenitive) {
        this.name = name;
        this.nameGenitive = nameGenitive;
    }

    public String getName() {
        return name;
    }

    public String getNameGenitive() {
        return nameGenitive;
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

package tm.lib.domain.core;

import java.util.Arrays;

public enum Nation {
    ALMAGEST("Almagest", "Almagest's"),
    BELLEROPHON("Bellerophon", "Bellerophon's"),
    GALILEO("Galileo", "Galileo's"),
    KAMELEOPARD("Kamelopard", "Kamelopard's"),
    OBERON_22("Oberon-22", "Oberon-22's");

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

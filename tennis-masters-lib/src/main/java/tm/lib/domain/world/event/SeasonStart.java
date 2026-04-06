package tm.lib.domain.world.event;

public class SeasonStart implements WorldEvent {

    public static final SeasonStart INSTANCE = new SeasonStart();

    private SeasonStart() {
    }
}

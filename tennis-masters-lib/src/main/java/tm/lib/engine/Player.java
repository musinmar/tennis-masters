package tm.lib.engine;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Knight;

@Getter
@Setter
public class Player {

    private final Knight knight;
    private final Side side;
    private final PlayerState state;

    public Player(Knight knight, Side side) {
        this(knight, side, new PlayerState());
    }

    private Player(Knight knight, Side side, PlayerState state) {
        this.knight = knight;
        this.side = side;
        this.state = state;
    }

    public Player createMirrored() {
        var mirroredState = state.createMirrored();
        return new Player(knight, side.getOpposite(), mirroredState);
    }
}

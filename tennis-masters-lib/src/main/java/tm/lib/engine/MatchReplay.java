package tm.lib.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MatchReplay {
    private final Knight homePlayer;
    private final Knight awayPlayer;
    private final List<GameState> states = new ArrayList<>();
}

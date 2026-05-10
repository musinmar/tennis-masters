package tm.lib.domain.competition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tm.lib.domain.core.MatchScore;
import tm.lib.engine.MatchReplay;

@Getter
@RequiredArgsConstructor
public class MatchResult {
    private final MatchScore score;
    private final MatchReplay replay;

    @Override
    public String toString() {
        return score.toString();
    }
}
package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class SeedWorldCupRound implements Action {
    private Optional<String> previousRoundPath;
    private String targetPath;
}
package tm.lib.domain.competition.definition.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OnAllOfCompetitionsEnd implements Trigger {
    private List<String> competitionPaths;
}
package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tm.lib.domain.core.Title;

@Getter
@AllArgsConstructor
public class SeedTitlePlayOffParticipants implements Action {
    private Title contendedTitle;
}
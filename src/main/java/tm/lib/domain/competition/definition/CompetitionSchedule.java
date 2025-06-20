package tm.lib.domain.competition.definition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionSchedule {
    public static CompetitionSchedule EVERY_YEAR = new CompetitionSchedule();

    private int periodInYears = 1;
    private int startingFromYear = 1;
}

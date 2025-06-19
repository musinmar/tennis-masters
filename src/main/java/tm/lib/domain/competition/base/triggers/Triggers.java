package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Triggers {

    @AllArgsConstructor
    public static class OnSeasonStart implements Trigger {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OnCompetitionEnd implements Trigger {
        private String competitionPath;
    }
}

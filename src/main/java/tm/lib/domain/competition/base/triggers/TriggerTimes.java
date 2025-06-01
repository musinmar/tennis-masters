package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class TriggerTimes {

    @AllArgsConstructor
    public static class SeasonStartTriggerTime implements TriggerTime {
        @Override
        public String getId() {
            return "SEASON_START";
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CompetitionEndedTriggerTime implements TriggerTime {
        private String competitionPath;
        @Override
        public String getId() {
            return "COMPETITION_ENDED";
        }
    }
}

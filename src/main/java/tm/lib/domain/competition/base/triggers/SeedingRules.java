package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Nation;

public class SeedingRules {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RandomSelection implements SeedingRule {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GroupStageToPlayOff implements SeedingRule {
        private String groupStagePath;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlayOffToPlayOff implements SeedingRule {
        private String playOffSubStagePath;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class AllFromNationSeedingRule implements SeedingRule {
        private Nation nation;
    }

}

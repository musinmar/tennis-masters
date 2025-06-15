package tm.lib.domain.competition.base.triggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tm.lib.domain.core.Nation;

import java.util.ArrayList;
import java.util.List;

public class SeedingRules {

    @Getter
    @AllArgsConstructor
    public static class RandomSelection implements SeedingRule {
    }

    @Getter
    @AllArgsConstructor
    public static class GroupStageToPlayOff implements SeedingRule {
        private String groupStagePath;
    }

    @Getter
    @AllArgsConstructor
    public static class PlayOffToPlayOff implements SeedingRule {
        private String playOffSubStagePath;
    }

    @Getter
    @AllArgsConstructor
    public static class AllFromNationSeedingRule implements SeedingRule {
        private Nation nation;
    }

    @Getter
    @AllArgsConstructor
    public static class BasedOnNationalRankingSeedingRule implements SeedingRule {
        @Getter
        @AllArgsConstructor
        public static class PreviousRound {
            private String path;
            private boolean winners;
        }

        @Getter
        @AllArgsConstructor
        public static class NationalRankingParticipant {
            private int nationRank;
            private int playerRankInNation;
        }

        public static List<NationalRankingParticipant> buildParticipants(Integer... ranks) {
            var result = new ArrayList<NationalRankingParticipant>();
            for (int i = 0; i < ranks.length; i += 2) {
                result.add(new NationalRankingParticipant(ranks[i], ranks[i + 1]));
            }
            return result;
        }

        private List<PreviousRound> previousRounds;
        private List<NationalRankingParticipant> nationalRankingParticipants;
    }

}

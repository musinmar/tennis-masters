package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tm.lib.domain.core.Nation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Actions {

    @Getter
    @AllArgsConstructor
    public static class SeedRandomPlayers implements Action {
    }

    @Getter
    @AllArgsConstructor
    public static class SeedGroupStageToPlayOff implements Action {
        private String groupStagePath;
    }

    @Getter
    @AllArgsConstructor
    public static class SeedPlayOffToPlayOff implements Action {
        private String playOffSubStagePath;
    }

    @Getter
    @AllArgsConstructor
    public static class SeedAllFromNationRule implements Action {
        private Nation nation;
    }

    @Getter
    @AllArgsConstructor
    public static class SeedBasedOnNationalRankingRule implements Action {
        @Getter
        @AllArgsConstructor
        public static class PreviousRound {
            private String path;
            private boolean winners;
        }

        @Getter
        @AllArgsConstructor
        public static class SeedNationalRankingParticipant {
            private int nationRank;
            private int playerRankInNation;
        }

        public static List<SeedNationalRankingParticipant> buildParticipants(Integer... ranks) {
            var result = new ArrayList<SeedNationalRankingParticipant>();
            for (int i = 0; i < ranks.length; i += 2) {
                result.add(new SeedNationalRankingParticipant(ranks[i], ranks[i + 1]));
            }
            return result;
        }

        private List<PreviousRound> previousRounds;
        private List<SeedNationalRankingParticipant> seedNationalRankingParticipants;
    }

    @Getter
    @AllArgsConstructor
    public static class AddAssignedParticipantsFromEloRankings implements Action {
        private int fromEloRank;
        private int toEloRank;
    }

    @Getter
    @AllArgsConstructor
    public static class SeedWorldCupRound implements Action {
        private Optional<String> previousRoundPath;
    }

    public static class GiveTrophy implements Action {
    }

}

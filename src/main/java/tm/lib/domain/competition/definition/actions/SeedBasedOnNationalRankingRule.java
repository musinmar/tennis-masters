package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class SeedBasedOnNationalRankingRule implements Action {
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
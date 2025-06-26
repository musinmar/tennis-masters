package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeedBasedOnNationalRankingRuleDto extends ActionDto {
    private List<PreviousRoundDto> previousRounds;
    private List<SeedNationalRankingParticipantDto> seedNationalRankingParticipants;

    @Getter
    @Setter
    public static class PreviousRoundDto {
        private String path;
        private boolean winners;
    }

    @Getter
    @Setter
    public static class SeedNationalRankingParticipantDto {
        private int nationRank;
        private int playerRankInNation;
    }
}
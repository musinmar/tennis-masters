package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeedBasedOnNationalRankingRuleDto extends ActionDto {
    public static final String TYPE = "seedBasedOnNationalRankingRule";
    
    private List<PreviousRoundDto> previousRounds;
    private List<SeedNationalRankingParticipantDto> seedNationalRankingParticipants;
    private String targetPath;

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
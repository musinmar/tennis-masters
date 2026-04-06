package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAssignedParticipantsFromEloRankingsDto extends ActionDto {
    public static final String TYPE = "addAssignedParticipantsFromEloRankings";
    
    private int fromEloRank;
    private int toEloRank;
    private String targetPath;
}
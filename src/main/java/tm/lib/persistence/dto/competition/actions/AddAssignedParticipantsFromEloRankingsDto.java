package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAssignedParticipantsFromEloRankingsDto extends ActionDto {
    private int fromEloRank;
    private int toEloRank;
}
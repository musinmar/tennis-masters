package tm.lib.domain.competition.definition.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddAssignedParticipantsFromEloRankings implements Action {
    private int fromEloRank;
    private int toEloRank;
    private String targetPath;
}
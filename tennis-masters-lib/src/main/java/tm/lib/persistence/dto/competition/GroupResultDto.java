package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupResultDto {
    private String participantId;
    private int matchesPlayed;
    private int points;
    private int gamesWon;
    private int gamesLost;
}
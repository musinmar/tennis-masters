package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Stadium;
import tm.lib.persistence.dto.core.MatchScoreDto;

@Getter
@Setter
public class MatchEventDto {
    private int date;
    private String competitionId;
    private String homePlayerParticipantId;
    private String awayPlayerParticipantId;
    private int sets;
    private boolean playOff;
    private MatchScoreDto result;
}
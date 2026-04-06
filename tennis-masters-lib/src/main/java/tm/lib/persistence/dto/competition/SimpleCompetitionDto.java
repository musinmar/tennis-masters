package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimpleCompetitionDto extends AbstractCompetitionDto {
    private List<ParticipantDto> participants;
    private List<MatchEventDto> matches;
    private int nextMatchIndex;
}
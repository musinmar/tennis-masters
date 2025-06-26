package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public abstract class AbstractCompetitionDto {
    private String id;
    private String name;
    private List<CompetitionEventDto> events;
    private List<Integer> assignedParticipantIds;
}
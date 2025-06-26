package tm.lib.persistence.dto.competition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayOffSubStageResultDto {
    private List<Integer> winnerIds;
    private List<Integer> loserIds;
}
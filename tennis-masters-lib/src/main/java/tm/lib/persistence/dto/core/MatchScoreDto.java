package tm.lib.persistence.dto.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MatchScoreDto {
    private List<BasicScoreDto> sets;
    private BasicScoreDto additionalTime;
}
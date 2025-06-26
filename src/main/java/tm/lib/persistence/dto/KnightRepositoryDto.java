package tm.lib.persistence.dto;

import lombok.Getter;
import lombok.Setter;
import tm.lib.persistence.dto.core.KnightDto;

import java.util.List;

@Getter
@Setter
public class KnightRepositoryDto {
    private List<KnightDto> knights;
}

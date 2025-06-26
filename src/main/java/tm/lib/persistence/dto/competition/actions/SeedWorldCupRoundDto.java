package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeedWorldCupRoundDto extends ActionDto {
    public static final String TYPE = "seedWorldCupRound";
    
    private String previousRoundPath;
}
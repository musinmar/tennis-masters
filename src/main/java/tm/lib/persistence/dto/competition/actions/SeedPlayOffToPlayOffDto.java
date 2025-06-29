package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeedPlayOffToPlayOffDto extends ActionDto {
    public static final String TYPE = "seedPlayOffToPlayOff";
    
    private String playOffRoundPath;
}
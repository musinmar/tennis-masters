package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeedRandomPlayersDto extends ActionDto {
    public static final String TYPE = "seedRandomPlayers";
    
    private String targetPath;
}
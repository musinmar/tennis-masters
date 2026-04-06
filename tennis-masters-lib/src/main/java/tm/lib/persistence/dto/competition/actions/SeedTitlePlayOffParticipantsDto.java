package tm.lib.persistence.dto.competition.actions;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Title;

@Getter
@Setter
public class SeedTitlePlayOffParticipantsDto extends ActionDto {
    public static final String TYPE = "seedTitlePlayOffParticipants";

    private Title contendedTitle;
    private String targetPath;
}
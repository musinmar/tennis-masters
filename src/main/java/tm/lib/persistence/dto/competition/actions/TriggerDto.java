package tm.lib.persistence.dto.competition.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = OnSeasonStartDto.class, name = OnSeasonStartDto.TYPE),
    @JsonSubTypes.Type(value = OnCompetitionEndDto.class, name = OnCompetitionEndDto.TYPE),
    @JsonSubTypes.Type(value = OnAllOfCompetitionsEndDto.class, name = OnAllOfCompetitionsEndDto.TYPE),
})
@Getter
@Setter
public abstract class TriggerDto {
}
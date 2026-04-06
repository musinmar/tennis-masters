package tm.lib.persistence.dto.competition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MultiStageCompetitionDefinitionDto.class, name = MultiStageCompetitionDefinitionDto.TYPE),
    @JsonSubTypes.Type(value = GroupStageDefinitionDto.class, name = GroupStageDefinitionDto.TYPE),
    @JsonSubTypes.Type(value = PlayOffStageDefinitionDto.class, name = PlayOffStageDefinitionDto.TYPE)
})
@Getter
@Setter
public abstract class CompetitionDefinitionDto {
    private String id;
    private String name;
    private List<CompetitionEventDefinitionDto> events;
    private CompetitionScheduleDto schedule;
}
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
        @JsonSubTypes.Type(value = MultiStageCompetitionDto.class, name = MultiStageCompetitionDto.TYPE),
        @JsonSubTypes.Type(value = GroupStageDto.class, name = GroupStageDto.TYPE),
        @JsonSubTypes.Type(value = GroupCompetitionDto.class, name = GroupCompetitionDto.TYPE),
        @JsonSubTypes.Type(value = PlayOffStageDto.class, name = PlayOffStageDto.TYPE),
        @JsonSubTypes.Type(value = PlayOffRoundDto.class, name = PlayOffRoundDto.TYPE),
        @JsonSubTypes.Type(value = SeriesPlayOffRoundDto.class, name = SeriesPlayOffRoundDto.TYPE)
})
@Getter
@Setter
public abstract class AbstractCompetitionDto {
    private String id;
    private String name;
    private List<CompetitionEventDto> events;
    private List<Integer> assignedParticipantIds;
}
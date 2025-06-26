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
    @JsonSubTypes.Type(value = AddAssignedParticipantsFromEloRankingsDto.class, name = AddAssignedParticipantsFromEloRankingsDto.TYPE),
    @JsonSubTypes.Type(value = GiveTrophyDto.class, name = GiveTrophyDto.TYPE),
    @JsonSubTypes.Type(value = SeedAllFromNationRuleDto.class, name = SeedAllFromNationRuleDto.TYPE),
    @JsonSubTypes.Type(value = SeedBasedOnNationalRankingRuleDto.class, name = SeedBasedOnNationalRankingRuleDto.TYPE),
    @JsonSubTypes.Type(value = SeedGroupStageToPlayOffDto.class, name = SeedGroupStageToPlayOffDto.TYPE),
    @JsonSubTypes.Type(value = SeedPlayOffToPlayOffDto.class, name = SeedPlayOffToPlayOffDto.TYPE),
    @JsonSubTypes.Type(value = SeedRandomPlayersDto.class, name = SeedRandomPlayersDto.TYPE),
    @JsonSubTypes.Type(value = SeedWorldCupRoundDto.class, name = SeedWorldCupRoundDto.TYPE)
})
@Getter
@Setter
public abstract class ActionDto {
}
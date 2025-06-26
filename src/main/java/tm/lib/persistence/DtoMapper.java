package tm.lib.persistence;

import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.Participant;
import tm.lib.domain.competition.definition.actions.Action;
import tm.lib.domain.competition.definition.actions.Actions;
import tm.lib.domain.competition.definition.actions.CompetitionEvent;
import tm.lib.domain.competition.definition.actions.Trigger;
import tm.lib.domain.competition.definition.actions.Triggers;
import tm.lib.domain.competition.impl.AbstractCompetition;
import tm.lib.domain.competition.impl.GroupResult;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.GroupSubStage;
import tm.lib.domain.competition.impl.MultiStageCompetition;
import tm.lib.domain.competition.impl.PlayOffStage;
import tm.lib.domain.competition.impl.PlayOffSubStage;
import tm.lib.domain.competition.impl.SimpleCompetition;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.EloRating;
import tm.lib.domain.world.NationRating;
import tm.lib.domain.world.World;
import tm.lib.persistence.dto.competition.GroupResultDto;
import tm.lib.persistence.dto.core.BasicScoreDto;
import tm.lib.persistence.dto.EloRatingDto;
import tm.lib.persistence.dto.core.KnightDto;
import tm.lib.persistence.dto.core.MatchScoreDto;
import tm.lib.persistence.dto.NationRatingDto;
import tm.lib.persistence.dto.WorldDto;
import tm.lib.persistence.dto.competition.AbstractCompetitionDto;
import tm.lib.persistence.dto.competition.actions.ActionDto;
import tm.lib.persistence.dto.competition.actions.AddAssignedParticipantsFromEloRankingsDto;
import tm.lib.persistence.dto.competition.CompetitionEventDto;
import tm.lib.persistence.dto.competition.actions.GiveTrophyDto;
import tm.lib.persistence.dto.competition.GroupStageDto;
import tm.lib.persistence.dto.competition.GroupSubStageDto;
import tm.lib.persistence.dto.competition.MatchEventDto;
import tm.lib.persistence.dto.competition.MultiStageCompetitionDto;
import tm.lib.persistence.dto.competition.actions.OnCompetitionEndDto;
import tm.lib.persistence.dto.competition.actions.OnSeasonStartDto;
import tm.lib.persistence.dto.competition.ParticipantDto;
import tm.lib.persistence.dto.competition.PlayOffStageDto;
import tm.lib.persistence.dto.competition.PlayOffSubStageDto;
import tm.lib.persistence.dto.competition.actions.SeedAllFromNationRuleDto;
import tm.lib.persistence.dto.competition.actions.SeedBasedOnNationalRankingRuleDto;
import tm.lib.persistence.dto.competition.actions.SeedGroupStageToPlayOffDto;
import tm.lib.persistence.dto.competition.actions.SeedPlayOffToPlayOffDto;
import tm.lib.persistence.dto.competition.actions.SeedRandomPlayersDto;
import tm.lib.persistence.dto.competition.actions.SeedWorldCupRoundDto;
import tm.lib.persistence.dto.competition.SimpleCompetitionDto;
import tm.lib.persistence.dto.competition.actions.TriggerDto;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DtoMapper {

    public static Knight toDomain(KnightDto dto) {
        Knight knight = new Knight();
        knight.setId(dto.getId());
        knight.setName(dto.getName());
        knight.setSurname(dto.getSurname());
        knight.setNation(dto.getNation());
        knight.setCountry(dto.getCountry());
        knight.setSkills(dto.getSkills());
        knight.setTrophies(dto.getTrophies() != null ? dto.getTrophies() : knight.getTrophies());
        return knight;
    }

    public static KnightDto toDto(Knight knight) {
        KnightDto dto = new KnightDto();
        dto.setId(knight.getId());
        dto.setName(knight.getName());
        dto.setSurname(knight.getSurname());
        dto.setNation(knight.getNation());
        dto.setCountry(knight.getCountry());
        dto.setSkills(knight.getSkills());
        dto.setTrophies(knight.getTrophies());
        return dto;
    }

    public static EloRating toDomain(EloRatingDto eloRatingDto, List<Knight> knights) {
        EloRating eloRating = new EloRating(knights);
        eloRating.setRatings(eloRatingDto.getItems().stream()
                .collect(toMap(item -> knights.get(item.getPlayerId()), itemDto -> {
                    EloRating.Item rating = new EloRating.Item();
                    rating.setValue(itemDto.getPoints());
                    rating.setValuePreviousYear(itemDto.getPointsPreviousYear());
                    return rating;
                })));
        return eloRating;
    }

    public static EloRatingDto toDto(EloRating eloRating) {
        EloRatingDto eloRatingDto = new EloRatingDto();
        List<EloRatingDto.ItemDto> itemDtos = eloRating.getRatings().entrySet().stream()
                .map(e -> {
                    var itemDto = new EloRatingDto.ItemDto();
                    itemDto.setPlayerId(e.getKey().getId());
                    itemDto.setPoints(e.getValue().getValue());
                    itemDto.setPointsPreviousYear(e.getValue().getValuePreviousYear());
                    return itemDto;
                })
                .collect(toList());
        eloRatingDto.setItems(itemDtos);
        return eloRatingDto;
    }

    public static NationRating toDomain(NationRatingDto nationRatingDto, List<Knight> knights) {
        NationRating nationRating = new NationRating();
        nationRating.setPointHistoryPerNation(nationRatingDto.getPointHistory().entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> toDomain(e.getValue()))));
        nationRating.setNationRanking(nationRatingDto.getNationRanking());
        nationRating.setNationSeasonPoints(nationRatingDto.getNationSeasonPoints());
        nationRating.setPlayerSeasonPoints(nationRatingDto.getPlayerSeasonPoints().entrySet().stream()
                .collect(toMap(e -> knights.get(e.getKey()), e -> e.getValue())));
        return nationRating;
    }

    public static NationRatingDto toDto(NationRating nationRating) {
        NationRatingDto nationRatingDto = new NationRatingDto();
        nationRatingDto.setPointHistory(nationRating.getPointHistoryPerNation().entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> toDto(e.getValue()))));
        nationRatingDto.setNationRanking(nationRating.getNationRanking());
        nationRatingDto.setNationSeasonPoints(nationRating.getNationSeasonPoints());
        nationRatingDto.setPlayerSeasonPoints(nationRating.getPlayerSeasonPoints().entrySet().stream()
                .collect(toMap(e -> e.getKey().getId(), e -> e.getValue())));
        return nationRatingDto;
    }

    public static NationRating.PointHistory toDomain(NationRatingDto.PointHistoryItemDto itemDto) {
        NationRating.PointHistory item = new NationRating.PointHistory();
        item.setSeasons(itemDto.getSeasons());
        return item;
    }

    public static NationRatingDto.PointHistoryItemDto toDto(NationRating.PointHistory item) {
        NationRatingDto.PointHistoryItemDto itemDto = new NationRatingDto.PointHistoryItemDto();
        itemDto.setSeasons(item.getSeasons());
        return itemDto;
    }

    public static World toDomain(WorldDto dto) {
        World world = new World();

        world.setYear(dto.getYear());
        world.setSeasonFinished(dto.isSeasonFinished());
        world.setPlayers(dto.getKnights().stream().map(DtoMapper::toDomain).collect(toList()));
        world.setEloRating(toDomain(dto.getEloRating(), world.getPlayers()));
        world.setNationRating(toDomain(dto.getNationRating(), world.getPlayers()));

        world.startNewSeason();

        return world;
    }

    public static WorldDto toDto(World world) {
        WorldDto dto = new WorldDto();

        dto.setYear(world.getYear());
        dto.setSeasonFinished(world.isSeasonFinished());
        dto.setKnights(world.getPlayers().stream().map(DtoMapper::toDto).collect(toList()));
        dto.setEloRating(toDto(world.getEloRating()));
        dto.setNationRating(toDto(world.getNationRating()));
        dto.setSeasonCompetition(toDto(world.getSeasonCompetition()));

        return dto;
    }

    public static Participant toDomain(ParticipantDto dto, List<Knight> knights) {
        Optional<Knight> player = dto.getPlayerId() != null ? knights.stream()
                .filter(k -> k.getId() == dto.getPlayerId())
                .findFirst() : Optional.empty();
        return new Participant(dto.getId(), player);
    }

    public static ParticipantDto toDto(Participant participant) {
        ParticipantDto dto = new ParticipantDto();
        dto.setId(participant.getId());
        dto.setPlayerId(participant.getPlayer().map(Knight::getId).orElse(null));
        return dto;
    }

    public static MatchEventDto toDto(MatchEvent matchEvent) {
        MatchEventDto dto = new MatchEventDto();
        dto.setDate(matchEvent.getDate());
        dto.setCompetitionId(matchEvent.getCompetition().getId());
        dto.setHomePlayerParticipantId(matchEvent.getHomePlayer().getId());
        dto.setAwayPlayerParticipantId(matchEvent.getAwayPlayer().getId());
        dto.setSets(matchEvent.getSets());
        dto.setPlayOff(matchEvent.isPlayOff());
        dto.setResult(matchEvent.getResult() != null ? toDto(matchEvent.getResult()) : null);
        return dto;
    }

    public static MatchEvent toDomain(MatchEventDto dto, Competition competition, List<Participant> participants) {
        Participant homePlayer = participants.stream()
                .filter(p -> p.getId().equals(dto.getHomePlayerParticipantId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Home player participant not found: " + dto.getHomePlayerParticipantId()));
        
        Participant awayPlayer = participants.stream()
                .filter(p -> p.getId().equals(dto.getAwayPlayerParticipantId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Away player participant not found: " + dto.getAwayPlayerParticipantId()));
        
        MatchEvent matchEvent = new MatchEvent(competition, homePlayer, awayPlayer, dto.getSets(), dto.isPlayOff());
        matchEvent.setDate(dto.getDate());
        matchEvent.setResult(toDomain(dto.getResult()));
        return matchEvent;
    }

    public static AbstractCompetitionDto toDto(Competition competition) {
        return switch (competition) {
            case GroupStage groupStage -> toDto(groupStage);
            case GroupSubStage groupSubStage -> toDto(groupSubStage);
            case PlayOffStage playOffStage -> toDto(playOffStage);
            case PlayOffSubStage playOffSubStage -> toDto(playOffSubStage);
            case MultiStageCompetition multiStageCompetition -> toDto(multiStageCompetition);
            default ->
                    throw new IllegalArgumentException("Unknown competition type: " + competition.getClass().getName());
        };
    }

    public static SimpleCompetitionDto toDto(SimpleCompetition competition) {
        SimpleCompetitionDto dto = new SimpleCompetitionDto();
        fillAbstractCompetitionDto(dto, competition);
        dto.setParticipants(competition.getParticipants().stream().map(DtoMapper::toDto).collect(toList()));
        dto.setMatches(competition.getAllMatches().stream().map(DtoMapper::toDto).collect(toList()));
        dto.setNextMatchIndex(competition.getNextMatchIndex());
        return dto;
    }

    public static GroupStageDto toDto(GroupStage competition) {
        GroupStageDto dto = new GroupStageDto();
        fillMultiStageCompetitionDto(dto, competition);
        dto.setGroupCount(competition.getGroupCount());
        dto.setPlayersPerGroup(competition.getPlayersPerGroup());
        return dto;
    }

    public static GroupSubStageDto toDto(GroupSubStage competition) {
        GroupSubStageDto dto = new GroupSubStageDto();
        fillSimpleCompetitionDto(dto, competition);
        dto.setGroupResults(competition.getGroupResults().stream()
                .map(DtoMapper::toDto)
                .collect(toList()));
        return dto;
    }

    private static GroupResultDto toDto(GroupResult groupResult) {
        var groupResultDto = new GroupResultDto();
        groupResultDto.setParticipantId(groupResult.getParticipant().getId());
        groupResultDto.setPoints(groupResult.getPoints());
        groupResultDto.setMatchesPlayed(groupResult.getMatchesPlayed());
        groupResultDto.setGamesWon(groupResult.getGamesWon());
        groupResultDto.setGamesLost(groupResult.getGamesLost());
        return groupResultDto;
    }

    public static PlayOffStageDto toDto(PlayOffStage competition) {
        PlayOffStageDto dto = new PlayOffStageDto();
        fillMultiStageCompetitionDto(dto, competition);
        var configDto = new tm.lib.persistence.dto.competition.PlayOffStageConfigurationDto();
        configDto.setPlayerCount(competition.getParticipantCount());
        // Note: Rounds count would need to be calculated or exposed
        dto.setConfiguration(configDto);
        return dto;
    }

    public static PlayOffSubStageDto toDto(PlayOffSubStage competition) {
        PlayOffSubStageDto dto = new PlayOffSubStageDto();
        fillSimpleCompetitionDto(dto, competition);
        return dto;
    }

    public static MultiStageCompetitionDto toDto(MultiStageCompetition competition) {
        MultiStageCompetitionDto dto = new MultiStageCompetitionDto();
        fillMultiStageCompetitionDto(dto, competition);
        return dto;
    }

    private static void fillAbstractCompetitionDto(AbstractCompetitionDto dto, AbstractCompetition competition) {
        dto.setId(competition.getId());
        dto.setName(competition.getName());
        dto.setEvents(competition.getEvents().stream().map(DtoMapper::toDto).collect(toList()));
        dto.setAssignedParticipantIds(competition.getAssignedParticipants().stream().map(Knight::getId).collect(toList()));
    }

    private static void fillSimpleCompetitionDto(SimpleCompetitionDto dto, SimpleCompetition competition) {
        fillAbstractCompetitionDto(dto, competition);
        dto.setParticipants(competition.getParticipants().stream().map(DtoMapper::toDto).collect(toList()));
        dto.setMatches(competition.getAllMatches().stream().map(DtoMapper::toDto).collect(toList()));
        dto.setNextMatchIndex(competition.getNextMatchIndex());
    }

    private static void fillMultiStageCompetitionDto(MultiStageCompetitionDto dto, MultiStageCompetition competition) {
        fillAbstractCompetitionDto(dto, competition);
        dto.setStages(competition.getStages().stream().map(DtoMapper::toDto).collect(toList()));
    }

    public static CompetitionEventDto toDto(CompetitionEvent event) {
        CompetitionEventDto dto = new CompetitionEventDto();
        dto.setTrigger(toDto(event.getTrigger()));
        dto.setAction(toDto(event.getAction()));
        return dto;
    }

    public static BasicScore toDomain(BasicScoreDto dto) {
        return new BasicScore(dto.getV1(), dto.getV2());
    }

    public static BasicScoreDto toDto(BasicScore basicScore) {
        BasicScoreDto dto = new BasicScoreDto();
        dto.setV1(basicScore.v1);
        dto.setV2(basicScore.v2);
        return dto;
    }

    public static MatchScore toDomain(MatchScoreDto dto) {
        List<BasicScore> sets = dto.getSets().stream()
                .map(DtoMapper::toDomain)
                .collect(toList());
        BasicScore additionalTime = dto.getAdditionalTime() != null 
                ? toDomain(dto.getAdditionalTime()) 
                : null;
        return new MatchScore(sets, additionalTime);
    }

    public static MatchScoreDto toDto(MatchScore matchScore) {
        MatchScoreDto dto = new MatchScoreDto();
        dto.setSets(matchScore.getSets().stream()
                .map(DtoMapper::toDto)
                .collect(toList()));
        dto.setAdditionalTime(matchScore.getAdditionalTime() != null 
                ? toDto(matchScore.getAdditionalTime()) 
                : null);
        return dto;
    }

    public static TriggerDto toDto(Trigger trigger) {
        if (trigger instanceof Triggers.OnSeasonStart) {
            return new OnSeasonStartDto();
        } else if (trigger instanceof Triggers.OnCompetitionEnd onCompetitionEnd) {
            OnCompetitionEndDto dto = new OnCompetitionEndDto();
            dto.setCompetitionPath(onCompetitionEnd.getCompetitionPath());
            return dto;
        } else {
            throw new IllegalArgumentException("Unknown trigger type: " + trigger.getClass().getName());
        }
    }

    public static ActionDto toDto(Action action) {
        if (action instanceof Actions.SeedRandomPlayers) {
            return new SeedRandomPlayersDto();
        } else if (action instanceof Actions.SeedGroupStageToPlayOff seedGroupStageToPlayOff) {
            SeedGroupStageToPlayOffDto dto = new SeedGroupStageToPlayOffDto();
            dto.setGroupStagePath(seedGroupStageToPlayOff.getGroupStagePath());
            return dto;
        } else if (action instanceof Actions.SeedPlayOffToPlayOff seedPlayOffToPlayOff) {
            SeedPlayOffToPlayOffDto dto = new SeedPlayOffToPlayOffDto();
            dto.setPlayOffSubStagePath(seedPlayOffToPlayOff.getPlayOffSubStagePath());
            return dto;
        } else if (action instanceof Actions.SeedAllFromNationRule seedAllFromNationRule) {
            SeedAllFromNationRuleDto dto = new SeedAllFromNationRuleDto();
            dto.setNation(seedAllFromNationRule.getNation());
            return dto;
        } else if (action instanceof Actions.SeedBasedOnNationalRankingRule seedBasedOnNationalRankingRule) {
            SeedBasedOnNationalRankingRuleDto dto = new SeedBasedOnNationalRankingRuleDto();
            dto.setPreviousRounds(seedBasedOnNationalRankingRule.getPreviousRounds().stream()
                    .map(DtoMapper::toDto)
                    .collect(toList()));
            dto.setSeedNationalRankingParticipants(seedBasedOnNationalRankingRule.getSeedNationalRankingParticipants().stream()
                    .map(DtoMapper::toDto)
                    .collect(toList()));
            return dto;
        } else if (action instanceof Actions.AddAssignedParticipantsFromEloRankings addAssignedParticipantsFromEloRankings) {
            AddAssignedParticipantsFromEloRankingsDto dto = new AddAssignedParticipantsFromEloRankingsDto();
            dto.setFromEloRank(addAssignedParticipantsFromEloRankings.getFromEloRank());
            dto.setToEloRank(addAssignedParticipantsFromEloRankings.getToEloRank());
            return dto;
        } else if (action instanceof Actions.SeedWorldCupRound seedWorldCupRound) {
            SeedWorldCupRoundDto dto = new SeedWorldCupRoundDto();
            dto.setPreviousRoundPath(seedWorldCupRound.getPreviousRoundPath().orElse(null));
            return dto;
        } else if (action instanceof Actions.GiveTrophy) {
            return new GiveTrophyDto();
        } else {
            throw new IllegalArgumentException("Unknown action type: " + action.getClass().getName());
        }
    }

    public static SeedBasedOnNationalRankingRuleDto.PreviousRoundDto toDto(Actions.SeedBasedOnNationalRankingRule.PreviousRound previousRound) {
        SeedBasedOnNationalRankingRuleDto.PreviousRoundDto dto = new SeedBasedOnNationalRankingRuleDto.PreviousRoundDto();
        dto.setPath(previousRound.getPath());
        dto.setWinners(previousRound.isWinners());
        return dto;
    }

    public static SeedBasedOnNationalRankingRuleDto.SeedNationalRankingParticipantDto toDto(Actions.SeedBasedOnNationalRankingRule.SeedNationalRankingParticipant participant) {
        SeedBasedOnNationalRankingRuleDto.SeedNationalRankingParticipantDto dto = new SeedBasedOnNationalRankingRuleDto.SeedNationalRankingParticipantDto();
        dto.setNationRank(participant.getNationRank());
        dto.setPlayerRankInNation(participant.getPlayerRankInNation());
        return dto;
    }
}
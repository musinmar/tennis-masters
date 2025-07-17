package tm.lib.persistence;

import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.CompetitionEvent;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.Participant;
import tm.lib.domain.competition.definition.actions.Action;
import tm.lib.domain.competition.definition.actions.AddAssignedParticipantsFromEloRankings;
import tm.lib.domain.competition.definition.actions.AwardTitle;
import tm.lib.domain.competition.definition.actions.GiveTrophy;
import tm.lib.domain.competition.definition.actions.SeedAllFromNationRule;
import tm.lib.domain.competition.definition.actions.SeedBasedOnNationalRankingRule;
import tm.lib.domain.competition.definition.actions.SeedBasedOnNationalRankingRule.SeedNationalRankingParticipant;
import tm.lib.domain.competition.definition.actions.SeedGroupStageToPlayOff;
import tm.lib.domain.competition.definition.actions.SeedPlayOffToPlayOff;
import tm.lib.domain.competition.definition.actions.SeedRandomPlayers;
import tm.lib.domain.competition.definition.actions.SeedTitlePlayOffParticipants;
import tm.lib.domain.competition.definition.actions.SeedWorldCupRound;
import tm.lib.domain.competition.definition.triggers.OnAllOfCompetitionsEnd;
import tm.lib.domain.competition.definition.triggers.OnCompetitionEnd;
import tm.lib.domain.competition.definition.triggers.OnSeasonStart;
import tm.lib.domain.competition.definition.triggers.Trigger;
import tm.lib.domain.competition.impl.AbstractCompetition;
import tm.lib.domain.competition.impl.AbstractMultiStageCompetition;
import tm.lib.domain.competition.impl.GroupCompetition;
import tm.lib.domain.competition.impl.GroupResult;
import tm.lib.domain.competition.impl.GroupStage;
import tm.lib.domain.competition.impl.MultiStageCompetition;
import tm.lib.domain.competition.impl.PlayOffRound;
import tm.lib.domain.competition.impl.PlayOffStage;
import tm.lib.domain.competition.impl.SeriesPlayOffRound;
import tm.lib.domain.competition.impl.SimpleCompetition;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Title;
import tm.lib.domain.world.EloRating;
import tm.lib.domain.world.KnightRepository;
import tm.lib.domain.world.NationRating;
import tm.lib.domain.world.World;
import tm.lib.persistence.dto.EloRatingDto;
import tm.lib.persistence.dto.KnightRepositoryDto;
import tm.lib.persistence.dto.NationRatingDto;
import tm.lib.persistence.dto.WorldDto;
import tm.lib.persistence.dto.competition.AbstractCompetitionDto;
import tm.lib.persistence.dto.competition.CompetitionEventDto;
import tm.lib.persistence.dto.competition.GroupCompetitionDto;
import tm.lib.persistence.dto.competition.GroupResultDto;
import tm.lib.persistence.dto.competition.GroupStageDto;
import tm.lib.persistence.dto.competition.MatchEventDto;
import tm.lib.persistence.dto.competition.MultiStageCompetitionDto;
import tm.lib.persistence.dto.competition.ParticipantDto;
import tm.lib.persistence.dto.competition.PlayOffRoundDto;
import tm.lib.persistence.dto.competition.PlayOffStageDto;
import tm.lib.persistence.dto.competition.SeriesPlayOffRoundDto;
import tm.lib.persistence.dto.competition.SimpleCompetitionDto;
import tm.lib.persistence.dto.competition.actions.ActionDto;
import tm.lib.persistence.dto.competition.actions.AddAssignedParticipantsFromEloRankingsDto;
import tm.lib.persistence.dto.competition.actions.AwardTitleDto;
import tm.lib.persistence.dto.competition.actions.GiveTrophyDto;
import tm.lib.persistence.dto.competition.actions.OnAllOfCompetitionsEndDto;
import tm.lib.persistence.dto.competition.actions.OnCompetitionEndDto;
import tm.lib.persistence.dto.competition.actions.OnSeasonStartDto;
import tm.lib.persistence.dto.competition.actions.SeedAllFromNationRuleDto;
import tm.lib.persistence.dto.competition.actions.SeedBasedOnNationalRankingRuleDto;
import tm.lib.persistence.dto.competition.actions.SeedBasedOnNationalRankingRuleDto.PreviousRoundDto;
import tm.lib.persistence.dto.competition.actions.SeedBasedOnNationalRankingRuleDto.SeedNationalRankingParticipantDto;
import tm.lib.persistence.dto.competition.actions.SeedGroupStageToPlayOffDto;
import tm.lib.persistence.dto.competition.actions.SeedPlayOffToPlayOffDto;
import tm.lib.persistence.dto.competition.actions.SeedRandomPlayersDto;
import tm.lib.persistence.dto.competition.actions.SeedTitlePlayOffParticipantsDto;
import tm.lib.persistence.dto.competition.actions.SeedWorldCupRoundDto;
import tm.lib.persistence.dto.competition.actions.TriggerDto;
import tm.lib.persistence.dto.core.BasicScoreDto;
import tm.lib.persistence.dto.core.KnightDto;
import tm.lib.persistence.dto.core.MatchScoreDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static tm.lib.domain.competition.CompetitionPath.createPathTo;

public class DtoMapper {

    public static Knight toDomain(KnightDto dto) {
        Knight knight = new Knight();
        knight.setId(dto.getId());
        knight.setName(dto.getName());
        knight.setSurname(dto.getSurname());
        knight.setNation(dto.getNation());
        knight.setCountry(dto.getCountry());
        knight.setSkills(dto.getSkills());
        knight.setTitle(dto.getTitle() != null ? dto.getTitle() : Title.NONE);
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
        dto.setTitle(knight.getTitle());
        dto.setTrophies(knight.getTrophies());
        return dto;
    }

    public static EloRating toDomain(EloRatingDto eloRatingDto, KnightRepository knightRepository) {
        EloRating eloRating = new EloRating();
        eloRating.setRatings(eloRatingDto.getItems().stream()
                .collect(toMap(item -> knightRepository.getById(item.getPlayerId()), itemDto -> {
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

    public static NationRating toDomain(NationRatingDto nationRatingDto, KnightRepository knightRepository) {
        NationRating nationRating = new NationRating();
        nationRating.setPointHistoryPerNation(nationRatingDto.getPointHistory().entrySet().stream()
                .collect(toMap(e -> e.getKey(),
                        e -> toDomain(e.getValue()))));
        nationRating.setNationRanking(nationRatingDto.getNationRanking());
        nationRating.setNationSeasonPoints(nationRatingDto.getNationSeasonPoints());
        nationRating.setPlayerSeasonPoints(nationRatingDto.getPlayerSeasonPoints().entrySet().stream()
                .collect(toMap(e -> knightRepository.getById(e.getKey()),
                        e -> e.getValue())));
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

    public static KnightRepository toDomain(KnightRepositoryDto dto) {
        KnightRepository knightRepository = new KnightRepository();
        knightRepository.setKnights(dto.getKnights().stream().map(DtoMapper::toDomain).toList());
        return knightRepository;
    }

    public static KnightRepositoryDto toDto(KnightRepository knightRepository) {
        KnightRepositoryDto dto = new KnightRepositoryDto();
        dto.setKnights(knightRepository.getAll().stream().map(DtoMapper::toDto).toList());
        return dto;
    }

    public static World toDomain(WorldDto dto) {
        World world = new World();

        world.setYear(dto.getYear());
        world.setState(dto.getState());
        world.setKnightRepository(toDomain(dto.getKnightRepository()));
        world.setEloRating(toDomain(dto.getEloRating(), world.getKnightRepository()));
        world.setNationRating(toDomain(dto.getNationRating(), world.getKnightRepository()));
        world.setSeasonCompetition(toDomain(dto.getSeasonCompetition(), world.getKnightRepository()));
        world.initCompetitionPointValues(dto.getCompetitionPointValues());

        world.initializeEventProcessor();

        return world;
    }

    public static WorldDto toDto(World world) {
        WorldDto dto = new WorldDto();

        dto.setYear(world.getYear());
        dto.setState(world.getState());
        dto.setKnightRepository(toDto(world.getKnightRepository()));
        dto.setEloRating(toDto(world.getEloRating()));
        dto.setNationRating(toDto(world.getNationRating()));

        var seasonCompetition = world.getSeasonCompetition();
        dto.setSeasonCompetition(toDto(seasonCompetition));
        Map<String, Integer> competitionPointValues = world.getCompetitionPointValues().entrySet().stream()
                .collect(toMap(
                        entry -> createPathTo(seasonCompetition, entry.getKey()),
                        Map.Entry::getValue
                ));
        dto.setCompetitionPointValues(competitionPointValues);

        return dto;
    }

    public static Participant toDomain(ParticipantDto dto, KnightRepository knightRepository) {
        Optional<Knight> player = Optional.ofNullable(
                dto.getPlayerId() != null ? knightRepository.getById(dto.getPlayerId()) : null
        );
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
        matchEvent.setResult(dto.getResult() != null ? toDomain(dto.getResult()) : null);
        return matchEvent;
    }

    public static AbstractCompetitionDto toDto(Competition competition) {
        return switch (competition) {
            case GroupStage groupStage -> toDto(groupStage);
            case GroupCompetition groupCompetition -> toDto(groupCompetition);
            case PlayOffStage playOffStage -> toDto(playOffStage);
            case SeriesPlayOffRound seriesPlayOffRound -> toDto(seriesPlayOffRound);
            case PlayOffRound playOffRound -> toDto(playOffRound);
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
        return dto;
    }

    public static GroupCompetitionDto toDto(GroupCompetition competition) {
        GroupCompetitionDto dto = new GroupCompetitionDto();
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
        return dto;
    }

    public static PlayOffRoundDto toDto(PlayOffRound competition) {
        PlayOffRoundDto dto = new PlayOffRoundDto();
        fillSimpleCompetitionDto(dto, competition);
        return dto;
    }

    public static SeriesPlayOffRoundDto toDto(SeriesPlayOffRound competition) {
        SeriesPlayOffRoundDto dto = new SeriesPlayOffRoundDto();
        fillSimpleCompetitionDto(dto, competition);
        dto.setWinCount(competition.getWinCount());
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

    private static void fillMultiStageCompetitionDto(MultiStageCompetitionDto dto, AbstractMultiStageCompetition competition) {
        fillAbstractCompetitionDto(dto, competition);
        dto.setStages(competition.getChildren().stream().map(DtoMapper::toDto).collect(toList()));
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
        if (trigger instanceof OnSeasonStart) {
            return new OnSeasonStartDto();
        } else if (trigger instanceof OnCompetitionEnd onCompetitionEnd) {
            OnCompetitionEndDto dto = new OnCompetitionEndDto();
            dto.setCompetitionPath(onCompetitionEnd.getCompetitionPath());
            return dto;
        } else if (trigger instanceof OnAllOfCompetitionsEnd onAllOfCompetitionsEnd) {
            OnAllOfCompetitionsEndDto dto = new OnAllOfCompetitionsEndDto();
            dto.setCompetitionPaths(onAllOfCompetitionsEnd.getCompetitionPaths());
            return dto;
        } else {
            throw new IllegalArgumentException("Unknown trigger type: " + trigger.getClass().getName());
        }
    }

    public static ActionDto toDto(Action action) {
        switch (action) {
            case SeedRandomPlayers seedRandomPlayers -> {
                SeedRandomPlayersDto dto = new SeedRandomPlayersDto();
                dto.setTargetPath(seedRandomPlayers.getTargetPath());
                return dto;
            }
            case SeedGroupStageToPlayOff seedGroupStageToPlayOff -> {
                SeedGroupStageToPlayOffDto dto = new SeedGroupStageToPlayOffDto();
                dto.setGroupStagePath(seedGroupStageToPlayOff.getGroupStagePath());
                dto.setTargetPath(seedGroupStageToPlayOff.getTargetPath());
                return dto;
            }
            case SeedPlayOffToPlayOff seedPlayOffToPlayOff -> {
                SeedPlayOffToPlayOffDto dto = new SeedPlayOffToPlayOffDto();
                dto.setPlayOffRoundPath(seedPlayOffToPlayOff.getPlayOffRoundPath());
                dto.setTargetPath(seedPlayOffToPlayOff.getTargetPath());
                return dto;
            }
            case SeedAllFromNationRule seedAllFromNationRule -> {
                SeedAllFromNationRuleDto dto = new SeedAllFromNationRuleDto();
                dto.setNation(seedAllFromNationRule.getNation());
                dto.setTargetPath(seedAllFromNationRule.getTargetPath());
                return dto;
            }
            case SeedBasedOnNationalRankingRule seedBasedOnNationalRankingRule -> {
                SeedBasedOnNationalRankingRuleDto dto = new SeedBasedOnNationalRankingRuleDto();
                dto.setPreviousRounds(seedBasedOnNationalRankingRule.getPreviousRounds().stream()
                        .map(DtoMapper::toDto)
                        .collect(toList()));
                dto.setSeedNationalRankingParticipants(seedBasedOnNationalRankingRule.getSeedNationalRankingParticipants().stream()
                        .map(DtoMapper::toDto)
                        .collect(toList()));
                dto.setTargetPath(seedBasedOnNationalRankingRule.getTargetPath());
                return dto;
            }
            case AddAssignedParticipantsFromEloRankings addAssignedParticipantsFromEloRankings -> {
                AddAssignedParticipantsFromEloRankingsDto dto = new AddAssignedParticipantsFromEloRankingsDto();
                dto.setFromEloRank(addAssignedParticipantsFromEloRankings.getFromEloRank());
                dto.setToEloRank(addAssignedParticipantsFromEloRankings.getToEloRank());
                dto.setTargetPath(addAssignedParticipantsFromEloRankings.getTargetPath());
                return dto;
            }
            case SeedWorldCupRound seedWorldCupRound -> {
                SeedWorldCupRoundDto dto = new SeedWorldCupRoundDto();
                dto.setPreviousRoundPath(seedWorldCupRound.getPreviousRoundPath().orElse(null));
                dto.setTargetPath(seedWorldCupRound.getTargetPath());
                return dto;
            }
            case GiveTrophy giveTrophy -> {
                GiveTrophyDto dto = new GiveTrophyDto();
                dto.setTargetPath(giveTrophy.getTargetPath());
                return dto;
            }
            case SeedTitlePlayOffParticipants seedTitlePlayOffParticipants -> {
                var dto = new SeedTitlePlayOffParticipantsDto();
                dto.setContendedTitle(seedTitlePlayOffParticipants.getContendedTitle());
                dto.setTargetPath(seedTitlePlayOffParticipants.getTargetPath());
                return dto;
            }
            case AwardTitle awardTitle -> {
                var dto = new AwardTitleDto();
                dto.setTitle(awardTitle.getTitle());
                dto.setTargetPath(awardTitle.getTargetPath());
                return dto;
            }
            default -> throw new IllegalArgumentException("Unknown action type: " + action.getClass().getName());
        }
    }

    public static PreviousRoundDto toDto(SeedBasedOnNationalRankingRule.PreviousRound previousRound) {
        PreviousRoundDto dto = new PreviousRoundDto();
        dto.setPath(previousRound.getPath());
        dto.setWinners(previousRound.isWinners());
        return dto;
    }

    public static SeedNationalRankingParticipantDto toDto(SeedNationalRankingParticipant participant) {
        SeedNationalRankingParticipantDto dto = new SeedNationalRankingParticipantDto();
        dto.setNationRank(participant.getNationRank());
        dto.setPlayerRankInNation(participant.getPlayerRankInNation());
        return dto;
    }

    public static Competition toDomain(AbstractCompetitionDto dto, KnightRepository knightRepository) {
        return switch (dto) {
            case GroupStageDto groupStageDto -> toDomain(groupStageDto, knightRepository);
            case GroupCompetitionDto groupCompetitionDto -> toDomain(groupCompetitionDto, knightRepository);
            case PlayOffStageDto playOffStageDto -> toDomain(playOffStageDto, knightRepository);
            case SeriesPlayOffRoundDto seriesPlayOffRoundDto -> toDomain(seriesPlayOffRoundDto, knightRepository);
            case PlayOffRoundDto playOffRoundDto -> toDomain(playOffRoundDto, knightRepository);
            case MultiStageCompetitionDto multiStageDto -> toDomain(multiStageDto, knightRepository);
            default -> throw new IllegalArgumentException("Unknown competition DTO type: " + dto.getClass().getName());
        };
    }

    public static void fillMultiStageCompetitionFromDto(AbstractMultiStageCompetition multiStage,
                                                        MultiStageCompetitionDto dto,
                                                        KnightRepository knightRepository) {
        fillAbstractCompetitionFromDto(multiStage, dto, knightRepository);
    }

    public static void fillSimpleCompetitionFromDto(SimpleCompetition competition,
                                                    SimpleCompetitionDto dto,
                                                    KnightRepository knightRepository) {
        fillAbstractCompetitionFromDto(competition, dto, knightRepository);

        List<Participant> participants = dto.getParticipants().stream()
                .map(p -> toDomain(p, knightRepository))
                .collect(toList());
        competition.setParticipants(participants);

        List<MatchEvent> matches = dto.getMatches().stream()
                .map(m -> toDomain(m, competition, participants))
                .collect(toList());
        competition.setMatches(matches);
        competition.setNextMatchIndex(dto.getNextMatchIndex());
    }

    public static GroupStage toDomain(GroupStageDto dto, KnightRepository knightRepository) {
        GroupStage groupStage = new GroupStage(dto.getId(), dto.getName());
        ;
        fillMultiStageCompetitionFromDto(groupStage, dto, knightRepository);
        List<GroupCompetition> groups = dto.getStages().stream()
                .map(stageDto -> (GroupCompetition) toDomain(stageDto, knightRepository))
                .collect(toList());
        groupStage.setGroups(groups);
        return groupStage;
    }

    public static GroupCompetition toDomain(GroupCompetitionDto dto, KnightRepository knightRepository) {
        GroupCompetition groupCompetition = new GroupCompetition(dto.getId(), dto.getName());
        fillSimpleCompetitionFromDto(groupCompetition, dto, knightRepository);

        List<GroupResult> groupResults = dto.getGroupResults().stream()
                .map(gr -> toDomain(gr, groupCompetition.getParticipants()))
                .collect(toList());
        groupCompetition.setGroupResults(groupResults);

        return groupCompetition;
    }

    public static PlayOffStage toDomain(PlayOffStageDto dto, KnightRepository knightRepository) {
        PlayOffStage playOffStage = new PlayOffStage(dto.getId(), dto.getName());
        fillMultiStageCompetitionFromDto(playOffStage, dto, knightRepository);
        List<PlayOffRound> rounds = dto.getStages().stream()
                .map(stageDto -> (PlayOffRound) toDomain(stageDto, knightRepository))
                .collect(toList());
        playOffStage.setRounds(rounds);
        return playOffStage;
    }

    public static PlayOffRound toDomain(PlayOffRoundDto dto, KnightRepository knightRepository) {
        PlayOffRound playOffRound = new PlayOffRound(dto.getId(), dto.getName());
        fillSimpleCompetitionFromDto(playOffRound, dto, knightRepository);
        return playOffRound;
    }

    public static SeriesPlayOffRound toDomain(SeriesPlayOffRoundDto dto, KnightRepository knightRepository) {
        SeriesPlayOffRound seriesPlayOffRound = new SeriesPlayOffRound(dto.getId(), dto.getName());
        fillSimpleCompetitionFromDto(seriesPlayOffRound, dto, knightRepository);
        seriesPlayOffRound.setWinCount(dto.getWinCount());
        return seriesPlayOffRound;
    }

    public static MultiStageCompetition toDomain(MultiStageCompetitionDto dto, KnightRepository knightRepository) {
        MultiStageCompetition multiStage = new MultiStageCompetition(dto.getId(), dto.getName());
        fillMultiStageCompetitionFromDto(multiStage, dto, knightRepository);
        List<Competition> rounds = dto.getStages().stream()
                .map(stageDto -> toDomain(stageDto, knightRepository))
                .collect(toList());
        multiStage.setStages(rounds);
        return multiStage;
    }

    public static GroupResult toDomain(GroupResultDto dto, List<Participant> participants) {
        Participant participant = participants.stream()
                .filter(p -> p.getId().equals(dto.getParticipantId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + dto.getParticipantId()));

        GroupResult groupResult = new GroupResult(participant);
        groupResult.setPoints(dto.getPoints());
        groupResult.setMatchesPlayed(dto.getMatchesPlayed());
        groupResult.setGamesWon(dto.getGamesWon());
        groupResult.setGamesLost(dto.getGamesLost());
        return groupResult;
    }

    public static CompetitionEvent toDomain(CompetitionEventDto dto,
                                            Competition competition) {
        Trigger trigger = toDomain(dto.getTrigger());
        Action action = toDomain(dto.getAction());
        return new CompetitionEvent(trigger, action);
    }

    private static void fillAbstractCompetitionFromDto(AbstractCompetition competition,
                                                       AbstractCompetitionDto dto,
                                                       KnightRepository knightRepository) {
        List<CompetitionEvent> events = dto.getEvents().stream()
                .map(eventDto -> toDomain(eventDto, competition))
                .collect(toList());
        competition.setEvents(events);

        List<Knight> assignedParticipants = dto.getAssignedParticipantIds().stream()
                .map(knightRepository::getById)
                .collect(toList());
        competition.setAssignedParticipants(assignedParticipants);
    }

    public static Trigger toDomain(TriggerDto dto) {
        return switch (dto) {
            case OnSeasonStartDto ignored -> new OnSeasonStart();
            case OnCompetitionEndDto onCompetitionEndDto ->
                    new OnCompetitionEnd(onCompetitionEndDto.getCompetitionPath());
            case OnAllOfCompetitionsEndDto onAllOfCompetitionsEndDto ->
                    new OnAllOfCompetitionsEnd(onAllOfCompetitionsEndDto.getCompetitionPaths());
            default -> throw new IllegalArgumentException("Unknown trigger DTO type: " + dto.getClass().getName());
        };
    }

    public static Action toDomain(ActionDto dto) {
        return switch (dto) {
            case SeedRandomPlayersDto seedRandomPlayersDto -> new SeedRandomPlayers(seedRandomPlayersDto.getTargetPath());
            case SeedGroupStageToPlayOffDto seedGroupStageDto ->
                    new SeedGroupStageToPlayOff(seedGroupStageDto.getGroupStagePath(), seedGroupStageDto.getTargetPath());
            case SeedPlayOffToPlayOffDto seedPlayOffDto ->
                    new SeedPlayOffToPlayOff(seedPlayOffDto.getPlayOffRoundPath(), seedPlayOffDto.getTargetPath());
            case SeedAllFromNationRuleDto seedAllFromNationDto ->
                    new SeedAllFromNationRule(seedAllFromNationDto.getNation(), seedAllFromNationDto.getTargetPath());
            case SeedBasedOnNationalRankingRuleDto seedBasedOnNationalRankingDto -> {
                List<SeedBasedOnNationalRankingRule.PreviousRound> previousRounds = seedBasedOnNationalRankingDto.getPreviousRounds().stream()
                        .map(DtoMapper::toDomain)
                        .collect(toList());
                List<SeedNationalRankingParticipant> seedParticipants = seedBasedOnNationalRankingDto.getSeedNationalRankingParticipants().stream()
                        .map(DtoMapper::toDomain)
                        .collect(toList());
                yield new SeedBasedOnNationalRankingRule(previousRounds, seedParticipants, seedBasedOnNationalRankingDto.getTargetPath());
            }
            case AddAssignedParticipantsFromEloRankingsDto addAssignedDto ->
                    new AddAssignedParticipantsFromEloRankings(addAssignedDto.getFromEloRank(), addAssignedDto.getToEloRank(), addAssignedDto.getTargetPath());
            case SeedWorldCupRoundDto seedWorldCupDto ->
                    new SeedWorldCupRound(Optional.ofNullable(seedWorldCupDto.getPreviousRoundPath()), seedWorldCupDto.getTargetPath());
            case GiveTrophyDto giveTrophyDto -> new GiveTrophy(giveTrophyDto.getTargetPath());
            case SeedTitlePlayOffParticipantsDto seedTitlePlayOffParticipantsDto ->
                    new SeedTitlePlayOffParticipants(seedTitlePlayOffParticipantsDto.getContendedTitle(), seedTitlePlayOffParticipantsDto.getTargetPath());
            case AwardTitleDto awardTitleDto -> new AwardTitle(awardTitleDto.getTitle(), awardTitleDto.getTargetPath());
            default -> throw new IllegalArgumentException("Unknown action DTO type: " + dto.getClass().getName());
        };
    }

    public static SeedBasedOnNationalRankingRule.PreviousRound toDomain(PreviousRoundDto dto) {
        return new SeedBasedOnNationalRankingRule.PreviousRound(dto.getPath(), dto.isWinners());
    }

    public static SeedNationalRankingParticipant toDomain(SeedNationalRankingParticipantDto dto) {
        return new SeedNationalRankingParticipant(dto.getNationRank(), dto.getPlayerRankInNation());
    }
}
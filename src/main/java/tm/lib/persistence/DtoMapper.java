package tm.lib.persistence;

import tm.lib.domain.core.Knight;
import tm.lib.domain.world.EloRating;
import tm.lib.domain.world.NationRating;
import tm.lib.domain.world.World;
import tm.lib.persistence.dto.EloRatingDto;
import tm.lib.persistence.dto.KnightDto;
import tm.lib.persistence.dto.NationRatingDto;
import tm.lib.persistence.dto.WorldDto;

import java.util.List;

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
                .collect(toMap(item -> knights.get(item.getPlayerId()), item -> new EloRating.Rating(item.getPoints()))));
        eloRating.normalize();
        eloRating.savePointsAsLastYearPoints();
        return eloRating;
    }

    public static EloRatingDto toDto(EloRating eloRating) {
        EloRatingDto eloRatingDto = new EloRatingDto();
        List<EloRatingDto.ItemDto> itemDtos = eloRating.getRatings().entrySet().stream()
                .map(e -> new EloRatingDto.ItemDto(e.getKey().getId(), e.getValue().getValue()))
                .collect(toList());
        eloRatingDto.setItems(itemDtos);
        return eloRatingDto;
    }

    public static NationRating toDomain(NationRatingDto nationRatingDto) {
        NationRating nationRating = new NationRating();
        nationRating.setPointHistoryPerNation(nationRatingDto.getPointHistory().entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> toDomain(e.getValue()))));
        nationRating.updateNationRanking();
        nationRating.resetSeasonPoints();
        return nationRating;
    }

    public static NationRatingDto toDto(NationRating nationRating) {
        NationRatingDto nationRatingDto = new NationRatingDto();
        nationRatingDto.setPointHistory(nationRating.getPointHistoryPerNation().entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> toDto(e.getValue()))));
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
        world.setNationRating(toDomain(dto.getNationRating()));

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

        return dto;
    }
}
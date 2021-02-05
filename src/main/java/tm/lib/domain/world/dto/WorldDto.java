package tm.lib.domain.world.dto;

import tm.lib.domain.core.dto.KnightDto;

import java.util.List;

public class WorldDto {

    private int year;
    private List<KnightDto> knights;
    private EloRatingDto eloRating;
    private NationRatingDto nationRating;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<KnightDto> getKnights() {
        return knights;
    }

    public void setKnights(List<KnightDto> knights) {
        this.knights = knights;
    }

    public EloRatingDto getEloRating() {
        return eloRating;
    }

    public void setEloRating(EloRatingDto eloRating) {
        this.eloRating = eloRating;
    }

    public NationRatingDto getNationRating() {
        return nationRating;
    }

    public void setNationRating(NationRatingDto nationRating) {
        this.nationRating = nationRating;
    }
}

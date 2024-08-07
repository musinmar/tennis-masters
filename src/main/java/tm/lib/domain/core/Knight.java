package tm.lib.domain.core;

import lombok.*;
import tm.lib.domain.core.dto.KnightDto;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Knight {
    private int id;

    private String name;
    private String surname;

    private Nation nation;
    private Country country;

    private SkillSet skills;

    private List<Trophy> trophies = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trophy {
        private int year;
        private String competitionName;
    }

    public Knight() {
        id = -1;
        name = "Nil_name";
        surname = "Nil_surname";
        nation = Nation.ALMAGEST;
        country = Country.ALDORUM;
        skills = new SkillSet();
    }

    public static Knight fromDto(KnightDto dto) {
        Knight knight = new Knight();
        knight.id = dto.getId();
        knight.name = dto.getName();
        knight.surname = dto.getSurname();
        knight.nation = dto.getNation();
        knight.country = dto.getCountry();
        knight.skills = dto.getSkills();
        knight.trophies = dto.getTrophies() != null ? dto.getTrophies() : knight.trophies;
        return knight;
    }

    public KnightDto toDto() {
        KnightDto dto = new KnightDto();
        dto.setId(id);
        dto.setName(name);
        dto.setSurname(surname);
        dto.setNation(nation);
        dto.setCountry(country);
        dto.setSkills(skills);
        dto.setTrophies(trophies);
        return dto;
    }

    public String getFullName() {
        return getName() + " " + getSurname();
    }

    public String getShortName() {
        return getName().charAt(0) + ". " + getSurname();
    }

    public void randomizeSkills() {
        skills = SkillSet.randomSkillSet();
    }

    public void addTrophy(int year, String competitionName) {
        trophies.add(new Trophy(year, competitionName));
    }
}

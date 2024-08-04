package tm.lib.domain.core;

import lombok.Getter;
import tm.lib.domain.core.dto.KnightDto;

@Getter
public class Knight {
    private int id;

    private String name;
    private String surname;

    private Nation nation;
    private Country country;

    private SkillSet skills;

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
        return dto;
    }

    public String getFullName() {
        return getName() + " " + getSurname();
    }

    public String getShortName() {
        return getName().charAt(0) + ". " + getSurname();
    }

    static int randomIndex = 1;

    public static Knight getRandomPerson() {
        Knight a = new Knight();
        a.name = "Энди";
        a.surname = "Рэндом_" + randomIndex++;
        a.country = Country.ALDORUM;
        a.nation = Nation.GALILEO;
        a.skills = SkillSet.randomSkillSet();
        return a;
    }
}

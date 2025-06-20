package tm.lib.domain.core;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

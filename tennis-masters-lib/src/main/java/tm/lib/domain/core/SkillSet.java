package tm.lib.domain.core;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

@Getter
@Setter
public class SkillSet {
    private double speed;
    private double acceleration;
    private double hitPower;
    private double shotRange;
    private double accuracy;
    private double cunning;
    private double intelligence;
    private double risk;
    private double endurance;
    private double dexterity;

    public SkillSet() {
        speed = 50;
        acceleration = 50;
        hitPower = 50;
        shotRange = 50;
        accuracy = 50;
        cunning = 50;
        intelligence = 50;
        risk = 50;
        endurance = 50;
        dexterity = 50;
    }

    public static SkillSet randomSkillSet() {
        SkillSet skills = new SkillSet();
        skills.speed = RandomUtils.nextDouble(0, 100);
        skills.acceleration = RandomUtils.nextDouble(0, 100);
        skills.hitPower = RandomUtils.nextDouble(0, 100);
        skills.shotRange = RandomUtils.nextDouble(0, 100);
        skills.accuracy = RandomUtils.nextDouble(0, 100);
        skills.cunning = RandomUtils.nextDouble(0, 100);
        skills.intelligence = RandomUtils.nextDouble(0, 100);
        skills.risk = RandomUtils.nextDouble(0, 100);
        skills.endurance = RandomUtils.nextDouble(0, 100);
        skills.dexterity = RandomUtils.nextDouble(0, 100);
        return skills;
    }
}
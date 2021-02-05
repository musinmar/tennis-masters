package tm.lib.domain.core;

import tm.lib.domain.core.dto.KnightDto;

public class Knight {
    private int id;

    private String name;
    private String surname;

    private Nation nation;
    private Country country;

    private double speed;
    private double acceleration;
    private double hitPower;
    private double shotRange;
    private double accuracy;
    private double cunning;
    private double skill;
    private double risk;
    private double endurance;
    private double dexterity;

    public Knight() {
        id = -1;

        name = "Nil_name";
        surname = "Nil_surname";
        nation = Nation.ALMAGEST;
        country = Country.ALDORUM;

        speed = 50;
        acceleration = 50;
        hitPower = 50;
        shotRange = 50;
        accuracy = 50;
        cunning = 50;
        skill = 50;
        risk = 50;
        endurance = 50;
        dexterity = 50;
    }

    public static Knight fromDto(KnightDto dto) {
        Knight knight = new Knight();
        knight.id = dto.getId();
        knight.name = dto.getName();
        knight.surname = dto.getSurname();
        knight.nation = dto.getNation();
        knight.country = dto.getCountry();
        knight.speed = dto.getSpeed();
        knight.acceleration = dto.getAcceleration();
        knight.hitPower = dto.getHitPower();
        knight.shotRange = dto.getShotRange();
        knight.accuracy = dto.getAccuracy();
        knight.cunning = dto.getCunning();
        knight.skill = dto.getSkill();
        knight.risk = dto.getRisk();
        knight.endurance = dto.getEndurance();
        knight.dexterity = dto.getDexterity();
        return knight;
    }

    public KnightDto toDto() {
        KnightDto dto = new KnightDto();
        dto.setId(id);
        dto.setName(name);
        dto.setSurname(surname);
        dto.setNation(nation);
        dto.setCountry(country);
        dto.setSpeed(speed);
        dto.setAcceleration(acceleration);
        dto.setHitPower(hitPower);
        dto.setShotRange(shotRange);
        dto.setAccuracy(accuracy);
        dto.setCunning(cunning);
        dto.setSkill(skill);
        dto.setRisk(risk);
        dto.setEndurance(endurance);
        dto.setDexterity(dexterity);
        return dto;
    }

    public String getFullName() {
        return getName() + " " + getSurname();
    }

    public String getShortName() {
        return getName().charAt(0) + ". " + getSurname();
    }

    public static Knight getTestPerson1() {
        Knight a = new Knight();
        a.name = "Флер";
        a.surname = "Рокки";
        a.country = Country.CONJUCTION;
        a.nation = Nation.ALMAGEST;

        a.speed = 50;
        a.acceleration = 50;
        a.hitPower = 50;
        a.shotRange = 50;
        a.accuracy = 50;
        a.cunning = 50;
        a.skill = 50;
        a.risk = 50;
        a.endurance = 50;
        a.dexterity = 100;

        return a;
    }

    public static Knight getTestPerson2() {
        Knight b = new Knight();
        b.name = "Майкл";
        b.surname = "Холл";
        b.country = Country.ALDORUM;
        b.nation = Nation.GALILEO;

        b.speed = 50;
        b.acceleration = 50;
        b.hitPower = 50;
        b.shotRange = 50;
        b.accuracy = 50;
        b.cunning = 50;
        b.skill = 50;
        b.risk = 50;
        b.endurance = 50;
        b.dexterity = 0;

        return b;
    }

    static int randomIndex = 1;

    public static Knight getRandomPerson() {
        Knight a = new Knight();
        java.util.Random r = new java.util.Random(System.currentTimeMillis());

        a.name = "Энди";
        a.surname = "Рэндом_" + randomIndex++;
        a.country = Country.ALDORUM;
        a.nation = Nation.GALILEO;

        a.speed = r.nextDouble() * 100;
        a.acceleration = r.nextDouble() * 100;
        a.hitPower = r.nextDouble() * 100;
        a.shotRange = r.nextDouble() * 100;
        a.accuracy = r.nextDouble() * 100;
        a.cunning = r.nextDouble() * 100;
        a.skill = r.nextDouble() * 100;
        a.risk = r.nextDouble() * 100;
        a.endurance = r.nextDouble() * 100;
        a.dexterity = r.nextDouble() * 100;

        return a;
    }

    public static Knight getPowerfulPerson() {
        Knight a = new Knight();
        a.name = "Мр.";
        a.surname = "Большой";
        a.country = Country.CONJUCTION;
        a.nation = Nation.ALMAGEST;

        a.speed = 50;
        a.acceleration = 50;
        a.hitPower = 100;
        a.shotRange = 100;
        a.accuracy = 50;
        a.cunning = 50;
        a.skill = 50;
        a.risk = 50;
        a.endurance = 100;
        a.dexterity = 50;

        return a;
    }

    public static Knight getAthleticPerson() {
        Knight a = new Knight();
        a.name = "Мр.";
        a.surname = "Быстрый";
        a.country = Country.CONJUCTION;
        a.nation = Nation.ALMAGEST;

        a.speed = 100;
        a.acceleration = 100;
        a.hitPower = 50;
        a.shotRange = 50;
        a.accuracy = 50;
        a.cunning = 50;
        a.skill = 50;
        a.risk = 50;
        a.endurance = 50;
        a.dexterity = 100;

        return a;
    }

    public static Knight getSmartPerson() {
        Knight a = new Knight();
        a.name = "Мр.";
        a.surname = "Умный";
        a.country = Country.CONJUCTION;
        a.nation = Nation.ALMAGEST;

        a.speed = 50;
        a.acceleration = 50;
        a.hitPower = 50;
        a.shotRange = 50;
        a.accuracy = 100;
        a.cunning = 100;
        a.skill = 100;
        a.risk = 50;
        a.endurance = 50;
        a.dexterity = 50;

        return a;
    }

    public static Knight getAveragePerson() {
        Knight a = new Knight();
        a.name = "Мр.";
        a.surname = "Средний";
        a.country = Country.CONJUCTION;
        a.nation = Nation.ALMAGEST;

        a.speed = 66;
        a.acceleration = 66;
        a.hitPower = 66;
        a.shotRange = 66;
        a.accuracy = 66;
        a.cunning = 66;
        a.skill = 66;
        a.risk = 50;
        a.endurance = 66;
        a.dexterity = 66;

        return a;
    }

    public void setRandomSkills() {
        java.util.Random r = new java.util.Random(System.currentTimeMillis());
        speed = r.nextDouble() * 100;
        acceleration = r.nextDouble() * 100;
        hitPower = r.nextDouble() * 100;
        shotRange = r.nextDouble() * 100;
        accuracy = r.nextDouble() * 100;
        cunning = r.nextDouble() * 100;
        skill = r.nextDouble() * 100;
        risk = r.nextDouble() * 100;
        endurance = r.nextDouble() * 100;
        dexterity = r.nextDouble() * 100;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Nation getNation() {
        return nation;
    }

    public Country getCountry() {
        return country;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getHitPower() {
        return hitPower;
    }

    public double getShotRange() {
        return shotRange;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getCunning() {
        return cunning;
    }

    public double getSkill() {
        return skill;
    }

    public double getRisk() {
        return risk;
    }

    public double getEndurance() {
        return endurance;
    }

    public double getDexterity() {
        return dexterity;
    }
}

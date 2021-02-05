package tm.lib.domain.core.dto;

import tm.lib.domain.core.Country;
import tm.lib.domain.core.Nation;

public class KnightDto {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getHitPower() {
        return hitPower;
    }

    public void setHitPower(double hitPower) {
        this.hitPower = hitPower;
    }

    public double getShotRange() {
        return shotRange;
    }

    public void setShotRange(double shotRange) {
        this.shotRange = shotRange;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getCunning() {
        return cunning;
    }

    public void setCunning(double cunning) {
        this.cunning = cunning;
    }

    public double getSkill() {
        return skill;
    }

    public void setSkill(double skill) {
        this.skill = skill;
    }

    public double getRisk() {
        return risk;
    }

    public void setRisk(double risk) {
        this.risk = risk;
    }

    public double getEndurance() {
        return endurance;
    }

    public void setEndurance(double endurance) {
        this.endurance = endurance;
    }

    public double getDexterity() {
        return dexterity;
    }

    public void setDexterity(double dexterity) {
        this.dexterity = dexterity;
    }
}

package tm.lib.domain.core;

import org.w3c.dom.Element;

public class Person
{
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

    public Person()
    {
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

    public String getFullName()
    {
        return getName() + " " + getSurname();
    }

    public String getShortName()
    {
        return getName().charAt(0) + ". " + getSurname();
    }

    public static Person getTestPerson1()
    {
        Person a = new Person();
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

    public static Person getTestPerson2()
    {
        Person b = new Person();
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

    public static Person getRandomPerson()
    {
        Person a = new Person();
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

    public static Person getPowerfulPerson()
    {
        Person a = new Person();
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

    public static Person getAthleticPerson()
    {
        Person a = new Person();
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

    public static Person getSmartPerson()
    {
        Person a = new Person();
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

    public static Person getAveragePerson()
    {
        Person a = new Person();
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

    public String getName()
    {
        return name;
    }

    public String getSurname()
    {
        return surname;
    }

    public Nation getNation()
    {
        return nation;
    }

    public Country getCountry()
    {
        return country;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getAcceleration()
    {
        return acceleration;
    }

    public double getHitPower()
    {
        return hitPower;
    }

    public double getShotRange()
    {
        return shotRange;
    }

    public double getAccuracy()
    {
        return accuracy;
    }

    public double getCunning()
    {
        return cunning;
    }

    public double getSkill()
    {
        return skill;
    }

    public double getRisk()
    {
        return risk;
    }

    public double getEndurance()
    {
        return endurance;
    }

    public double getDexterity()
    {
        return dexterity;
    }

    public void init(Element playerElement)
    {
        String fullName = playerElement.getAttribute("name");
        int spaceIndex = fullName.indexOf(' ');
        name = fullName.substring(0, spaceIndex);
        surname = fullName.substring(spaceIndex + 1);

        nation = Nation.fromString(playerElement.getAttribute("nation"));
        country = Country.fromString(playerElement.getAttribute("country"));

        speed = Integer.parseInt(playerElement.getAttribute("spe"));
        acceleration = Integer.parseInt(playerElement.getAttribute("acc"));
        hitPower = Integer.parseInt(playerElement.getAttribute("pow"));
        shotRange = Integer.parseInt(playerElement.getAttribute("rng"));
        accuracy = Integer.parseInt(playerElement.getAttribute("acu"));
        cunning = Integer.parseInt(playerElement.getAttribute("cun"));
        skill = Integer.parseInt(playerElement.getAttribute("skl"));
        risk = Integer.parseInt(playerElement.getAttribute("rsk"));
        endurance = Integer.parseInt(playerElement.getAttribute("end"));
        dexterity = Integer.parseInt(playerElement.getAttribute("dex"));
    }
}

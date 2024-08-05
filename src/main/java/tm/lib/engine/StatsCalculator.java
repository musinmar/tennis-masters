package tm.lib.engine;

import tm.lib.domain.core.Stadium;

import static tm.lib.engine.MatchEngineConstants.*;

public class StatsCalculator {

    private final Stadium venue;

    public StatsCalculator(Stadium venue) {
        this.venue = venue;
    }

    private Stadium getVenue() {
        return venue;
    }

    static double map(double value, double min, double max) {
        return value / 100 * (max - min) + min;
    }

    private double applyEnergyModifier(Player p, double value, double modifier) {
        value *= (1 - modifier) * p.getEnergy() / 100 + modifier;
        return value;
    }

    static double getInvertedScaleModifier(double scaleFactor, double modifier) {
        return 1 + (1 - scaleFactor / 100) * modifier;
    }

    static double applyInvertedEnergyModifier(Player p, double value, double modifier) {
        return value * getInvertedScaleModifier(p.getEnergy(), modifier);
    }

    public double getVenueSpeedModifier() {
        return map(getVenue().getRoughness(), MIN_VENUE_SPEED_MODIFIER, MAX_VENUE_SPEED_MODIFIER);
    }

    private double getVenueAccelerationModifier() {
        return map(100 - getVenue().getSlippery(), MIN_VENUE_ACC_MODIFIER, MAX_VENUE_ACC_MODIFIER);
    }

    public double getNetZone() {
        return map(getVenue().getNetHeight(), MIN_NET_ZONE_LENGTH, MAX_NET_ZONE_LENGTH);
    }

    public double getActualPlayerSpeed(Player p) {
        //double speed = p.person.speed / 100 * (PLAYER_MAX_SPEED - PLAYER_MIN_SPEED) + PLAYER_MIN_SPEED;
        //speed *= (1 - SPEED_ENERGY_MODIFIER) * p.energy / 100 + SPEED_ENERGY_MODIFIER;
        double speed = map(p.getKnight().getSkills().getSpeed(), PLAYER_MIN_SPEED, PLAYER_MAX_SPEED);
        speed = applyEnergyModifier(p, speed, SPEED_ENERGY_MODIFIER);
        speed = speed * getVenueSpeedModifier();
        return speed;
    }

    public double getActualPlayerAcceleration(Player p) {
        double acc = map(p.getKnight().getSkills().getAcceleration(), PLAYER_MIN_ACCELERATION, PLAYER_MAX_ACCELERATION);
        acc = applyEnergyModifier(p, acc, ACCELERATION_ENERGY_MODIFIER);
        acc = acc * getVenueAccelerationModifier();
        return acc;
    }

    public double getActualBallSpeed(Player p) {
        //double ball_speed = p.person.hit_power / 100 * (BALL_MAX_SPEED - BALL_MIN_SPEED) + BALL_MIN_SPEED;
        //ball_speed *= (1 - BALL_SPEED_ENERGY_MODIFIER) * p.energy / 100 + BALL_SPEED_ENERGY_MODIFIER;
        double ball_speed = map(p.getKnight().getSkills().getHitPower(), BALL_MIN_SPEED, BALL_MAX_SPEED);
        ball_speed = applyEnergyModifier(p, ball_speed, BALL_SPEED_ENERGY_MODIFIER);
        return ball_speed;
    }

    public double getActualShotRange(Player p) {
        double shot_range = map(p.getKnight().getSkills().getShotRange(), SHOT_MIN_RANGE, SHOT_MAX_RANGE);
        shot_range = applyEnergyModifier(p, shot_range, SHOT_RANGE_ENERGY_MODIFIER);
        return shot_range;
    }

    public double getActualTargetRange(Player p) {
        double target_range = map(100 - p.getKnight().getSkills().getAccuracy(), TARGET_MIN_RANGE, TARGET_MAX_RANGE);
        target_range = applyInvertedEnergyModifier(p, target_range, TARGET_RANGE_ENERGY_MODIFIER);
        return target_range;
    }

    public double getActualVisibleTargetRange(Player p) {
        double visibleTargetRange = map(p.getKnight().getSkills().getCunning(), 0, VISIBLE_TARGET_MAX_RANGE);
        visibleTargetRange = applyEnergyModifier(p, visibleTargetRange, VISIBLE_TARGET_ENERGY_MODIFIER);
        return visibleTargetRange;
    }

    public double getActualSkillRange(Player p) {
        double intelligenceRange = map(p.getKnight().getSkills().getIntelligence(), 0, SKILL_MAX_RANGE);
        intelligenceRange = applyEnergyModifier(p, intelligenceRange, SKILL_RANGE_ENERGY_MODIFIER);
        return intelligenceRange;
    }

    public double getActualRiskMargin(Player p) {
        double risk_margin = map(100 - p.getKnight().getSkills().getRisk(), 0, MAX_RISK_MARGIN);
        return risk_margin;
    }

    public double getActualSaveAddDistance(Player p) {
        double save_add_distance = map(p.getKnight().getSkills().getDexterity(), SAVE_MIN_ADD_DISTANCE, SAVE_MAX_ADD_DISTANCE);
        save_add_distance = applyEnergyModifier(p, save_add_distance, SAVE_ADD_DISTANCE_ENERGY_MODIFIER);
        return save_add_distance;
    }

    public double getTotalLyingTime(Player p) {
        double lyingTime = map(100 - p.getKnight().getSkills().getDexterity(), MIN_LYING_TIME, MAX_LYING_TIME);
        return lyingTime * getInvertedScaleModifier(p.getEnergy(), LYING_TIME_ENERGY_MODIFIER);
    }

    public double getEnergyDecreaseModifier(Player p) {
        double energyDecreaseModifier = map(100 - p.getKnight().getSkills().getEndurance(), ENERGY_DECREASE_MIN_MODIFIER, ENERGY_DECREASE_MAX_MODIFIER);
        return energyDecreaseModifier;
    }
}

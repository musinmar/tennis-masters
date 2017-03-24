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

    private double applyValueMargins(double base, double min_value, double max_value) {
        return base / 100 * (max_value - min_value) + min_value;
    }

    private double applyEnergyModifier(Player p, double value, double modifier) {
        value *= (1 - modifier) * p.getEnergy() / 100 + modifier;
        return value;
    }

    private double applyInvertedEnergyModifier(Player p, double value, double modifier) {
        value *= 1 + modifier - (value / 100) * modifier;
        return value;
    }

    public double getVenueSpeedModifier() {
        return applyValueMargins(getVenue().roughness, MIN_VENUE_SPEED_MODIFIER, MAX_VENUE_SPEED_MODIFIER);
    }

    private double getVenueAccelerationModifier() {
        return applyValueMargins(100 - getVenue().slippery, MIN_VENUE_ACC_MODIFIER, MAX_VENUE_ACC_MODIFIER);
    }

    public double getNetZone() {
        return applyValueMargins(getVenue().net_height, MIN_NET_ZONE_LENGTH, MAX_NET_ZONE_LENGTH);
    }

    public double getActualPlayerSpeed(Player p) {
        //double speed = p.person.speed / 100 * (PLAYER_MAX_SPEED - PLAYER_MIN_SPEED) + PLAYER_MIN_SPEED;
        //speed *= (1 - SPEED_ENERGY_MODIFIER) * p.energy / 100 + SPEED_ENERGY_MODIFIER;
        double speed = applyValueMargins(p.getPerson().getSpeed(), PLAYER_MIN_SPEED, PLAYER_MAX_SPEED);
        speed = applyEnergyModifier(p, speed, SPEED_ENERGY_MODIFIER);
        speed = speed * getVenueSpeedModifier();
        return speed;
    }

    public double getActualPlayerAcceleration(Player p) {
        double acc = applyValueMargins(p.getPerson().getAcceleration(), PLAYER_MIN_ACCELERATION, PLAYER_MAX_ACCELERATION);
        acc = applyEnergyModifier(p, acc, ACCELERATION_ENERGY_MODIFIER);
        acc = acc * getVenueAccelerationModifier();
        return acc;
    }

    public double getActualBallSpeed(Player p) {
        //double ball_speed = p.person.hit_power / 100 * (BALL_MAX_SPEED - BALL_MIN_SPEED) + BALL_MIN_SPEED;
        //ball_speed *= (1 - BALL_SPEED_ENERGY_MODIFIER) * p.energy / 100 + BALL_SPEED_ENERGY_MODIFIER;
        double ball_speed = applyValueMargins(p.getPerson().getHitPower(), BALL_MIN_SPEED, BALL_MAX_SPEED);
        ball_speed = applyEnergyModifier(p, ball_speed, BALL_SPEED_ENERGY_MODIFIER);
        return ball_speed;
    }

    public double getActualShotRange(Player p) {
        double shot_range = applyValueMargins(p.getPerson().getShotRange(), SHOT_MIN_RANGE, SHOT_MAX_RANGE);
        shot_range = applyEnergyModifier(p, shot_range, SHOT_RANGE_ENERGY_MODIFIER);
        return shot_range;
    }

    public double getActualTargetRange(Player p) {
        double target_range = applyValueMargins(100 - p.getPerson().getAccuracy(), TARGET_MIN_RANGE, TARGET_MAX_RANGE);
        target_range = applyInvertedEnergyModifier(p, target_range, TARGET_RANGE_ENERGY_MODIFIER);
        return target_range;
    }

    public double getActualVisibleTargetRange(Player p) {
        double visibleTargetRange = applyValueMargins(p.getPerson().getCunning(), 0, VISIBLE_TARGET_MAX_RANGE);
        visibleTargetRange = applyEnergyModifier(p, visibleTargetRange, VISIBLE_TARGET_ENERGY_MODIFIER);
        return visibleTargetRange;
    }

    public double getActualSkillRange(Player p) {
        double skill_range = applyValueMargins(p.getPerson().getSkill(), 0, SKILL_MAX_RANGE);
        skill_range = applyEnergyModifier(p, skill_range, SKILL_RANGE_ENERGY_MODIFIER);
        return skill_range;
    }

    public double getActualRiskMargin(Player p) {
        double risk_margin = applyValueMargins(100 - p.getPerson().getRisk(), 0, MAX_RISK_MARGIN);
        return risk_margin;
    }

    public double getActualSaveAddDistance(Player p) {
        double save_add_distance = applyValueMargins(p.getPerson().getDexterity(), SAVE_MIN_ADD_DISTANCE, SAVE_MAX_ADD_DISTANCE);
        save_add_distance = applyEnergyModifier(p, save_add_distance, SAVE_ADD_DISTANCE_ENERGY_MODIFIER);
        return save_add_distance;
    }

    public double getActualMaxLyingTime(Player p) {
        double lying_time = applyValueMargins(100 - p.getPerson().getDexterity(), MIN_LYING_TIME, MAX_LYING_TIME);
        lying_time = applyInvertedEnergyModifier(p, lying_time, LYING_TIME_ENERGY_MODIFIER);
        return lying_time;
    }

    public void decreasePlayerEnergy(Player p, double value) {
        double energy_decrease_modifier = applyValueMargins(100 - p.getPerson().getEndurance(), ENERGY_DECREASE_MIN_MODIFIER, ENERGY_DECREASE_MAX_MODIFIER);
        value = value * energy_decrease_modifier;
        p.changeEnergy(-value);
    }
}

package tm.lib.engine;

public class MatchEngineConstants {

    /**
     * Max distance at which player can perform actions - mainly hit the ball.
     */
    public static final double PLAYER_HAND_LENGTH = 15;

    public static final double PLAYER_MAX_SPEED = Pitch.WIDTH / 2;
    public static final double PLAYER_MIN_SPEED = Pitch.WIDTH / 3.5;
    public static final double SPEED_ENERGY_MODIFIER = 0.5;
    public static final double PLAYER_MAX_ACCELERATION = PLAYER_MAX_SPEED / 3;
    public static final double PLAYER_MIN_ACCELERATION = PLAYER_MIN_SPEED / 8;
    public static final double ACCELERATION_ENERGY_MODIFIER = 0.5;
    public static final double BALL_MAX_SPEED = Pitch.WIDTH / 1;
    public static final double BALL_MIN_SPEED = Pitch.WIDTH / 1.8;
    public static final double BALL_SPEED_ENERGY_MODIFIER = 0.7;
    public static final double SHOT_MAX_RANGE = Pitch.HEIGHT * 8 / 6;
    public static final double SHOT_MIN_RANGE = Pitch.HEIGHT * 4 / 8;
    public static final double SHOT_RANGE_ENERGY_MODIFIER = 0.7;
    public static final double TARGET_MAX_RANGE = Pitch.WIDTH / 8;
    public static final double TARGET_MIN_RANGE = Pitch.WIDTH / 16;
    public static final double TARGET_RANGE_ENERGY_MODIFIER = 0.6;
    public static final double VISIBLE_TARGET_MAX_RANGE = Pitch.WIDTH / 3;
    public static final double VISIBLE_TARGET_ENERGY_MODIFIER = 0.5;
    public static final double SKILL_MAX_RANGE = Pitch.WIDTH * 5 / 20;
    public static final double SKILL_RANGE_ENERGY_MODIFIER = 0.8;
    public static final double MAX_RISK_MARGIN = Pitch.WIDTH / 10;
    public static final double ENERGY_DECREASE_MAX_MODIFIER = 1.2;
    public static final double ENERGY_DECREASE_MIN_MODIFIER = 0.8;
    public static final double SAVE_MAX_ADD_DISTANCE = PLAYER_HAND_LENGTH * 2;
    public static final double SAVE_MIN_ADD_DISTANCE = PLAYER_HAND_LENGTH * 1;
    public static final double SAVE_ADD_DISTANCE_ENERGY_MODIFIER = 0.5;
    public static final double MAX_LYING_TIME = 1.2;
    public static final double MIN_LYING_TIME = 0.6;
    public static final double LYING_TIME_ENERGY_MODIFIER = 1;
    public static final double MAX_VENUE_SPEED_MODIFIER = 1.25;
    public static final double MIN_VENUE_SPEED_MODIFIER = 0.75;
    public static final double MAX_VENUE_ACC_MODIFIER = 1.40;
    public static final double MIN_VENUE_ACC_MODIFIER = 0.60;
    public static final double MAX_NET_ZONE_LENGTH = Pitch.HALF_HEIGHT * 3 / 10;
    public static final double MIN_NET_ZONE_LENGTH = Pitch.HALF_HEIGHT * 3 / 20;
}

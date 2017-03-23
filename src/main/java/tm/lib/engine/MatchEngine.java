package tm.lib.engine;

import java.util.Random;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.domain.competition.Match;

public class MatchEngine {

    private Pitch pitch;
    private Side winningSide;
    private Player lastHittedPlayer;
    private boolean net_hitted;

    private static Random random = new Random(System.currentTimeMillis());
    public static final double TIME_STEP = 0.02;
    private static final double PLAYER_HAND_LENGTH = 15;
    private static final double PLAYER_MAX_SPEED = Pitch.WIDTH / 2;
    private static final double PLAYER_MIN_SPEED = Pitch.WIDTH / 3.5;
    private static final double SPEED_ENERGY_MODIFIER = 0.5;
    private static final double PLAYER_MAX_ACCELERATION = PLAYER_MAX_SPEED / 3;
    private static final double PLAYER_MIN_ACCELERATION = PLAYER_MIN_SPEED / 8;
    private static final double ACCELERATION_ENERGY_MODIFIER = 0.5;
    private static final double BALL_MAX_SPEED = Pitch.WIDTH / 1;
    private static final double BALL_MIN_SPEED = Pitch.WIDTH / 1.8;
    private static final double BALL_SPEED_ENERGY_MODIFIER = 0.7;
    private static final double SHOT_MAX_RANGE = Pitch.HEIGHT * 8 / 6;
    private static final double SHOT_MIN_RANGE = Pitch.HEIGHT * 4 / 8;
    private static final double SHOT_RANGE_ENERGY_MODIFIER = 0.7;
    private static final double TARGET_MAX_RANGE = Pitch.WIDTH / 8;
    private static final double TARGET_MIN_RANGE = Pitch.WIDTH / 16;
    private static final double TARGET_RANGE_ENERGY_MODIFIER = 0.6;
    private static final double FAKE_TARGET_MAX_RANGE = Pitch.WIDTH / 3;
    private static final double FAKE_TARGET_ENERGY_MODIFIER = 0.5;
    private static final double SKILL_MAX_RANGE = Pitch.WIDTH * 5 / 20;
    private static final double SKILL_RANGE_ENERGY_MODIFIER = 0.8;
    private static final double MAX_RISK_MARGIN = Pitch.WIDTH / 10;
    private static final double ENERGY_DECREASE_MAX_MODIFIER = 1.2;
    private static final double ENERGY_DECREASE_MIN_MODIFIER = 0.8;
    private static final double SAVE_MAX_ADD_DISTANCE = PLAYER_HAND_LENGTH * 2;
    private static final double SAVE_MIN_ADD_DISTANCE = PLAYER_HAND_LENGTH * 1;
    private static final double SAVE_ADD_DISTANCE_ENERGY_MODIFIER = 0.5;
    private static final double MAX_LYING_TIME = 1.2;
    private static final double MIN_LYING_TIME = 0.6;
    private static final double LYING_TIME_ENERGY_MODIFIER = 1;
    private static final double MAX_VENUE_SPEED_MODIFIER = 1.25;
    private static final double MIN_VENUE_SPEED_MODIFIER = 0.75;
    private static final double MAX_VENUE_ACC_MODIFIER = 1.40;
    private static final double MIN_VENUE_ACC_MODIFIER = 0.60;
    private static final double MAX_NET_ZONE_LENGTH = Pitch.HALF_HEIGHT * 3 / 10;
    private static final double MIN_NET_ZONE_LENGTH = Pitch.HALF_HEIGHT * 3 / 20;
    //private static final double NET_ZONE_MAX_LENGTH = Pitch.HHEIGHT / 5;
    private static final double NET_PONG = Pitch.HALF_HEIGHT / 35;
    private static final double ENERGY_LOSS_PER_DISTANCE = 0.9 / Pitch.WIDTH;
    private static final double ENERGY_LOSS_PER_HIT = 0.3;
    private static final double ENERGY_LOSS_PER_SAVE = 0.3;
    private static final double ENERGY_REGAIN_PER_GAME = 1;
    private static final double ENERGY_REGAIN_PER_SET = 3;

    public MatchEngine(Match match) {
        pitch = new Pitch(match.getFirstPlayer(), match.getSecondPlayer(), match.getVenue());
        pitch.setInitialPositions(Side.HOME);
        lastHittedPlayer = null;
    }

    public void reset(Side startingSide) {
        getPitch().setInitialPositions(startingSide);
        winningSide = null;
    }

    public void next() {
        performPlayerAction(getPitch().getPlayer(Side.HOME));
        performPlayerAction(getPitch().getPlayer(Side.AWAY));
        performBallAction(getPitch().getBall());
    }

    public Pitch getPitch() {
        return pitch;
    }

    public Side getWinningSide() {
        return winningSide;
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

    private double getVenueSpeedModifier() {
        return applyValueMargins(getPitch().getVenue().roughness, MIN_VENUE_SPEED_MODIFIER, MAX_VENUE_SPEED_MODIFIER);
    }

    private double getActualPlayerSpeed(Player p) {
        //double speed = p.person.speed / 100 * (PLAYER_MAX_SPEED - PLAYER_MIN_SPEED) + PLAYER_MIN_SPEED;
        //speed *= (1 - SPEED_ENERGY_MODIFIER) * p.energy / 100 + SPEED_ENERGY_MODIFIER;
        double speed = applyValueMargins(p.getPerson().getSpeed(), PLAYER_MIN_SPEED, PLAYER_MAX_SPEED);
        speed = applyEnergyModifier(p, speed, SPEED_ENERGY_MODIFIER);
        speed = speed * getVenueSpeedModifier();
        return speed;
    }

    private double getVenueAccelerationModifier() {
        return applyValueMargins(100 - getPitch().getVenue().slippery, MIN_VENUE_ACC_MODIFIER, MAX_VENUE_ACC_MODIFIER);
    }

    private double getActualPlayerAcceleration(Player p) {
        double acc = applyValueMargins(p.getPerson().getAcceleration(), PLAYER_MIN_ACCELERATION, PLAYER_MAX_ACCELERATION);
        acc = applyEnergyModifier(p, acc, ACCELERATION_ENERGY_MODIFIER);
        acc = acc * getVenueAccelerationModifier();
        return acc;
    }

    private double getActualBallSpeed(Player p) {
        //double ball_speed = p.person.hit_power / 100 * (BALL_MAX_SPEED - BALL_MIN_SPEED) + BALL_MIN_SPEED;
        //ball_speed *= (1 - BALL_SPEED_ENERGY_MODIFIER) * p.energy / 100 + BALL_SPEED_ENERGY_MODIFIER;
        double ball_speed = applyValueMargins(p.getPerson().getHitPower(), BALL_MIN_SPEED, BALL_MAX_SPEED);
        ball_speed = applyEnergyModifier(p, ball_speed, BALL_SPEED_ENERGY_MODIFIER);
        return ball_speed;
    }

    private double getActualShotRange(Player p) {
        double shot_range = applyValueMargins(p.getPerson().getShotRange(), SHOT_MIN_RANGE, SHOT_MAX_RANGE);
        shot_range = applyEnergyModifier(p, shot_range, SHOT_RANGE_ENERGY_MODIFIER);
        return shot_range;
    }

    private double getActualTargetRange(Player p) {
        double target_range = applyValueMargins(100 - p.getPerson().getAccuracy(), TARGET_MIN_RANGE, TARGET_MAX_RANGE);
        target_range = applyInvertedEnergyModifier(p, target_range, TARGET_RANGE_ENERGY_MODIFIER);
        return target_range;
    }

    private double getActualFakeTargetRange(Player p) {
        double fake_target_range = applyValueMargins(p.getPerson().getCunning(), 0, FAKE_TARGET_MAX_RANGE);
        fake_target_range = applyEnergyModifier(p, fake_target_range, FAKE_TARGET_ENERGY_MODIFIER);
        return fake_target_range;
    }

    private double getActualSkillRange(Player p) {
        double skill_range = applyValueMargins(p.getPerson().getSkill(), 0, SKILL_MAX_RANGE);
        skill_range = applyEnergyModifier(p, skill_range, SKILL_RANGE_ENERGY_MODIFIER);
        return skill_range;
    }

    private double getActualRiskMargin(Player p) {
        double risk_margin = applyValueMargins(100 - p.getPerson().getRisk(), 0, MAX_RISK_MARGIN);
        return risk_margin;
    }

    private double getActualSaveAddDistance(Player p) {
        double save_add_distance = applyValueMargins(p.getPerson().getDexterity(), SAVE_MIN_ADD_DISTANCE, SAVE_MAX_ADD_DISTANCE);
        save_add_distance = applyEnergyModifier(p, save_add_distance, SAVE_ADD_DISTANCE_ENERGY_MODIFIER);
        return save_add_distance;
    }

    private double getActualMaxLyingTime(Player p) {
        double lying_time = applyValueMargins(100 - p.getPerson().getDexterity(), MIN_LYING_TIME, MAX_LYING_TIME);
        lying_time = applyInvertedEnergyModifier(p, lying_time, LYING_TIME_ENERGY_MODIFIER);
        return lying_time;
    }

    private void decrasePlayerEnergy(Player p, double value) {
        double energy_decrease_modifier = applyValueMargins(100 - p.getPerson().getEndurance(), ENERGY_DECREASE_MIN_MODIFIER, ENERGY_DECREASE_MAX_MODIFIER);
        value = value * energy_decrease_modifier;
        p.changeEnergy(-value);
    }

    private double getNetZone() {
        return applyValueMargins(getPitch().getVenue().net_height, MIN_NET_ZONE_LENGTH, MAX_NET_ZONE_LENGTH);
    }

    private boolean isPlayerZoneTargeted(Player p) {
        Vector2D fakeTarget = getPitch().getBall().getFakeTarget();
        if (fakeTarget.getX() < 0 || fakeTarget.getX() > Pitch.WIDTH
                || fakeTarget.getY() > Pitch.HALF_HEIGHT || fakeTarget.getY() < -Pitch.HALF_HEIGHT) {
            return false;
        }
        boolean fp_target = (fakeTarget.getY() >= 0);
        if ((p.getSide() == Side.HOME && fp_target) || (p.getSide() == Side.AWAY && !fp_target)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canHitBall(Player p) {
        Ball ball = getPitch().getBall();
        if (p.getPosition().distance(ball.getPosition()) <= PLAYER_HAND_LENGTH) {
            return true;
        } else {
            return false;
        }
    }

    private double getPlayerModifier(Player p) {
        return p.getSide().getModifier();
    }

    /*private DPoint player_standard_position(Player p) {
     if (p.id == 1) 
     return new DPoint(Pitch.WIDTH / 2, Pitch.HHEIGHT / 2);
     else 
     return new DPoint(Pitch.WIDTH / 2, - Pitch.HHEIGHT / 2);
     }*/
    private boolean isTargetSmartEnough(Player p, Vector2D target) {
        Player opposite = getPitch().getOppositePlayer(p);
        double dist = target.subtract(opposite.getPosition()).getNorm();
        double player_no_hit_range = getActualSkillRange(p);
        if (dist >= player_no_hit_range) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isTargetHighEnough(Player p, Vector2D target) {
        double net_zone_length = Math.abs(p.getPosition().getY()) / Pitch.HALF_HEIGHT * getNetZone();
        double mod = getPlayerModifier(p);
        if (target.getY() * mod > 0) {
            return true;
        }

        if (Math.abs(target.getY()) > net_zone_length) {
            return true;
        } else {
            return false;
        }
    }

    private void hasBallHittedNet(Player p, Vector2D target) {
        double mod = getPlayerModifier(p);
        Vector2D d = target.subtract(p.getPosition());
        double k = Math.abs(p.getPosition().getY() / (target.getY() - p.getPosition().getY()));
        d = d.scalarMultiply(k);
        Ball ball = getPitch().getBall();
        ball.setRealTarget(p.getPosition().add(d).add(new Vector2D(0, mod * NET_PONG)));
        ball.setFakeTarget(target);
        net_hitted = true;
    }

    private Vector2D applyShotDistanceModification(Player p, Vector2D target) {
        Vector2D d = target.subtract(p.getPosition());
        double dist = d.getNorm();

        double optimal_shot_distance = getActualShotRange(p);
        if (dist > optimal_shot_distance) {
            double excess = dist - optimal_shot_distance;
            double r = Math.pow(random.nextDouble(), 2);
            d = d.scalarMultiply((optimal_shot_distance + excess * r) / (optimal_shot_distance + excess));
            target = d.add(p.getPosition());
        }
        
        return target;
    }

    private void getNewBallTarget(Player p, Ball b) {
        double mod = getPlayerModifier(p);

        Vector2D target = Vector2D.ZERO;
        boolean found = false;

        double net_zone_length = Math.abs(p.getPosition().getY()) / Pitch.HALF_HEIGHT * getNetZone();
        double risk_margin = getActualRiskMargin(p);
        while (!found) {
            double x = random.nextDouble() * (Pitch.WIDTH - 2 * risk_margin) + risk_margin;
            double y = -mod * (random.nextDouble() * (Pitch.HALF_HEIGHT - net_zone_length - 2 * risk_margin) + net_zone_length + risk_margin);
            target = new Vector2D(x, y);
            //target.y = -mod * random.nextDouble() * Pitch.HHEIGHT;

            if (isTargetSmartEnough(p, target)) {
                found = true;
            }
        }

        target = applyShotDistanceModification(p, target);

        double target_range = getActualTargetRange(p);
        double phi = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * target_range;
        double x = target.getX() + Math.cos(phi) * r;
        double y = target.getY() + Math.sin(phi) * r;
        target = new Vector2D(x, y);

        if (!isTargetHighEnough(p, target)) {
            hasBallHittedNet(p, target);
        } else {
            b.setRealTarget(target);
        }
    }

    private void getNewFakeBallTarget(Player p, Ball b) {
        double fake_target_range = getActualFakeTargetRange(p);
        double phi = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * fake_target_range;
        double target_x = b.getRealTarget().getX() + Math.cos(phi) * r;
        double target_y = b.getRealTarget().getY() + Math.sin(phi) * r;
        b.setFakeTarget(new Vector2D(target_x, target_y));
    }

    private void hitBall(Player p) {
        Ball ball = getPitch().getBall();
        getNewBallTarget(p, ball);
        ball.setSpeed(getActualBallSpeed(p));
        if (net_hitted) {
            net_hitted = false;
        } else {
            getNewFakeBallTarget(p, ball);
        }
        lastHittedPlayer = p;
        decrasePlayerEnergy(p, ENERGY_LOSS_PER_HIT);
    }

    private boolean getZoneHitThreat(Player p) {
        double mod = getPlayerModifier(p);
        if (mod * getPitch().getBall().getFakeTarget().getY() > -Pitch.HALF_HEIGHT / 6) {
            return true;
        } else {
            return false;
        }
    }

    private Vector2D getPlayerOptimalPosition(Player p) {
        Player opp = getPitch().getOppositePlayer(p);
        double net_zone_length = Math.abs(opp.getPosition().getY()) / Pitch.HALF_HEIGHT * getNetZone();
        if (net_zone_length < 0) {
            net_zone_length = 0;
        }
        double mod = getPlayerModifier(p);
        double optimal_x = Pitch.WIDTH / 2;
        double optimal_y = mod * ((Pitch.HALF_HEIGHT - net_zone_length) / 2 + net_zone_length);
        return new Vector2D(optimal_x, optimal_y);
    }

    private void movePlayerToTarget(Player p, Vector2D target) {
        double speed = getActualPlayerSpeed(p);
        double acc = getActualPlayerAcceleration(p);
        double step = speed * TIME_STEP;
        double ac_step = acc * TIME_STEP;

        Vector2D v = p.getDirection().scalarMultiply(p.getSpeed() * TIME_STEP);
        Vector2D d = target.subtract(p.getPosition());
        double dd = d.getNorm();

        if (dd > step) {//PLAYER_SPEED) {
            d = d.scalarMultiply(1 / dd).scalarMultiply(step);
        }
        Vector2D dv = d.subtract(v);
        double dv_len = dv.getNorm();

        if (dv_len > ac_step) {//PLAYER_ACCELERATION) {
            dv = dv.scalarMultiply(1 / dv_len).scalarMultiply(ac_step);
        }

        v = v.add(dv);
        p.setSpeed(v.getNorm() / TIME_STEP);
        if (p.getSpeed() != 0) {
            p.setDirection(v.scalarMultiply(1 / (p.getSpeed() * TIME_STEP)));
        }

        Vector2D move = p.getDirection().scalarMultiply(p.getSpeed() * TIME_STEP);
        p.setPosition(p.getPosition().add(move));
        decrasePlayerEnergy(p, move.getNorm() / getVenueSpeedModifier() * ENERGY_LOSS_PER_DISTANCE);
    }

    private void movePlayer(Player p) {
        Vector2D target;
        //if (player_zone_targeted(p)) 
        if (getZoneHitThreat(p)) {
            target = getPitch().getBall().getFakeTarget();
            double mod = getPlayerModifier(p);
            double x = target.getX();
            double y = target.getY();
            if (y * mod < 0) {
                y = 0;
            }
            if (x < 0) {
                x = 0;
            }
            if (x > Pitch.WIDTH) {
                x = Pitch.WIDTH;
            }
            target = new Vector2D(x, y);
            /*if (target.y > mod * Pitch.HHEIGHT) {
             target.y 
             }*/
        } else //target = player_standard_position(p);
        {
            target = getPlayerOptimalPosition(p);
        }

        movePlayerToTarget(p, target);
    }

    private void performLyingAction(Player p) {
        movePlayerToTarget(p, p.getPosition());
        p.addLyingTime(TIME_STEP);
        if (p.getLyingTime() >= getActualMaxLyingTime(p)) {
            p.setLying(false);
        }
    }

    private void performPlayerAction(Player p) {
        if (p.isLying()) {
            performLyingAction(p);
        } else if (isPlayerZoneTargeted(p) && canHitBall(p)) {
            hitBall(p);
        } else {
            movePlayer(p);
        }
    }

    private boolean hasBallHittedGround(Ball b) {
        if (b.getRealTarget().subtract(b.getPosition()).getNorm() < 0.001) {
            return true;
        } else {
            return false;
        }
    }

    private void moveBall(Ball b) {
        Vector2D d = b.getFakeTarget().subtract(b.getPosition());
        double dd = d.getNorm();
        double dist_to_target = b.getRealTarget().subtract(b.getPosition()).getNorm();
        double step = TIME_STEP * b.getSpeed();
        double m_step = step * dd / dist_to_target;
        if (dd > m_step) {
            d = d.scalarMultiply(1 / dd).scalarMultiply(m_step);
        }
        b.setPosition(b.getPosition().add(d));

        int steps = (int) (dist_to_target / step);
        if (steps == 0) {
            b.setFakeTarget(b.getRealTarget());
        } else {
            Vector2D fake_target_d = b.getRealTarget().subtract(b.getFakeTarget()).scalarMultiply(1 / (double) steps);
            b.setFakeTarget(b.getFakeTarget().add(fake_target_d));
        }
    }

    private void endGame(Side sideHitted) {
        if (sideHitted == null) {
            winningSide = lastHittedPlayer.getSide().getOpposite();
        } else {
            winningSide = sideHitted.getOpposite();
        }
    }

    private Side getBallSide(Ball b) {
        Vector2D position = b.getPosition();
        if (position.getX() >= 0 && position.getX() <= Pitch.WIDTH
                && position.getY() >= -Pitch.HALF_HEIGHT && position.getY() <= Pitch.HALF_HEIGHT) {
            if (position.getY() >= 0) {
                return Side.HOME;
            } else {
                return Side.AWAY;
            }
        } else {
            return null;
        }
    }

    private boolean trySave(Side sideHitted) {
        Player p = getPitch().getPlayer(sideHitted);
        double save_max_distance = PLAYER_HAND_LENGTH + getActualSaveAddDistance(p);
        if (p.getPosition().subtract(getPitch().getBall().getPosition()).getNorm() <= save_max_distance && !p.isLying()) {
            p.lieDown();
            hitBall(p);
            decrasePlayerEnergy(p, ENERGY_LOSS_PER_SAVE);
            return true;
        } else {
            return false;
        }
    }

    private void performBallAction(Ball b) {
        if (hasBallHittedGround(b)) {
            Side side = getBallSide(b);
            if (side == null || !trySave(side)) {
                endGame(side);
            }
        } else {
            moveBall(b);
        }
    }

    public void performEndOfGameActions() {
        getPitch().getPlayer(Side.HOME).changeEnergy(ENERGY_REGAIN_PER_GAME);
        getPitch().getPlayer(Side.AWAY).changeEnergy(ENERGY_REGAIN_PER_GAME);
    }

    public void performEndOfSetActions() {
        getPitch().getPlayer(Side.HOME).changeEnergy(ENERGY_REGAIN_PER_SET);
        getPitch().getPlayer(Side.AWAY).changeEnergy(ENERGY_REGAIN_PER_SET);
    }
}

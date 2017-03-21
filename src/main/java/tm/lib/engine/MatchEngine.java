package tm.lib.engine;

import java.util.Random;
import tm.lib.domain.competition.Match;

public class MatchEngine
{
    private Pitch pitch;
    private int gameResult;
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
    private static final double MAX_NET_ZONE_LENGTH = Pitch.HHEIGHT * 3 / 10;
    private static final double MIN_NET_ZONE_LENGTH = Pitch.HHEIGHT * 3 / 20;
    //private static final double NET_ZONE_MAX_LENGTH = Pitch.HHEIGHT / 5;
    private static final double NET_PONG = Pitch.HHEIGHT / 35;
    private static final double ENERGY_LOSS_PER_DISTANCE = 0.9 / Pitch.WIDTH;
    private static final double ENERGY_LOSS_PER_HIT = 0.3;
    private static final double ENERGY_LOSS_PER_SAVE = 0.3;
    private static final double ENERGY_REGAIN_PER_GAME = 1;
    private static final double ENERGY_REGAIN_PER_SET = 3;

    public MatchEngine(Match match)
    {
        pitch = new Pitch(match.getFirstPlayer(), match.getSecondPlayer(), match.getVenue());
        pitch.set_initial_pos(1);
        lastHittedPlayer = null;
    }

    public void reset(int player)
    {
        getPitch().set_initial_pos(player);
        gameResult = 0;
    }

    public void next()
    {
        performPlayerAction(getPitch().player_1);
        performPlayerAction(getPitch().player_2);
        performBallAction(getPitch().ball);
    }

    public Pitch getPitch()
    {
        return pitch;
    }

    public int getGameResult()
    {
        return gameResult;
    }

    private Player getPlayer(int index)
    {
        if (index == 1)
        {
            return getPitch().player_1;
        }
        else
        {
            return getPitch().player_2;
        }
    }

    private Player getOppositePlayer(Player p)
    {
        if (p == getPitch().player_1)
        {
            return getPitch().player_2;
        }
        else
        {
            return getPitch().player_1;
        }
    }

    private double applyValueMargins(double base, double min_value, double max_value)
    {
        return base / 100 * (max_value - min_value) + min_value;
    }

    private double applyEnergyModifier(Player p, double value, double modifier)
    {
        value *= (1 - modifier) * p.getEnergy() / 100 + modifier;
        return value;
    }

    private double applyInvertedEnergyModifier(Player p, double value, double modifier)
    {
        value *= 1 + modifier - (value / 100) * modifier;
        return value;
    }

    private double getVenueSpeedModifier()
    {
        return applyValueMargins(getPitch().venue.roughness, MIN_VENUE_SPEED_MODIFIER, MAX_VENUE_SPEED_MODIFIER);
    }

    private double getActualPlayerSpeed(Player p)
    {
        //double speed = p.person.speed / 100 * (PLAYER_MAX_SPEED - PLAYER_MIN_SPEED) + PLAYER_MIN_SPEED;
        //speed *= (1 - SPEED_ENERGY_MODIFIER) * p.energy / 100 + SPEED_ENERGY_MODIFIER;
        double speed = applyValueMargins(p.getPerson().getSpeed(), PLAYER_MIN_SPEED, PLAYER_MAX_SPEED);
        speed = applyEnergyModifier(p, speed, SPEED_ENERGY_MODIFIER);
        speed = speed * getVenueSpeedModifier();
        return speed;
    }

    private double getVenueAccelerationModifier()
    {
        return applyValueMargins(100 - getPitch().venue.slippery, MIN_VENUE_ACC_MODIFIER, MAX_VENUE_ACC_MODIFIER);
    }

    private double getActualPlayerAcceleration(Player p)
    {
        double acc = applyValueMargins(p.getPerson().getAcceleration(), PLAYER_MIN_ACCELERATION, PLAYER_MAX_ACCELERATION);
        acc = applyEnergyModifier(p, acc, ACCELERATION_ENERGY_MODIFIER);
        acc = acc * getVenueAccelerationModifier();
        return acc;
    }

    private double getActualBallSpeed(Player p)
    {
        //double ball_speed = p.person.hit_power / 100 * (BALL_MAX_SPEED - BALL_MIN_SPEED) + BALL_MIN_SPEED;
        //ball_speed *= (1 - BALL_SPEED_ENERGY_MODIFIER) * p.energy / 100 + BALL_SPEED_ENERGY_MODIFIER;
        double ball_speed = applyValueMargins(p.getPerson().getHitPower(), BALL_MIN_SPEED, BALL_MAX_SPEED);
        ball_speed = applyEnergyModifier(p, ball_speed, BALL_SPEED_ENERGY_MODIFIER);
        return ball_speed;
    }

    private double getActualShotRange(Player p)
    {
        double shot_range = applyValueMargins(p.getPerson().getShotRange(), SHOT_MIN_RANGE, SHOT_MAX_RANGE);
        shot_range = applyEnergyModifier(p, shot_range, SHOT_RANGE_ENERGY_MODIFIER);
        return shot_range;
    }

    private double getActualTargetRange(Player p)
    {
        double target_range = applyValueMargins(100 - p.getPerson().getAccuracy(), TARGET_MIN_RANGE, TARGET_MAX_RANGE);
        target_range = applyInvertedEnergyModifier(p, target_range, TARGET_RANGE_ENERGY_MODIFIER);
        return target_range;
    }

    private double getActualFakeTargetRange(Player p)
    {
        double fake_target_range = applyValueMargins(p.getPerson().getCunning(), 0, FAKE_TARGET_MAX_RANGE);
        fake_target_range = applyEnergyModifier(p, fake_target_range, FAKE_TARGET_ENERGY_MODIFIER);
        return fake_target_range;
    }

    private double getActualSkillRange(Player p)
    {
        double skill_range = applyValueMargins(p.getPerson().getSkill(), 0, SKILL_MAX_RANGE);
        skill_range = applyEnergyModifier(p, skill_range, SKILL_RANGE_ENERGY_MODIFIER);
        return skill_range;
    }

    private double getActualRiskMargin(Player p)
    {
        double risk_margin = applyValueMargins(100 - p.getPerson().getRisk(), 0, MAX_RISK_MARGIN);
        return risk_margin;
    }

    private double getActualSaveAddDistance(Player p)
    {
        double save_add_distance = applyValueMargins(p.getPerson().getDexterity(), SAVE_MIN_ADD_DISTANCE, SAVE_MAX_ADD_DISTANCE);
        save_add_distance = applyEnergyModifier(p, save_add_distance, SAVE_ADD_DISTANCE_ENERGY_MODIFIER);
        return save_add_distance;
    }

    private double getActualLyingTime(Player p)
    {
        double lying_time = applyValueMargins(100 - p.getPerson().getDexterity(), MIN_LYING_TIME, MAX_LYING_TIME);
        lying_time = applyInvertedEnergyModifier(p, lying_time, LYING_TIME_ENERGY_MODIFIER);
        return lying_time;
    }

    private void decrasePlayerEnergy(Player p, double value)
    {
        double energy_decrease_modifier = applyValueMargins(100 - p.getPerson().getEndurance(), ENERGY_DECREASE_MIN_MODIFIER, ENERGY_DECREASE_MAX_MODIFIER);
        value = value * energy_decrease_modifier;
        p.changeEnergy(-value);
    }

    private double getNetZone()
    {
        return applyValueMargins(getPitch().venue.net_height, MIN_NET_ZONE_LENGTH, MAX_NET_ZONE_LENGTH);
    }

    private boolean isPlayerZoneTargeted(Player p)
    {
        if (getPitch().ball.fake_target.x < 0 || getPitch().ball.fake_target.x > Pitch.WIDTH
                || getPitch().ball.fake_target.y > Pitch.HHEIGHT || getPitch().ball.fake_target.y < -Pitch.HHEIGHT)
        {
            return false;
        }
        boolean fp_target = (getPitch().ball.fake_target.y >= 0);
        if ((p.getSide() == Side.HOME && fp_target) || (p.getSide() == Side.AWAY && !fp_target))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean canHitBall(Player p)
    {
        if (p.position.dist(getPitch().ball.position) <= PLAYER_HAND_LENGTH)
        {
            return true;
        }
        else
        {
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
    private boolean isTargetSmartEnough(Player p, Point2d target)
    {
        Player opposite = getOppositePlayer(p);
        double dist = target.minus(opposite.position).norm();
        double player_no_hit_range = getActualSkillRange(p);
        if (dist >= player_no_hit_range)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isTargetHighEnough(Player p, Point2d target)
    {
        double net_zone_length = Math.abs(p.position.y) / Pitch.HHEIGHT * getNetZone();
        double mod = getPlayerModifier(p);
        if (target.y * mod > 0)
        {
            return true;
        }

        if (Math.abs(target.y) > net_zone_length)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void hasBallHittedNet(Player p, Point2d target)
    {
        double mod = getPlayerModifier(p);
        Point2d d = target.minus(p.position);
        double k = Math.abs(p.position.y / (target.y - p.position.y));
        d = d.multiply(k);
        pitch.ball.target = p.position.plus(d);
        pitch.ball.target.y = getPitch().ball.target.y + mod * NET_PONG;
        pitch.ball.fake_target = target;
        net_hitted = true;
    }

    private void getShotDistanceModification(Player p, Point2d target)
    {
        Point2d d = target.minus(p.position);
        double dist = d.norm();

        double optimal_shot_distance = getActualShotRange(p);
        if (dist > optimal_shot_distance)
        {
            double excess = dist - optimal_shot_distance;
            double r = Math.pow(random.nextDouble(), 2);
            d = d.multiply((optimal_shot_distance + excess * r) / (optimal_shot_distance + excess));
            target.set(d.plus(p.position));
        }
    }

    private void getNewBallTarget(Player p, Ball b)
    {
        double mod = getPlayerModifier(p);

        Point2d target = new Point2d();
        boolean found = false;

        double net_zone_length = Math.abs(p.position.y) / Pitch.HHEIGHT * getNetZone();
        double risk_margin = getActualRiskMargin(p);
        while (!found)
        {
            target.x = random.nextDouble() * (Pitch.WIDTH - 2 * risk_margin) + risk_margin;
            target.y = -mod * (random.nextDouble() * (Pitch.HHEIGHT - net_zone_length - 2 * risk_margin) + net_zone_length + risk_margin);
            //target.y = -mod * random.nextDouble() * Pitch.HHEIGHT;

            if (isTargetSmartEnough(p, target))
            {
                found = true;
            }
        }

        getShotDistanceModification(p, target);

        double target_range = getActualTargetRange(p);
        double phi = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * target_range;
        target.x = target.x + Math.cos(phi) * r;
        target.y = target.y + Math.sin(phi) * r;

        if (!isTargetHighEnough(p, target))
        {
            hasBallHittedNet(p, target);
        }
        else
        {
            b.target = target;
        }
    }

    private void getNewFakeBallTarget(Player p, Ball b)
    {
        double fake_target_range = getActualFakeTargetRange(p);
        double phi = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * fake_target_range;
        double target_x = b.target.x + Math.cos(phi) * r;
        double target_y = b.target.y + Math.sin(phi) * r;

        b.fake_target = new Point2d(target_x, target_y);
    }

    private void hitBall(Player p)
    {
        getNewBallTarget(p, getPitch().ball);
        pitch.ball.speed = getActualBallSpeed(p);
        if (net_hitted)
        {
            net_hitted = false;
        }
        else
        {
            getNewFakeBallTarget(p, getPitch().ball);
        }
        lastHittedPlayer = p;
        decrasePlayerEnergy(p, ENERGY_LOSS_PER_HIT);
    }

    private boolean getZoneHitThreat(Player p)
    {
        double mod = getPlayerModifier(p);
        if (mod * getPitch().ball.fake_target.y > -Pitch.HHEIGHT / 6)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private Point2d getPlayerOptimalPosition(Player p)
    {
        Player opp = getOppositePlayer(p);
        double net_zone_length = Math.abs(opp.position.y) / Pitch.HHEIGHT * getNetZone();
        if (net_zone_length < 0)
        {
            net_zone_length = 0;
        }
        double mod = getPlayerModifier(p);
        double optimal_x = Pitch.WIDTH / 2;
        double optimal_y = mod * ((Pitch.HHEIGHT - net_zone_length) / 2 + net_zone_length);
        return new Point2d(optimal_x, optimal_y);
    }

    private void movePlayerToTarget(Player p, Point2d target)
    {
        double speed = getActualPlayerSpeed(p);
        double acc = getActualPlayerAcceleration(p);
        double step = speed * TIME_STEP;
        double ac_step = acc * TIME_STEP;

        Point2d v = p.direction.multiply(p.speed * TIME_STEP);
        Point2d d = target.minus(p.position);
        double dd = d.norm();

        if (dd > step)
        {//PLAYER_SPEED) {
            d = d.div(dd).multiply(step);
        }
        Point2d dv = d.minus(v);
        double dv_len = dv.norm();

        if (dv_len > ac_step)
        {//PLAYER_ACCELERATION) {
            dv = dv.div(dv_len).multiply(ac_step);
        }

        v = v.plus(dv);
        p.speed = v.norm() / TIME_STEP;
        if (p.speed != 0)
        {
            p.direction = v.div(p.speed * TIME_STEP);
        }

        Point2d move = p.direction.multiply(p.speed * TIME_STEP);
        p.position = p.position.plus(move);
        decrasePlayerEnergy(p, move.norm() / getVenueSpeedModifier() * ENERGY_LOSS_PER_DISTANCE);
    }

    private void movePlayer(Player p)
    {
        Point2d target;
        //if (player_zone_targeted(p)) 
        if (getZoneHitThreat(p))
        {
            target = new Point2d(getPitch().ball.fake_target);
            double mod = getPlayerModifier(p);
            if (target.y * mod < 0)
            {
                target.y = 0;
            }
            if (target.x < 0)
            {
                target.x = 0;
            }
            if (target.x > Pitch.WIDTH)
            {
                target.x = Pitch.WIDTH;
            }
            /*if (target.y > mod * Pitch.HHEIGHT) {
             target.y 
             }*/
        }
        else
        //target = player_standard_position(p);
        {
            target = getPlayerOptimalPosition(p);
        }

        movePlayerToTarget(p, target);
    }

    private void performLyingAction(Player p)
    {
        Point2d target = p.position;
        movePlayerToTarget(p, target);
        p.lying_time += TIME_STEP;
        double lying_time = getActualLyingTime(p);
        if (p.lying_time >= lying_time)
        {
            p.lying = false;
        }
    }

    private void performPlayerAction(Player p)
    {
        if (p.lying)
        {
            performLyingAction(p);
        }
        else
        {
            if (isPlayerZoneTargeted(p) && canHitBall(p))
            {
                hitBall(p);
            }
            else
            {
                movePlayer(p);
            }
        }
    }

    private boolean hasBallHittedGround(Ball b)
    {
        if (b.target.minus(b.position).norm() < 0.001)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void moveBall(Ball b)
    {
        Point2d d = b.fake_target.minus(b.position);
        double dd = d.norm();
        double dist_to_target = b.target.minus(b.position).norm();
        double step = TIME_STEP * b.speed;
        double m_step = step * dd / dist_to_target;
        if (dd > m_step)
        {
            d = d.div(dd).multiply(m_step);
        }
        b.position = b.position.plus(d);

        int steps = (int) (dist_to_target / step);
        if (steps == 0)
        {
            b.fake_target = b.target;
        }
        else
        {
            Point2d fake_target_d = b.target.minus(b.fake_target).div(steps);
            b.fake_target = b.fake_target.plus(fake_target_d);
        }
    }

    private void endGame(int zone_hitted)
    {
        if (zone_hitted == 0)
        {
            gameResult = 2 - lastHittedPlayer.getSide().ordinal();
        }
        else
        {
            gameResult = 3 - zone_hitted;
        }
    }

    private int getBallZone(Ball b)
    {
        if (b.position.x >= 0 && b.position.x <= Pitch.WIDTH
                && b.position.y >= -Pitch.HHEIGHT && b.position.y <= Pitch.HHEIGHT)
        {
            if (b.position.y >= 0)
            {
                return 1;
            }
            else
            {
                return 2;
            }
        }
        else
        {
            return 0;
        }
    }

    private boolean trySave(int zone_hitted)
    {
        Player p = getPlayer(zone_hitted);
        double save_max_distance = PLAYER_HAND_LENGTH + getActualSaveAddDistance(p);
        if (p.position.minus(getPitch().ball.position).norm() <= save_max_distance && !p.lying)
        {
            p.lying = true;
            p.lying_time = 0;
            hitBall(p);
            decrasePlayerEnergy(p, ENERGY_LOSS_PER_SAVE);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void performBallAction(Ball b)
    {
        if (hasBallHittedGround(b))
        {
            int zone = getBallZone(b);
            if (zone == 0 || !trySave(zone))
            {
                endGame(zone);
            }
        }
        else
        {
            moveBall(b);
        }
    }

    public void performEndOfGameActions()
    {
        getPitch().player_1.changeEnergy(ENERGY_REGAIN_PER_GAME);
        getPitch().player_2.changeEnergy(ENERGY_REGAIN_PER_GAME);
    }

    public void performEndOfSetActions()
    {
        getPitch().player_1.changeEnergy(ENERGY_REGAIN_PER_SET);
        getPitch().player_2.changeEnergy(ENERGY_REGAIN_PER_SET);
    }
}

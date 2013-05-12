package tm.lib.engine;

import tm.lib.base.*;
import java.util.Random;

public class MatchEngine {	
	public Pitch pitch;
	public int game_result;
	private Player last_hitted;
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
	
	public MatchEngine(Match match) {
		pitch = new Pitch(match);
		pitch.set_initial_pos(1);
		last_hitted = null;
	}
	
	public void initial_positions(int player) {
		pitch.set_initial_pos(player);
	}
	
	public void next() {
		player_action(pitch.player_1);
		player_action(pitch.player_2);
		ball_action(pitch.ball);
	}
	
	private Player get_player(int index) {
		if (index == 1)
			return pitch.player_1;
		else
			return pitch.player_2;
	}
	
	private Player opposite_player(Player p) {
		if (p == pitch.player_1)
			return pitch.player_2;
		else 
			return pitch.player_1;
	}
	
	private double apply_value_margins(double base, double min_value, double max_value) {
		return base / 100 * (max_value - min_value) + min_value;
	}
	
	private double apply_energy_modifier(Player p, double value, double modifier) {
		value *= (1 - modifier) * p.energy / 100 + modifier;
		return value;
	}

	private double apply_invert_energy_modifier(Player p, double value, double modifier) {
		value *= 1 + modifier - (value / 100) * modifier;
		return value;
	}

	private double venue_speed_modifier() {
		return apply_value_margins(pitch.venue.roughness, MIN_VENUE_SPEED_MODIFIER, MAX_VENUE_SPEED_MODIFIER);
	}		
	
	private double actual_player_speed(Player p) {
		//double speed = p.person.speed / 100 * (PLAYER_MAX_SPEED - PLAYER_MIN_SPEED) + PLAYER_MIN_SPEED;
		//speed *= (1 - SPEED_ENERGY_MODIFIER) * p.energy / 100 + SPEED_ENERGY_MODIFIER;
		double speed = apply_value_margins(p.person.getSpeed(), PLAYER_MIN_SPEED, PLAYER_MAX_SPEED);
		speed = apply_energy_modifier(p, speed, SPEED_ENERGY_MODIFIER);
		speed = speed * venue_speed_modifier();
		return speed;
	}
	
	private double venue_acc_modifier() {
		return apply_value_margins(100 - pitch.venue.slippery, MIN_VENUE_ACC_MODIFIER, MAX_VENUE_ACC_MODIFIER);
	}
	
	private double actual_player_acceleration(Player p) {
		double acc = apply_value_margins(p.person.getAcceleration(), PLAYER_MIN_ACCELERATION, PLAYER_MAX_ACCELERATION);
		acc = apply_energy_modifier(p, acc, ACCELERATION_ENERGY_MODIFIER);
		acc = acc * venue_acc_modifier();
		return acc;
 	}
	
	private double actual_ball_speed(Player p) {
		//double ball_speed = p.person.hit_power / 100 * (BALL_MAX_SPEED - BALL_MIN_SPEED) + BALL_MIN_SPEED;
		//ball_speed *= (1 - BALL_SPEED_ENERGY_MODIFIER) * p.energy / 100 + BALL_SPEED_ENERGY_MODIFIER;
		double ball_speed = apply_value_margins(p.person.getHitPower(), BALL_MIN_SPEED, BALL_MAX_SPEED);
		ball_speed = apply_energy_modifier(p, ball_speed, BALL_SPEED_ENERGY_MODIFIER);
		return ball_speed;
	}
	
	private double actual_shot_range(Player p) {
		double shot_range = apply_value_margins(p.person.getShotRange(), SHOT_MIN_RANGE, SHOT_MAX_RANGE);
		shot_range = apply_energy_modifier(p, shot_range, SHOT_RANGE_ENERGY_MODIFIER);
		return shot_range;		
	}

	private double actual_target_range(Player p) {
		double target_range = apply_value_margins(100 - p.person.getAccuracy(), TARGET_MIN_RANGE, TARGET_MAX_RANGE);
		target_range = apply_invert_energy_modifier(p, target_range, TARGET_RANGE_ENERGY_MODIFIER);
		return target_range;		
	}	
	
	private double actual_fake_target_range(Player p) {
		double fake_target_range = apply_value_margins(p.person.getCunning(), 0, FAKE_TARGET_MAX_RANGE);
		fake_target_range = apply_energy_modifier(p, fake_target_range, FAKE_TARGET_ENERGY_MODIFIER);
		return fake_target_range;		
	}
	
	private double actual_skill_range(Player p) {
		double skill_range = apply_value_margins(p.person.getSkill(), 0, SKILL_MAX_RANGE);
		skill_range = apply_energy_modifier(p, skill_range, SKILL_RANGE_ENERGY_MODIFIER);
		return skill_range;
	}
	
	private double actual_risk_margin(Player p) {
		double risk_margin = apply_value_margins(100 - p.person.getRisk(), 0, MAX_RISK_MARGIN);
		return risk_margin;
	}
	
	private double actual_save_add_distance(Player p) {
		double save_add_distance = apply_value_margins(p.person.getDexterity(), SAVE_MIN_ADD_DISTANCE, SAVE_MAX_ADD_DISTANCE);
		save_add_distance = apply_energy_modifier(p, save_add_distance, SAVE_ADD_DISTANCE_ENERGY_MODIFIER);
		return save_add_distance;
	}
	
	private double actual_lying_time(Player p) {
		double lying_time = apply_value_margins(100 - p.person.getDexterity(), MIN_LYING_TIME, MAX_LYING_TIME);
		lying_time = apply_invert_energy_modifier(p, lying_time, LYING_TIME_ENERGY_MODIFIER);
		return lying_time;
	}
	
	private void player_loose_energy(Player p, double value) {
		double energy_decrease_modifier = apply_value_margins(100 - p.person.getEndurance(), ENERGY_DECREASE_MIN_MODIFIER, ENERGY_DECREASE_MAX_MODIFIER);
		value = value * energy_decrease_modifier;
		p.loose_energy(value);
	}
	
	private double net_zone() {
		return apply_value_margins(pitch.venue.net_height, MIN_NET_ZONE_LENGTH, MAX_NET_ZONE_LENGTH);
	}
	
	private boolean player_zone_targeted(Player p) {
		if (pitch.ball.fake_target.x < 0 || pitch.ball.fake_target.x > Pitch.WIDTH || 
				pitch.ball.fake_target.y > Pitch.HHEIGHT || pitch.ball.fake_target.y < - Pitch.HHEIGHT) {
			return false;
		}
		boolean fp_target = (pitch.ball.fake_target.y >= 0);
		if ((p.id == 1 && fp_target) || (p.id == 2 && !fp_target))
			return true;
		else 
			return false;
	}
	
	private boolean can_hit_ball(Player p) {
		if (p.position.dist(pitch.ball.position) <= PLAYER_HAND_LENGTH)
			return true;
		else 
			return false;
	}

	private double player_modifier(Player p) {
		if (p.id == 1)
			return 1;
		else 
			return -1;
	}
	
	/*private DPoint player_standard_position(Player p) {
		if (p.id == 1) 
			return new DPoint(Pitch.WIDTH / 2, Pitch.HHEIGHT / 2);
		else 
			return new DPoint(Pitch.WIDTH / 2, - Pitch.HHEIGHT / 2);
	}*/
	
	private boolean target_is_smart_enough(Player p, DPoint target) {
		Player opposite = opposite_player(p);
		double dist = target.minus(opposite.position).norm();
		double player_no_hit_range = actual_skill_range(p);
		if (dist >= player_no_hit_range)
			return true;
		else 
			return false;
	}
	
	private boolean target_is_high_enough(Player p, DPoint target) {
		double net_zone_length = Math.abs(p.position.y) / Pitch.HHEIGHT * net_zone();
		double mod = player_modifier(p);
		if (target.y * mod > 0)
			return true;
		
		if (Math.abs(target.y) > net_zone_length)
			return true;
		else
			return false;
	}
	
	private void ball_hit_net(Player p, DPoint target) {
		double mod = player_modifier(p);
		DPoint d = target.minus(p.position);
		double k = Math.abs(p.position.y / (target.y - p.position.y));
		d = d.multiply(k);
		pitch.ball.target = p.position.plus(d);
		pitch.ball.target.y = pitch.ball.target.y + mod * NET_PONG;
		pitch.ball.fake_target = target;		
		net_hitted = true;
	}
	
	private void shot_distance_modification(Player p, DPoint target) {
		DPoint d = target.minus(p.position);
		double dist = d.norm();
		
		double optimal_shot_distance = actual_shot_range(p);
		if (dist > optimal_shot_distance) {
			double excess = dist - optimal_shot_distance;
			double r = Math.pow(random.nextDouble(), 2);
			d = d.multiply((optimal_shot_distance + excess * r) / (optimal_shot_distance + excess));
			target.set(d.plus(p.position));
		}
	}
	
	private void new_ball_target(Player p, Ball b) {
		double mod = player_modifier(p);
		
		DPoint target = new DPoint();		
		boolean found = false;
		
		double net_zone_length = Math.abs(p.position.y) / Pitch.HHEIGHT * net_zone();
		double risk_margin = actual_risk_margin(p);
		while (!found) {
			target.x = random.nextDouble() * (Pitch.WIDTH - 2 * risk_margin) + risk_margin;
			target.y = - mod * (random.nextDouble() * (Pitch.HHEIGHT - net_zone_length - 2 * risk_margin) + net_zone_length + risk_margin);
			//target.y = -mod * random.nextDouble() * Pitch.HHEIGHT;
			
			if (target_is_smart_enough(p, target))
				found = true;
		}
		
		shot_distance_modification(p, target);
		
		double target_range = actual_target_range(p);
		double phi = random.nextDouble() * 2 * Math.PI;
		double r = random.nextDouble() * target_range;
		target.x = target.x + Math.cos(phi) * r;
		target.y = target.y + Math.sin(phi) * r;	
		
		if (!target_is_high_enough(p, target)) {
			ball_hit_net(p, target);
		}
		else 
			b.target = target;		
	}
	
	private void new_fake_ball_target(Player p, Ball b) {
		double fake_target_range = actual_fake_target_range(p);
		double phi = random.nextDouble() * 2 * Math.PI;
		double r = random.nextDouble() * fake_target_range;	
		double target_x = b.target.x + Math.cos(phi) * r;
		double target_y = b.target.y + Math.sin(phi) * r;			
		
		b.fake_target = new DPoint(target_x, target_y);	
	}
	
	private void hit_ball(Player p) {
		new_ball_target(p, pitch.ball);
		pitch.ball.speed = actual_ball_speed(p);
		if (net_hitted) 
			net_hitted = false;
		else 
			new_fake_ball_target(p, pitch.ball);
		last_hitted = p;
		player_loose_energy(p, ENERGY_LOSS_PER_HIT);
	}
	
	private boolean zone_hit_threat(Player p) {
		double mod = player_modifier(p);
		if (mod * pitch.ball.fake_target.y > -Pitch.HHEIGHT / 6)
			return true;
		else 
			return false;
	}
	
	private DPoint player_optimal_position(Player p) {
		Player opp = opposite_player(p);
		double net_zone_length = Math.abs(opp.position.y) / Pitch.HHEIGHT * net_zone();
		if (net_zone_length < 0)
			net_zone_length = 0;
		double mod = player_modifier(p);
		double optimal_x = Pitch.WIDTH / 2;
		double optimal_y = mod * ((Pitch.HHEIGHT - net_zone_length) / 2 + net_zone_length);
		return new DPoint(optimal_x, optimal_y);
	}
	
	private void player_move_to_target(Player p, DPoint target) {
		double speed = actual_player_speed(p);
		double acc = actual_player_acceleration(p);
		double step = speed * TIME_STEP;		
		double ac_step = acc * TIME_STEP;	
		
		DPoint v = p.direction.multiply(p.speed * TIME_STEP);
		DPoint d = target.minus(p.position);
		double dd = d.norm();

		if (dd > step) {//PLAYER_SPEED) {
			d = d.div(dd).multiply(step);
		}
		DPoint dv = d.minus(v);
		double dv_len = dv.norm();

		if (dv_len > ac_step) {//PLAYER_ACCELERATION) {
			dv = dv.div(dv_len).multiply(ac_step);
		}
		
		v = v.plus(dv);
		p.speed = v.norm() / TIME_STEP;
		if (p.speed != 0) {
			p.direction = v.div(p.speed * TIME_STEP);
		}
		
		DPoint move = p.direction.multiply(p.speed * TIME_STEP);
		p.position = p.position.plus(move);		
		player_loose_energy(p, move.norm() / venue_speed_modifier() * ENERGY_LOSS_PER_DISTANCE);
	}
	
	private void player_move(Player p) {
		DPoint target;
		//if (player_zone_targeted(p)) 
		if (zone_hit_threat(p)) {
			target = new DPoint(pitch.ball.fake_target);
			double mod = player_modifier(p);
			if (target.y * mod < 0)
				target.y = 0;
			if (target.x < 0)
				target.x = 0;
			if (target.x > Pitch.WIDTH)
				target.x = Pitch.WIDTH;
			/*if (target.y > mod * Pitch.HHEIGHT) {
				target.y 
			}*/
		}
		else 
			//target = player_standard_position(p);
			target = player_optimal_position(p);
		
		player_move_to_target(p, target);
	}
	
	private void lying_action(Player p) {
		DPoint target = p.position;
		player_move_to_target(p, target);
		p.lying_time += TIME_STEP;
		double lying_time = actual_lying_time(p);
		if (p.lying_time >= lying_time) {
			p.lying = false;
		}		
	}
	
	private void player_action(Player p) {
		if (p.lying) {
			lying_action(p);
		}
		else { 
			if (player_zone_targeted(p) && can_hit_ball(p)) {
				hit_ball(p);
			}
			else 
				player_move(p);
		}
	}
	
	private boolean ball_hit(Ball b) {
		if (b.target.minus(b.position).norm() < 0.001)
			return true;
		else
			return false;
	}
	
	private void ball_move(Ball b) {
		DPoint d = b.fake_target.minus(b.position);
		double dd = d.norm();
		double dist_to_target = b.target.minus(b.position).norm();
		double step = TIME_STEP * b.speed;
		double m_step = step * dd / dist_to_target;
		if (dd > m_step) {
			d = d.div(dd).multiply(m_step);
		}
		b.position = b.position.plus(d);	
		
		int steps = (int) (dist_to_target / step);
		if (steps == 0) {
			b.fake_target = b.target;
		}
		else {
			DPoint fake_target_d = b.target.minus(b.fake_target).div(steps);
			b.fake_target = b.fake_target.plus(fake_target_d);
		}
	}
	
	private void game_end(int zone_hitted) {
		if (zone_hitted == 0) {
			game_result = 3 - last_hitted.id;
		}
		else {
			game_result = 3 - zone_hitted;			
		}
	}
	
	private int ball_zone(Ball b) {
		if (b.position.x >= 0 && b.position.x <= Pitch.WIDTH && 
				b.position.y >= - Pitch.HHEIGHT && b.position.y <= Pitch.HHEIGHT) {
			if (b.position.y >= 0)
				return 1;
			else 
				return 2;
		}
		else {
			return 0;
		}
	}
	
	private boolean try_save(int zone_hitted) {
		Player p = get_player(zone_hitted);
		double save_max_distance = PLAYER_HAND_LENGTH + actual_save_add_distance(p);
		if (p.position.minus(pitch.ball.position).norm() <= save_max_distance && !p.lying) {
			p.lying = true;
			p.lying_time = 0;
			hit_ball(p);
			player_loose_energy(p, ENERGY_LOSS_PER_SAVE);
			return true;
		}
		else
			return false;	
	}
	
	private void ball_action(Ball b) {
		if (ball_hit(b)) {
			int zone = ball_zone(b);
			if (zone == 0 || !try_save(zone))
				game_end(zone);
		}
		else {
			ball_move(b);
		}
	}
	
	public void end_of_game_actions() {
		pitch.player_1.regain_energy(ENERGY_REGAIN_PER_GAME);
		pitch.player_2.regain_energy(ENERGY_REGAIN_PER_GAME);
	}
	
	public void end_of_set_actions() {
		pitch.player_1.regain_energy(ENERGY_REGAIN_PER_SET);
		pitch.player_2.regain_energy(ENERGY_REGAIN_PER_SET);
	}
}

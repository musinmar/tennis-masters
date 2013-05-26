package tm.lib.engine;

import tm.lib.base.*;
import tm.ui.*;
import tm.ui.TenisMasters;

public class MatchManager {
	public Match match;
	public MatchEngine match_engine;
	public Runnable active_timer;
	public Runnable game_timer;
	public Runnable game_end_timer;
	public Runnable set_end_timer;
	public Runnable match_end_timer;
	public MatchWindow ui;
	public Score active_score;
	public int set;
	public boolean additional;
	int active_player;
	int match_time;
	
	//static public final int TIMER_INTERVAL = 30;
	static public final int TIMER_INTERVAL = (int) (1000 * MatchEngine.TIME_STEP);
	static public final int CAPTION_TIME = 1500;
	static public final int CAPTION_TICKS = (int) (CAPTION_TIME / TIMER_INTERVAL);
	
	public MatchManager(MatchWindow match_window, Match match) {
		this.match = match;
		match_engine = new MatchEngine(match);
		ui = match_window;
		active_score = new Score(match.getSets());
		set = 0;
		additional = false;
		active_player = 1;
		match_time = 0;
		
		game_timer = new Runnable() {			
			public void run() {	
				match_engine.next();
				match_time += (int) (MatchEngine.TIME_STEP * 1000);
				ui.refresh_pitch();
				ui.player1_widget.update();
				ui.player2_widget.update();
				ui.match_info_widget.set_time(match_time);
				if (match_engine.getGameResult() != 0) {
					game_has_ended();
				}
				else 
					TenisMasters.display.timerExec(TIMER_INTERVAL, this);
			}		
		};		
		
		game_end_timer = new Runnable() {
			public int i = 0;
			public void run() {
				i ++;
				if (i == CAPTION_TICKS) {
					hide_result_caption();
					i = 0;
					game_end_actions();
				} 
				else {
					TenisMasters.display.timerExec(TIMER_INTERVAL, this);
				}
			}
		};
		
		set_end_timer = new Runnable() {
			public int i = 0;
			public void run() {
				i ++;
				if (i == CAPTION_TICKS) {
					hide_result_caption();
					i = 0;
					set_end_actions();
				} 
				else {
					TenisMasters.display.timerExec(TIMER_INTERVAL, this);
				}
			}
		};
			
		match_end_timer = new Runnable() {
			public int i = 0;
			public void run() {
				i ++;
				if (i == CAPTION_TICKS) {
					hide_result_caption();
					i = 0;
					match_end_actions();
				} 
				else {
					TenisMasters.display.timerExec(TIMER_INTERVAL, this);
				}
			}			
		};
	}
	
	public void start() {
		match_engine.reset(active_player);
		active_timer = game_timer;
		TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
	}
	
	public void pause() {
		TenisMasters.display.timerExec(-1, active_timer);		
	}
	
	public void game_has_ended() {
		if (!additional) {
			if (match_engine.getGameResult() == 1) {
				active_score.sets[set].v1 += 1;
			}
			else {
				active_score.sets[set].v2 += 1;
			}
		}	
		else {
			if (match_engine.getGameResult() == 1) {
				active_score.additionalTime.v1 += 1;
			}
			else {
				active_score.additionalTime.v2 += 1;
			}			
		}
		
		show_game_end_caption();
	}
	
	public void show_game_end_caption() {
		show_game_result_caption();
		active_timer = game_end_timer;
		TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
	}
	
	public void show_set_end_caption() {
		show_set_result_caption();
		active_timer = set_end_timer;
		TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
	}	
	
	public void show_match_end_caption() {
		show_match_result_caption();
		active_timer = match_end_timer;
		TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);		
	}
	
	public boolean check_set_end() {		
		if (!additional) {	
			int v1 = active_score.sets[set].v1;
			int v2 = active_score.sets[set].v2;			
			if (v1 + v2 == Score.BASE_SET_LENGTH)
				return true;
			int rest = Score.BASE_SET_LENGTH - v1 - v2;
			int dif = Math.abs(v1 - v2);
			if (dif > rest) 
				return true;
			else 
				return false;
		}
		else {
			int v1 = active_score.additionalTime.v1;
			int v2 = active_score.additionalTime.v2;		
			if (v1 + v2 < Score.ADDITIONAL_SET_LENGTH) {
				int rest = Score.ADDITIONAL_SET_LENGTH - v1 - v2;
				int dif = Math.abs(v1 - v2);
				if (dif > rest)
					return true;
				else 
					return false;
			}
			else {
				if (v1 + v2 == Score.ADDITIONAL_SET_LENGTH) {
					if (v1 == v2)
						return false;
					else 
						return true;
				}
				else {
					/*if (v1 == Score.MAX_SET_VALUE || v2 == Score.MAX_SET_VALUE) {
						return true;
					}
					else {*/
						if (Math.abs(v1 - v2) == 2) 
							return true;
						else 
							return false;
					//}					
				}				
			}			
		}		
	}
	
	public boolean check_match_end() {
		SetScore s = active_score.get_set_score();
		if (match.isPlayoff()) {
			if (s.v1 + s.v2 < match.getSets()) {
				int dif = Math.abs(s.v1 - s.v2);
				int left = match.getSets() - s.v1 - s.v2;
				if (dif > left)
					return true;
				else 
					return false;
			}
			else {
				if (s.v1 + s.v2 == match.getSets()) {
					if (s.v1 == s.v2)
						return false;
					else 
						return true;
				}
				else {
					return true;
				}				
			}
		}
		else {
			if (s.v1 + s.v2 == match.getSets()) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public void switch_active_player() {
		if (active_player == 1)
			active_player = 2;
		else 
			active_player = 1;
	}
	
	public void game_end_actions() {
		match_engine.performEndOfGameActions();
		if (check_set_end()) {
			show_set_end_caption();
		}
		else {
			if (additional) {
				switch_active_player();
			}
			start();			
		}
	}
	
	public void set_end_actions() {
		match_engine.performEndOfSetActions();
		if (!check_match_end()) {
			if (set != match.getSets() - 1) {
				switch_active_player();
				set ++;
				start();
			}
			else {
				additional = true;
				active_score.additionalTime = new SetScore();
				active_player = 1;
				start();
			}
		}
		else {
			show_match_end_caption();
		}
	}
	
	public void match_end_actions() {
		ui.start_button.setEnabled(false);
		ui.final_score = new Score(active_score);
	}
	
	public void show_game_result_caption() {
		Person won;
		if (match_engine.getGameResult() == 1) {
			won = match.getFirstPlayer();
		}
		else {
			won = match.getSecondPlayer();
		}
		ui.match_info_widget.score_label.setText(active_score.toString());
		ui.match_info_widget.score_label.pack();
		//ui.match_info_widget.score_label.pack();
		ui.shell.layout();
		ui.pitch_widget.set_upper_text("Игрок " + won.getFullName() + " выиграл гейм.");
		ui.pitch_widget.set_bottom_text("Счёт в игре: " + active_score.get_short_score(set, additional));
	}
	
	public void show_set_result_caption() {
		Person won;
		if (!additional) {
			if (active_score.sets[set].v1 > active_score.sets[set].v2) {
				won = match.getFirstPlayer();
			}
			else {
				won = match.getSecondPlayer();
			}			
		}
		else {
			if (active_score.additionalTime.v1 > active_score.additionalTime.v2) {
				won = match.getFirstPlayer();
			}
			else {
				won = match.getSecondPlayer();
			}				
		}
		ui.pitch_widget.set_upper_text("Сет разыгран, победитель -  " + won.getFullName());		
		ui.pitch_widget.set_bottom_text("Счёт в игре: " + active_score.get_short_score(set, additional));		
	}
	
	public void show_match_result_caption() {
		Person won;
		SetScore s = active_score.get_set_score();
		if (s.v1 > s.v2) {
			won = match.getFirstPlayer();
		}
		else if (s.v1 < s.v2) {
			won = match.getSecondPlayer();
		}			
		else {
			if (match.isPlayoff()) {
				if (active_score.additionalTime.v1 > active_score.additionalTime.v2) {
					won = match.getFirstPlayer();
				}
				else 
					won = match.getSecondPlayer();
			}
			else 
				won = null;
		}
		if (won != null) {
			ui.pitch_widget.set_upper_text("Матч завершён, победитель -  " + won.getFullName());
		}
		else {
			ui.pitch_widget.set_upper_text("Матч завершён, итоговый результат - ничья");
		}			
		ui.pitch_widget.set_bottom_text("Счёт в игре: " + active_score.get_short_score(set, additional));		
	}	
	
	public void hide_result_caption() {
		ui.pitch_widget.top_label.setVisible(false);
		ui.pitch_widget.bottom_label.setVisible(false);	
	}
}

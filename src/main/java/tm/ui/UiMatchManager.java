/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm.ui;

import tm.lib.domain.competition.Match;
import tm.lib.domain.core.Person;
import tm.lib.domain.core.BasicScore;
import tm.lib.engine.MatchEngine;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.Pitch;
import tm.lib.engine.Side;

/**
 *
 * @author Corwine
 */
public class UiMatchManager {

    public Match match;
    private MatchSimulator matchSimulator;
    private MatchSimulator.State currentState;

    public Runnable active_timer;
    public Runnable game_timer;
    public Runnable game_end_timer;
    public Runnable set_end_timer;
    public Runnable match_end_timer;
    public MatchWindow ui;

    //static public final int TIMER_INTERVAL = 30;
    static public final int TIMER_INTERVAL = (int) (1000 * MatchEngine.TIME_STEP);
    static public final int CAPTION_TIME = 1500;
    static public final int CAPTION_TICKS = (int) (CAPTION_TIME / TIMER_INTERVAL);

    public UiMatchManager(MatchWindow match_window, Match match) {
        this.match = match;
        matchSimulator = new MatchSimulator(match);
        ui = match_window;

        game_timer = new Runnable() {
            @Override
            public void run() {
                currentState = matchSimulator.proceed();
                /*if (currentState == MatchSimulator.State.PLAYING) {
                    currentState = matchSimulator.proceed();
                }*/
                ui.refresh_pitch();
                ui.player1_widget.update();
                ui.player2_widget.update();
                ui.match_info_widget.set_time(matchSimulator.getMatchTime());
                if (currentState != MatchSimulator.State.PLAYING) {
                    game_has_ended();
                } else {
                    TenisMasters.display.timerExec(TIMER_INTERVAL, this);
                }
            }
        };

        game_end_timer = new Runnable() {
            public int i = 0;

            @Override
            public void run() {
                i++;
                if (i == CAPTION_TICKS) {
                    hide_result_caption();
                    i = 0;
                    game_end_actions();
                } else {
                    TenisMasters.display.timerExec(TIMER_INTERVAL, this);
                }
            }
        };

        set_end_timer = new Runnable() {
            public int i = 0;

            @Override
            public void run() {
                i++;
                if (i == CAPTION_TICKS) {
                    hide_result_caption();
                    i = 0;
                    set_end_actions();
                } else {
                    TenisMasters.display.timerExec(TIMER_INTERVAL, this);
                }
            }
        };

        match_end_timer = new Runnable() {
            public int i = 0;

            @Override
            public void run() {
                i++;
                if (i == CAPTION_TICKS) {
                    hide_result_caption();
                    i = 0;
                    match_end_actions();
                } else {
                    TenisMasters.display.timerExec(TIMER_INTERVAL, this);
                }
            }
        };
    }

    public void start() {
        active_timer = game_timer;
        TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
    }

    public void pause() {
        TenisMasters.display.timerExec(-1, active_timer);
    }

    public Pitch getPitch() {
        return matchSimulator.getPitch();
    }

    private void game_has_ended() {
        show_game_result_caption();
        active_timer = game_end_timer;
        TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
    }

    private void game_end_actions() {
        if (currentState != MatchSimulator.State.GAME_ENDED) {
            show_set_result_caption();
            active_timer = set_end_timer;
            TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
        } else {
            start();
        }
    }

    private void set_end_actions() {
        if (currentState != MatchSimulator.State.MATCH_ENDED) {
            start();
        } else {
            show_match_result_caption();
            active_timer = match_end_timer;
            TenisMasters.display.timerExec(TIMER_INTERVAL, active_timer);
        }
    }

    private void match_end_actions() {
        ui.start_button.setEnabled(false);
        ui.final_score = matchSimulator.getCurrentScore();
    }

    private void show_game_result_caption() {
        Person won;
        if (matchSimulator.getLastGameResult() == Side.HOME) {
            won = match.getFirstPlayer();
        } else {
            won = match.getSecondPlayer();
        }
        ui.match_info_widget.score_label.setText(matchSimulator.getCurrentScore().toString());
        ui.match_info_widget.score_label.pack();
        //ui.match_info_widget.score_label.pack();
        ui.shell.layout();
        ui.pitch_widget.setUpperText("Игрок " + won.getFullName() + " выиграл гейм.");
        ui.pitch_widget.setBottomText("Счёт в игре: " + matchSimulator.getCurrentScore());
    }

    private void show_set_result_caption() {
        Person won = (matchSimulator.getLastGameResult() == Side.HOME) ? match.getFirstPlayer() : match.getSecondPlayer();
        ui.pitch_widget.setUpperText("Сет разыгран, победитель -  " + won.getFullName());
        ui.pitch_widget.setBottomText("Счёт в игре: " + matchSimulator.getCurrentScore());
    }

    private void show_match_result_caption() {
        Person won;
        BasicScore s = matchSimulator.getCurrentScore().getScoreBySets();
        if (s.v1 > s.v2) {
            won = match.getFirstPlayer();
        } else {
            if (s.v1 < s.v2) {
                won = match.getSecondPlayer();
            } else {
                if (match.isPlayoff()) {
                    if (matchSimulator.getCurrentScore().getAdditionalTime().v1 > matchSimulator.getCurrentScore().getAdditionalTime().v2) {
                        won = match.getFirstPlayer();
                    } else {
                        won = match.getSecondPlayer();
                    }
                } else {
                    won = null;
                }
            }
        }
        if (won != null) {
            ui.pitch_widget.setUpperText("Матч завершён, победитель -  " + won.getFullName());
        } else {
            ui.pitch_widget.setUpperText("Матч завершён, итоговый результат - ничья");
        }
        ui.pitch_widget.setBottomText("Счёт в игре: " + matchSimulator.getCurrentScore());
    }

    private void hide_result_caption() {
        ui.pitch_widget.hideResultCaption();
    }
}

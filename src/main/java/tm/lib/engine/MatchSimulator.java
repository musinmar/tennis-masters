/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm.lib.engine;

import tm.lib.domain.competition.Match;
import tm.lib.domain.core.MatchScore;

public class MatchSimulator {

    private final MatchEngine matchEngine;
    private final MatchProgressTracker matchProgressTracker;
    private long matchTime = 0;
    private Side lastGameResult;

    public static enum State {
        PLAYING,
        GAME_ENDED,
        SET_ENDED,
        MATCH_ENDED
    }

    public MatchSimulator(Match match) {
        matchEngine = new MatchEngine(match);
        matchProgressTracker = new MatchProgressTracker(match.getSets(), match.isPlayoff());
        matchProgressTracker.startNewSet();
    }

    public long getMatchTime() {
        return matchTime;
    }

    public MatchScore getCurrentScore() {
        return matchProgressTracker.buildScore();
    }

    public Side getLastGameResult() {
        assert lastGameResult != null;
        return lastGameResult;
    }
    
    public Pitch getPitch() {
        return matchEngine.getPitch();
    }

    public State proceed() {
        if (matchProgressTracker.isSetFinished()) {
            matchProgressTracker.startNewSet();
        }

        matchEngine.next();
        matchTime += (long) (MatchEngine.TIME_STEP * 1000);
        if (matchEngine.getWinningSide() == null) {
            return State.PLAYING;
        }

        return performEndOfGameActions();
    }

    private State performEndOfGameActions() {
        lastGameResult = matchEngine.getWinningSide();
        matchProgressTracker.addPoint(lastGameResult);
        matchEngine.performEndOfGameActions();
        if (!matchProgressTracker.isSetFinished()) {
            matchEngine.reset(matchProgressTracker.getServingSide());
            return State.GAME_ENDED;
        }

        return performEndOfSetActions();
    }

    private State performEndOfSetActions() {
        matchEngine.performEndOfSetActions();
        if (!matchProgressTracker.isMatchFinished()) {
            matchEngine.reset(matchProgressTracker.getServingSide());
            return State.SET_ENDED;
        } else {
            return State.MATCH_ENDED;
        }
    }
}

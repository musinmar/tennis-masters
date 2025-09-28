/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm.lib.engine;

import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;

import java.util.ArrayList;
import java.util.List;

public class MatchSimulator {

    private final Match match;
    private final MatchEngine matchEngine;
    private final MatchProgressTracker matchProgressTracker;
    private long matchTime;
    private long gameStartTime;
    private Side lastGameResult;
    private final List<PointResult> pointResults = new ArrayList<>();

    public enum State {
        PLAYING,
        GAME_ENDED,
        SET_ENDED,
        MATCH_ENDED
    }

    public static class PointResult {
        private final Side winningSide;
        private final long time;

        public PointResult(Side winningSide, long time) {
            this.winningSide = winningSide;
            this.time = time;
        }

        public Side getWinningSide() {
            return winningSide;
        }

        public long getTime() {
            return time;
        }
    }

    public MatchSimulator(Match match, StrategyHolder strategyHolder) {
        this.match = match;
        matchEngine = new MatchEngine(match, strategyHolder);
        matchProgressTracker = new MatchProgressTracker(match.getSets(), match.isPlayOff());
        matchProgressTracker.startNewSet();
    }

    public long getMatchTime() {
        return matchTime;
    }

    public MatchScore getCurrentScore() {
        return matchProgressTracker.buildScore();
    }

    public List<PointResult> getPointResults() {
        return pointResults;
    }

    public Side getLastGameResult() {
        assert lastGameResult != null;
        return lastGameResult;
    }

    public Knight getLastGameWinner() {
        return getLastGameResult() == Side.HOME ? match.getHomePlayer() : match.getAwayPlayer();
    }

    public Pitch getPitch() {
        return matchEngine.getPitch();
    }

    public State proceed() {
        if (matchProgressTracker.isSetFinished()) {
            matchProgressTracker.startNewSet();
        }

        matchEngine.next();
        matchTime += MatchEngine.TIME_STEP_MILLIS;
        if (!matchEngine.isFinished()) {
            return State.PLAYING;
        } else {
            return performEndOfGameActions();
        }
    }

    private State performEndOfGameActions() {
        lastGameResult = matchEngine.getWinningSide();
        matchProgressTracker.addPoint(lastGameResult);
        pointResults.add(new PointResult(lastGameResult, matchTime - gameStartTime));
        gameStartTime = matchTime;
        matchEngine.performEndOfGameActions();

        if (!matchProgressTracker.isSetFinished()) {
            matchEngine.reset(matchProgressTracker.getNextServingSide());
            return State.GAME_ENDED;
        } else {
            return performEndOfSetActions();
        }
    }

    private State performEndOfSetActions() {
        matchEngine.performEndOfSetActions();
        if (!matchProgressTracker.isMatchFinished()) {
            matchEngine.reset(matchProgressTracker.getNextServingSide());
            return State.SET_ENDED;
        } else {
            return State.MATCH_ENDED;
        }
    }
}

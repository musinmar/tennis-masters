/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm.lib.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatchSimulator {

    private final Match match;
    private final MatchEngine matchEngine;
    private final MatchProgressTracker matchProgressTracker;
    private final MatchReplay matchReplay;
    private long matchTime;
    private long gameStartTime;
    private Side lastGameResult;
    private final List<PointResult> pointResults = new ArrayList<>();

    public enum Status {
        PLAYING,
        GAME_ENDED,
        SET_ENDED,
        MATCH_ENDED
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class PointResult {
        private final Side winningSide;
        private final long time;
    }

    public MatchSimulator(Match match, StrategyHolder strategyHolder) {
        this.match = match;
        matchEngine = new MatchEngine(match, strategyHolder);
        matchProgressTracker = new MatchProgressTracker(match.getSets(), match.isPlayOff());
        matchProgressTracker.startNewSet();
        matchReplay = new MatchReplay(match.getHomePlayer(), match.getAwayPlayer());
        recordGameState();
    }

    public MatchScore getCurrentScore() {
        return matchProgressTracker.buildScore();
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

    public Status proceed() {
        matchTime += MatchEngine.TIME_STEP_MILLIS;
        if (!matchEngine.isFinished()) {
            matchEngine.next();
            recordGameState();
            if (!matchEngine.isFinished()) {
                return Status.PLAYING;
            } else {
                lastGameResult = matchEngine.getWinningSide();
                matchProgressTracker.addPoint(lastGameResult);
                pointResults.add(new PointResult(lastGameResult, matchTime - gameStartTime));
                if (!matchProgressTracker.isSetFinished()) {
                    return Status.GAME_ENDED;
                } else if (!matchProgressTracker.isMatchFinished()) {
                    return Status.SET_ENDED;
                } else {
                    return Status.MATCH_ENDED;
                }
            }
        } else {
            gameStartTime = matchTime;
            if (!matchProgressTracker.isSetFinished()) {
                matchEngine.performEndOfGameActions();
            } else if (!matchProgressTracker.isMatchFinished()) {
                matchProgressTracker.startNewSet();
                matchEngine.performEndOfSetActions();
            } else {
                throw new IllegalStateException("Can't proceed in a finished match");
            }
            matchEngine.reset(matchProgressTracker.getNextServingSide());
            recordGameState();
            return Status.PLAYING;
        }
    }

    private void recordGameState() {
        var pitch = getPitch();
        var state = new GameState(
                matchTime,
                pitch.getHomePlayer().getState().copy(),
                pitch.getAwayPlayer().getState().copy(),
                pitch.getBall().copy(),
                pitch.getLastTouchedBall());
        matchReplay.getStates().add(state);
    }
}

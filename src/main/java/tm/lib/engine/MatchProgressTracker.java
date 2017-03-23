package tm.lib.engine;

import java.util.ArrayList;
import java.util.List;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.BasicScore;

public class MatchProgressTracker {

    private final int setCount;
    private boolean isPlayoff;

    private final List<BasicScore> setScores = new ArrayList<>();
    private BasicScore additionalTimeScore;

    private int currentSet = -1;
    private boolean isAdditionalTime = false;

    public MatchProgressTracker(int setCount, boolean isPlayoff) {
        if (setCount % 2 == 1) {
            throw new IllegalArgumentException("Set count should be an even number");
        }
        this.setCount = setCount;
        this.isPlayoff = isPlayoff;
    }

    public MatchScore buildScore() {
        return new MatchScore(setScores, additionalTimeScore);
    }

    public void startNewSet() {
        if (isMatchFinished()) {
            throw new IllegalStateException("Cannot start a new set: match already finished");
        }

        if (isMatchStarted() && !isSetFinished()) {
            throw new IllegalStateException("Cannot start a new set: previous set is not finished");
        }

        if (currentSet < setCount - 1) {
            ++currentSet;
            setScores.add(BasicScore.of(0, 0));
        } else {
            isAdditionalTime = true;
            additionalTimeScore = BasicScore.of(0, 0);
        }
    }

    public Side getServingSide() {
        if (!isAdditionalTime) {
            return currentSet % 2 == 0 ? Side.HOME : Side.AWAY;
        } else {
            BasicScore currentSetScore = getCurrentSetScore();
            int gameIndex = currentSetScore.v1 + currentSetScore.v2;
            return gameIndex % 2 == 0 ? Side.HOME : Side.AWAY;
        }
    }

    public BasicScore getCurrentSetScore() {
        return !isAdditionalTime ? setScores.get(currentSet) : additionalTimeScore;
    }

    private void setCurrentSetScore(BasicScore setScore) {
        if (!isAdditionalTime) {
            setScores.set(currentSet, setScore);
        } else {
            additionalTimeScore = setScore;
        }
    }

    public void addPoint(Side winningSide) {
        if (!isMatchStarted()) {
            throw new IllegalStateException("Cannot add a point: match is not started yet");
        }

        if (isSetFinished()) {
            throw new IllegalStateException("Cannot add a point: set is already finished");
        }

        BasicScore currentSetScore = getCurrentSetScore();
        int d1 = winningSide == Side.HOME ? 1 : 0;
        int d2 = winningSide == Side.HOME ? 0 : 1;
        BasicScore newSetScore = new BasicScore(currentSetScore.v1 + d1, currentSetScore.v2 + d2);
        setCurrentSetScore(newSetScore);
    }

    public boolean isSetFinished() {
        BasicScore currentSetScore = getCurrentSetScore();
        if (!isPlayoff || !isAdditionalTime) {
            final int MAX_POINTS_IN_SET = MatchScore.BASE_SET_LENGTH / 2 + 1;
            return currentSetScore.v1 == MAX_POINTS_IN_SET || currentSetScore.v2 == MAX_POINTS_IN_SET;
        } else {
            final int MAX_POINTS_IN_ADDITIONAL_TIME_SET = MatchScore.ADDITIONAL_SET_LENGTH / 2;
            return (currentSetScore.v1 > MAX_POINTS_IN_ADDITIONAL_TIME_SET || currentSetScore.v2 > MAX_POINTS_IN_ADDITIONAL_TIME_SET)
                    && Math.abs(currentSetScore.v1 - currentSetScore.v2) >= 2;
        }
    }

    public BasicScore getScoreBySets() {
        BasicScore scoreBySets = BasicScore.of(0, 0);
        for (BasicScore setScore : setScores) {
            scoreBySets = scoreBySets.summedWith(setScore.normalized());
        }
        if (isPlayoff && additionalTimeScore != null) {
            scoreBySets = scoreBySets.summedWith(additionalTimeScore.normalized());
        }
        return scoreBySets;
    }

    public boolean isMatchStarted() {
        return !setScores.isEmpty();
    }

    public boolean isMatchFinished() {
        BasicScore scoreBySets = getScoreBySets();
        if (!isPlayoff) {
            final int EQUAL_SET_SCORE = setCount / 2;
            return scoreBySets.v1 > EQUAL_SET_SCORE || scoreBySets.v2 > EQUAL_SET_SCORE
                    || scoreBySets.v1 + scoreBySets.v2 == setCount;
        } else {
            final int MAX_SET_SCORE = setCount / 2 + 1;
            return scoreBySets.v1 == MAX_SET_SCORE || scoreBySets.v2 == MAX_SET_SCORE;
        }
    }
}

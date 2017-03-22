package tm.lib.engine;

import java.util.ArrayList;
import java.util.List;
import tm.lib.domain.core.Score;
import tm.lib.domain.core.SetScore;

public class MatchProgressTracker {

    private final int setCount;
    private boolean isPlayoff;

    private final List<SetScore> setScores = new ArrayList<>();
    private SetScore additionalTimeScore;

    private int currentSet = -1;
    private boolean isAdditionalTime = false;

    public MatchProgressTracker(int setCount, boolean isPlayoff) {
        if (setCount % 2 == 1) {
            throw new IllegalArgumentException("Set count should be an even number");
        }
        this.setCount = setCount;
        this.isPlayoff = isPlayoff;
    }

    public Score buildScore() {
        Score score = new Score(setCount);
        score.sets = setScores.toArray(new SetScore[0]);
        score.additionalTime = additionalTimeScore;
        return score;
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
            setScores.add(SetScore.of(0, 0));
        } else {
            isAdditionalTime = true;
            additionalTimeScore = SetScore.of(0, 0);
        }
    }

    public Side getServingSide() {
        if (!isAdditionalTime) {
            return currentSet % 2 == 0 ? Side.HOME : Side.AWAY;
        } else {
            SetScore currentSetScore = getCurrentSetScore();
            int gameIndex = currentSetScore.v1 + currentSetScore.v2;
            return gameIndex % 2 == 0 ? Side.HOME : Side.AWAY;
        }
    }

    public SetScore getCurrentSetScore() {
        return !isAdditionalTime ? setScores.get(currentSet) : additionalTimeScore;
    }

    private void setCurrentSetScore(SetScore setScore) {
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

        SetScore currentSetScore = getCurrentSetScore();
        int d1 = winningSide == Side.HOME ? 1 : 0;
        int d2 = winningSide == Side.HOME ? 0 : 1;
        SetScore newSetScore = new SetScore(currentSetScore.v1 + d1, currentSetScore.v2 + d2);
        setCurrentSetScore(newSetScore);
    }

    public boolean isSetFinished() {
        SetScore currentSetScore = getCurrentSetScore();
        if (!isPlayoff || !isAdditionalTime) {
            final int MAX_POINTS_IN_SET = Score.BASE_SET_LENGTH / 2 + 1;
            return currentSetScore.v1 == MAX_POINTS_IN_SET || currentSetScore.v2 == MAX_POINTS_IN_SET;
        } else {
            final int MAX_POINTS_IN_ADDITIONAL_TIME_SET = Score.ADDITIONAL_SET_LENGTH / 2;
            return (currentSetScore.v1 > MAX_POINTS_IN_ADDITIONAL_TIME_SET || currentSetScore.v2 > MAX_POINTS_IN_ADDITIONAL_TIME_SET)
                    && Math.abs(currentSetScore.v1 - currentSetScore.v2) >= 2;
        }
    }

    public SetScore getScoreBySets() {
        SetScore scoreBySets = SetScore.of(0, 0);
        for (SetScore setScore : setScores) {
            SetScore normalizedSetScore = setScore.normalized();
            scoreBySets = SetScore.of(scoreBySets.v1 + normalizedSetScore.v1, scoreBySets.v2 + normalizedSetScore.v2);
        }
        if (isPlayoff && additionalTimeScore != null) {
            SetScore normalizedSetScore = additionalTimeScore.normalized();
            scoreBySets = SetScore.of(scoreBySets.v1 + normalizedSetScore.v1, scoreBySets.v2 + normalizedSetScore.v2);
        }
        return scoreBySets;
    }

    public boolean isMatchStarted() {
        return !setScores.isEmpty();
    }

    public boolean isMatchFinished() {
        SetScore scoreBySets = getScoreBySets();
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

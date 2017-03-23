package tm.lib.domain.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Score {

    public static final int BASE_SET_LENGTH = 9;
    public static final int ADDITIONAL_SET_LENGTH = 6;

    private final List<SetScore> sets;
    private final SetScore additionalTime;

    public Score(List<SetScore> sets, SetScore additionalTime) {
        this.sets = Collections.unmodifiableList(new ArrayList<>(sets));
        this.additionalTime = additionalTime;
    }

    public Score(Score other) {
        this(other.sets, other.additionalTime);
    }

    public List<SetScore> getSets() {
        return sets;
    }

    public SetScore getAdditionalTime() {
        return additionalTime;
    }

    public int getSetCount() {
        return sets.size();
    }

    public boolean isFirstPlayerWinner() {
        SetScore scoreBySets = getScoreBySets();
        return scoreBySets.v1 > scoreBySets.v2;
    }

    public boolean isDraw() {
        SetScore scoreBySets = getScoreBySets();
        return scoreBySets.v1 == scoreBySets.v2;
    }

    public Score reversed() {
        List<SetScore> reversedSetScores = sets.stream().map(SetScore::reversed).collect(Collectors.toList());
        SetScore reversedAdditionalTime = (additionalTime != null) ? additionalTime.reversed() : null;
        return new Score(reversedSetScores, reversedAdditionalTime);
    }

    @Override
    public String toString() {
        String buf = "";
        buf += sets.get(0);
        for (int i = 1; i < sets.size(); i++) {
            buf += " / " + sets.get(i);
        }
        if (additionalTime != null) {
            buf += " / д.в. " + additionalTime;
        }
        return buf;
    }

    public SetScore getScoreBySets() {
        SetScore scoreBySets = SetScore.of(0, 0);
        for (SetScore setScore : sets) {
            scoreBySets = scoreBySets.summedWith(setScore.normalized());
        }
        if (additionalTime != null) {
            scoreBySets = scoreBySets.summedWith(additionalTime.normalized());
        }
        return scoreBySets;
    }

    public SetScore getScoreByGames() {
        SetScore scoreByGames = SetScore.of(0, 0);
        for (SetScore setScore : sets) {
            scoreByGames = scoreByGames.summedWith(setScore);
        }
        if (additionalTime != null) {
            scoreByGames = scoreByGames.summedWith(additionalTime);
        }
        return scoreByGames;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.sets);
        hash = 97 * hash + Objects.hashCode(this.additionalTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Score other = (Score) obj;
        if (!Objects.equals(this.sets, other.sets)) {
            return false;
        }
        if (!Objects.equals(this.additionalTime, other.additionalTime)) {
            return false;
        }
        return true;
    }
    
    
}

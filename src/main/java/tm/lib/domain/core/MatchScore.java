package tm.lib.domain.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Representation of a match result (score).
 */
public class MatchScore {

    public static final int BASE_SET_LENGTH = 9;
    public static final int ADDITIONAL_SET_LENGTH = 6;

    private final List<BasicScore> sets;
    private final BasicScore additionalTime;

    public MatchScore(List<BasicScore> sets, BasicScore additionalTime) {
        this.sets = Collections.unmodifiableList(new ArrayList<>(sets));
        this.additionalTime = additionalTime;
    }

    public MatchScore(MatchScore other) {
        this(other.sets, other.additionalTime);
    }

    public List<BasicScore> getSets() {
        return sets;
    }

    public BasicScore getAdditionalTime() {
        return additionalTime;
    }

    public int getSetCount() {
        return sets.size();
    }

    public boolean isFirstPlayerWinner() {
        BasicScore scoreBySets = getScoreBySets();
        return scoreBySets.v1 > scoreBySets.v2;
    }

    public boolean isDraw() {
        BasicScore scoreBySets = getScoreBySets();
        return scoreBySets.v1 == scoreBySets.v2;
    }

    public MatchScore reversed() {
        List<BasicScore> reversedSetScores = sets.stream().map(BasicScore::reversed).collect(Collectors.toList());
        BasicScore reversedAdditionalTime = (additionalTime != null) ? additionalTime.reversed() : null;
        return new MatchScore(reversedSetScores, reversedAdditionalTime);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            builder.append(" / ").append(sets.get(i));
        }
        if (additionalTime != null) {
            builder.append(" / д.в. ").append(additionalTime);
        }
        builder.append(" (").append(getScoreBySets()).append(")");
        return builder.toString();
    }

    public BasicScore getScoreBySets() {
        BasicScore scoreBySets = sets.stream()
                .map(BasicScore::normalized)
                .reduce(BasicScore.of(0, 0), BasicScore::summedWith);
        if (additionalTime != null) {
            scoreBySets = scoreBySets.summedWith(additionalTime.normalized());
        }
        return scoreBySets;
    }

    public BasicScore getScoreByGames() {
        BasicScore scoreByGames = sets.stream()
                .reduce(BasicScore.of(0, 0), BasicScore::summedWith);
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
        final MatchScore other = (MatchScore) obj;
        if (!Objects.equals(this.sets, other.sets)) {
            return false;
        }
        if (!Objects.equals(this.additionalTime, other.additionalTime)) {
            return false;
        }
        return true;
    }
    
    
}

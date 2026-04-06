package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Participant;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.MatchScore;

@Getter
@Setter
public class GroupResult {
    private final Participant participant;

    private int matchesPlayed;
    private int points;
    private int gamesWon;
    private int gamesLost;

    public GroupResult(Participant participant) {
        this.participant = participant;
    }

    public void applyMatchResult(MatchScore score) {
        matchesPlayed += 1;
        if (score.isFirstPlayerWinner()) {
            points += 2;
        } else {
            if (score.isDraw()) {
                points += 1;
            }
        }

        BasicScore totalScore = score.getScoreByGames();
        gamesWon += totalScore.v1;
        gamesLost += totalScore.v2;
    }
}

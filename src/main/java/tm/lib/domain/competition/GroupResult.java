package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.MatchScore;

class GroupResult {
    private final Participant participant;

    private int matchesPlayed;
    private int points;
    private int gamesWon;
    private int gamesLost;

    public GroupResult(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getPoints() {
        return points;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
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

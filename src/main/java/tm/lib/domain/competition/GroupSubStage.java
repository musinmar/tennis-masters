package tm.lib.domain.competition;

import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Person;
import tm.lib.domain.world.Season;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GroupSubStage extends SimpleCompetition {
    static private class GroupResult implements Comparable<GroupResult> {
        private Participant participant;
        private int matchesPlayed;
        private int points;
        private int gamesWon;
        private int gamesLost;

        public GroupResult() {
            participant = null;
            matchesPlayed = 0;
            points = 0;
            gamesWon = 0;
            gamesLost = 0;
        }

        void setParticipant(Participant participant) {
            this.participant = participant;
        }

        @Override
        public int compareTo(GroupResult other) {
            if (other.points > this.points) {
                return -1;
            } else {
                if (other.points < this.points) {
                    return 1;
                }
            }

            if (other.gamesWon - other.gamesLost > this.gamesWon - this.gamesLost) {
                return -1;
            } else {
                if (other.gamesWon - other.gamesLost < this.gamesWon - this.gamesLost) {
                    return 1;
                }
            }

            if (other.gamesWon > this.gamesWon) {
                return -1;
            } else {
                if (other.gamesWon < this.gamesWon) {
                    return 1;
                }
            }

            return 0;
        }

        public void update(MatchEvent match) {
            assert match.getResult() != null;
            MatchScore score;
            if (match.getHomePlayer() == participant) {
                score = match.getResult();
            } else {
                if (match.getAwayPlayer() == participant) {
                    score = match.getResult().reversed();
                } else {
                    return;
                }
            }

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

        @Override
        public String toString() {
            return String.format("%1$-20s %2$-3d %3$d-%4$-2d %5$3d", participant.getFullNameOrId(),
                    matchesPlayed, gamesWon, gamesLost, points);
        }
    }

    private GroupResult[] results;

    public GroupSubStage(Season season, String name, List<Participant> participants) {
        super(season, name);
        setParticipants(participants);

        List<MatchEvent> matches = Arrays.asList(
                new MatchEvent(this, participants.get(0), participants.get(2), 2, false),
                new MatchEvent(this, participants.get(1), participants.get(3), 2, false),
                new MatchEvent(this, participants.get(0), participants.get(3), 2, false),
                new MatchEvent(this, participants.get(1), participants.get(2), 2, false),
                new MatchEvent(this, participants.get(0), participants.get(1), 2, false),
                new MatchEvent(this, participants.get(2), participants.get(3), 2, false));
        setMatches(matches);

        results = new GroupResult[participants.size()];
        for (int i = 0; i < results.length; ++i) {
            results[i] = new GroupResult();
            results[i].setParticipant(participants.get(i));
        }
    }

    @Override
    public void print(PrintStream stream) {
        super.print(stream);

        stream.println();
        for (int i = 0; i < results.length; ++i) {
            stream.printf("%1$-2d %2$s", i + 1, results[i].toString());
            stream.println();
        }
    }

    public List<Person> getResults() {
        return Arrays.stream(results)
                .map(groupResult -> groupResult.participant.getPlayer())
                .collect(Collectors.toList());
    }

    @Override
    public void setStartingDate(int date) {
        List<MatchEvent> matches = getAllMatches();
        matches.get(0).setDate(date);
        matches.get(1).setDate(date);
        matches.get(2).setDate(date + 1);
        matches.get(3).setDate(date + 1);
        matches.get(4).setDate(date + 2);
        matches.get(5).setDate(date + 2);
    }

    @Override
    public void onMatchEnded(MatchEvent match) {
        for (GroupResult groupResult : results) {
            groupResult.update(match);
        }
        Arrays.sort(results, Collections.reverseOrder());
        super.onMatchEnded(match);
    }
}

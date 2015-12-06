package tm.lib.domain;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroupSubStage extends SimpleCompetition
{
    static private class GroupResult implements Comparable<GroupResult>
    {
        private Person player;
        private int matchesPlayed;
        private int points;
        private int gamesWon;
        private int gamesLost;

        public GroupResult()
        {
            player = null;
            matchesPlayed = 0;
            points = 0;
            gamesWon = 0;
            gamesLost = 0;
        }

        void setPlayer(Person player)
        {
            this.player = player;
        }

        @Override
        public int compareTo(GroupResult other)
        {
            if (other.points > this.points)
            {
                return -1;
            }
            else
            {
                if (other.points < this.points)
                {
                    return 1;
                }
            }

            if (other.gamesWon - other.gamesLost > this.gamesWon - this.gamesLost)
            {
                return -1;
            }
            else
            {
                if (other.gamesWon - other.gamesLost < this.gamesWon - this.gamesLost)
                {
                    return 1;
                }
            }

            if (other.gamesWon > this.gamesWon)
            {
                return -1;
            }
            else
            {
                if (other.gamesWon < this.gamesWon)
                {
                    return 1;
                }
            }

            return 0;
        }

        public void update(Match match)
        {
            assert match.getResult() != null;
            Score score;
            if (match.getFirstPlayer() == player)
            {
                score = match.getResult();
            }
            else
            {
                if (match.getSecondPlayer() == player)
                {
                    score = match.getResult().reverse();
                }
                else
                {
                    return;
                }
            }

            matchesPlayed += 1;
            if (score.isFirstPlayerWinner())
            {
                points += 2;
            }
            else
            {
                if (score.isDraw())
                {
                    points += 1;
                }
            }

            SetScore totalScore = score.getGameScore();
            gamesWon += totalScore.v1;
            gamesLost += totalScore.v2;
        }

        @Override
        public String toString()
        {
            return String.format("%1$-20s %2$-3d %3$d-%4$-2d %5$3d", player.getFullName(),
                    matchesPlayed, gamesWon, gamesLost, points);
        }
    }

    private GroupResult[] results;

    public GroupSubStage(Competition parentCompetition, Person[] players)
    {
        super(parentCompetition);
        setParticipants(players);

        List<Match> matches = new ArrayList<>(6);
        Match match = new Match(this, players[0], players[2], 2, false);
        matches.add(match);
        match = new Match(this, players[1], players[3], 2, false);
        matches.add(match);
        match = new Match(this, players[0], players[3], 2, false);
        matches.add(match);
        match = new Match(this, players[1], players[2], 2, false);
        matches.add(match);
        match = new Match(this, players[0], players[1], 2, false);
        matches.add(match);
        match = new Match(this, players[2], players[3], 2, false);
        matches.add(match);
        setMatches(matches);

        results = new GroupResult[players.length];
        for (int i = 0; i < results.length; ++i)
        {
            results[i] = new GroupResult();
            results[i].setPlayer(players[i]);
        }
    }

    @Override
    public void print(PrintStream stream)
    {
        super.print(stream);

        stream.println();
        for (int i = 0; i < results.length; ++i)
        {
            stream.printf("%1$-2d %2$s", i + 1, results[i].toString());
            stream.println();
        }
    }

    @Override
    public Person[] getPositions()
    {
        Person[] positions = new Person[results.length];
        int i = 0;
        for (GroupResult groupResult : results)
        {
            positions[i++] = groupResult.player;
        }
        return positions;
    }

    @Override
    public void setStartingDate(int date)
    {
        List<Match> matches = getAllMatches();
        matches.get(0).setDate(date);
        matches.get(1).setDate(date);
        matches.get(2).setDate(date + 1);
        matches.get(3).setDate(date + 1);
        matches.get(4).setDate(date + 2);
        matches.get(5).setDate(date + 2);
    }

    @Override
    public void onMatchEnded(Match match)
    {
        for (GroupResult groupResult : results)
        {
            groupResult.update(match);
        }
        Arrays.sort(results, Collections.reverseOrder());
        super.onMatchEnded(match);
    }
}

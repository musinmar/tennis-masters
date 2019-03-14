package tm.lib.domain.competition;

import tm.lib.domain.core.Person;
import tm.lib.domain.core.Stadium;
import tm.lib.domain.world.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SeasonCompetition extends MultiStageCompetition {
    private static final int TOURNAMENT_COUNT = 1;

    public SeasonCompetition(Season season, List<Person> players) {
        super(season);
        setParticipants(players);

        Competition[] tournaments = new Competition[TOURNAMENT_COUNT];

        for (int i = 0; i < TOURNAMENT_COUNT; ++i) {
            List<Person> allPlayers = new ArrayList<>(players);
            Random r = new Random(System.currentTimeMillis());
            Collections.shuffle(allPlayers, r);
            List<Person> tournamentPlayers = allPlayers.subList(0, 8);
            Competition tournament = new StandardTournament(this, tournamentPlayers);
            tournament.setVenue(Stadium.test_stadium());
            tournament.setStartingDate(8 * i);
            tournaments[i] = tournament;
        }

        initParticipants();
        setStages(tournaments);
    }

    @Override
    public List<Person> getPositions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartingDate(int date) {
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        super.onCompetitionEnded(competition);
    }

    private void initParticipants() {
        //Person[][] tournamentPlayers = new Person[TOURNAMENT_COUNT][8];
        //Person[] players = getParticipants();

        //tournamentPlayers[0][0] = players[0];
        //tournamentPlayers[5][0] = players[1];
        /*boolean done = false;
         while (!done)
         {
         for (int i = 0; i < players.length; ++i)
         {
                

         }
         }*/
    }
}

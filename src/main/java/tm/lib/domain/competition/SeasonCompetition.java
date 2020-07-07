package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupSubStage;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;
import tm.lib.domain.world.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SeasonCompetition extends MultiStageCompetition {

    Map<Nation, Competition> nationalCups = new HashMap<>();

    public SeasonCompetition(Season season, String name, List<Knight> players) {
        super(season, name);

        //setParticipants(players);

        List<Competition> tournaments = new ArrayList<>();

        for (Nation nation : Nation.values()) {
            List<Knight> nationKnights = players.stream().filter(p -> p.getNation() == nation).collect(toList());
            GroupSubStage nationalCup = new GroupSubStage(season, "Кубок " + nation.getNameGenitive(), nationKnights.size());
            nationalCup.setActualParticipants(nationKnights);
            nationalCup.setStartingDate(nation.ordinal() * 8);
            nationalCups.put(nation, nationalCup);
            tournaments.add(nationalCup);
        }

//        for (int i = 0; i < TOURNAMENT_COUNT; ++i) {
//            List<Knight> allPlayers = new ArrayList<>(players);
//            Collections.shuffle(allPlayers);
//            List<Knight> tournamentPlayers = allPlayers.subList(0, 8);
//            Competition tournament = new StandardTournament(season, tournamentPlayers, i + 1);
//            tournament.setVenue(Stadium.standard());
//            tournament.setStartingDate(8 * i);
//            tournaments.add(tournament);
//        }

//        for (int i = 0; i < 10; ++i) {
//            WorldCupCompetition worldCupCompetition = new WorldCupCompetition(season);
//            ArrayList<Knight> worldCupParticipants = new ArrayList<>(players);
//            Collections.shuffle(worldCupParticipants);
//            worldCupCompetition.setActualParticipants(worldCupParticipants);
//            worldCupCompetition.setVenue(Stadium.standard());
//            worldCupCompetition.setStartingDate(i * 20);
//            tournaments.add(worldCupCompetition);
//        }


        initStages(tournaments);
    }

    @Override
    public void setStartingDate(int date) {
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        super.onCompetitionEnded(competition);
    }

}

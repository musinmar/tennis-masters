package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupSubStage;
import tm.lib.domain.competition.standard.PlayoffSubStageResult;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;
import tm.lib.domain.world.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static tm.lib.domain.competition.standard.PlayoffUtils.drawPlayersInPairs;
import static tm.lib.domain.core.Nation.OBERON_22;

public class SeasonCompetition extends MultiStageCompetition {

    private final GameWorld gameWorld;

    private final Map<Nation, GroupSubStage> nationalCups = new HashMap<>();
    private final ChampionsLeagueCompetition championsLeagueCompetition;

    public SeasonCompetition(String name, GameWorld gameWorld, List<Knight> players) {
        super(name);

        this.gameWorld = gameWorld;
        //setParticipants(players);

        List<Competition> tournaments = new ArrayList<>();

        for (Nation nation : Nation.values()) {
            List<Knight> nationKnights = players.stream().filter(p -> p.getNation() == nation).collect(toList());
            GroupSubStage nationalCup = new GroupSubStage("Кубок " + nation.getNameGenitive(), nationKnights.size());
            nationalCup.setActualParticipants(nationKnights);
            nationalCup.setStartingDate(nation.ordinal() * 8);
            nationalCups.put(nation, nationalCup);
            tournaments.add(nationalCup);
        }

        championsLeagueCompetition = new ChampionsLeagueCompetition();
        championsLeagueCompetition.setStartingDate(40);
        championsLeagueCompetition.getFirstQualifyingStage().addCompetitionEndListener(this);
        championsLeagueCompetition.getSecondQualifyingStage().addCompetitionEndListener(this);

        tournaments.add(championsLeagueCompetition);

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

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    @Override
    public void setStartingDate(int date) {
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        super.onCompetitionEnded(competition);

        if (competition == nationalCups.get(OBERON_22)) {
            initCLFirstQualifyingStage();
        } else if (competition == championsLeagueCompetition.getFirstQualifyingStage()) {
            initCLSecondQualifyingStage();
        } else if (competition == championsLeagueCompetition.getSecondQualifyingStage()) {
            initCLGroupStage();
        }
    }

    private void initCLFirstQualifyingStage() {
        List<Knight> clQualifyingRound1 = new ArrayList<>(asList(
                get_player_from(1, 4),
                get_player_from(3, 3),
                get_player_from(4, 2),
                get_player_from(4, 3),
                get_player_from(5, 1),
                get_player_from(5, 2)));

        clQualifyingRound1 = drawPlayersInPairs(clQualifyingRound1, true);
        championsLeagueCompetition.getFirstQualifyingStage().setActualParticipants(clQualifyingRound1);
    }

    private void initCLSecondQualifyingStage() {
        List<Knight> clQualifyingRound2 = new ArrayList<>(asList(
                get_player_from(1, 3),
                get_player_from(2, 2),
                get_player_from(2, 3),
                get_player_from(3, 2),
                get_player_from(4, 1)));

        PlayoffSubStageResult results = championsLeagueCompetition.getFirstQualifyingStage().getLastRoundResults();
        clQualifyingRound2.addAll(results.getWinners());
        clQualifyingRound2 = drawPlayersInPairs(clQualifyingRound2, true);
        championsLeagueCompetition.getSecondQualifyingStage().setActualParticipants(clQualifyingRound2);
    }

    private void initCLGroupStage() {
        List<Knight> clGroupRound = new ArrayList<>(asList(
                get_player_from(1, 1),
                get_player_from(1, 2),
                get_player_from(2, 1),
                get_player_from(3, 1)));

        PlayoffSubStageResult results = championsLeagueCompetition.getSecondQualifyingStage().getLastRoundResults();
        clGroupRound.addAll(results.getWinners());
        List<List<Knight>> groups = makeCLGroups(clGroupRound);
        championsLeagueCompetition.getGroupStage().setActualParticipantsByGroups(groups);
    }

    private List<List<Knight>> makeCLGroups(List<Knight> knights) {
        List<Knight> sortedKnights = new ArrayList<>(knights);
        gameWorld.getEloRating().sortPersonsByRating(sortedKnights);
        List<Knight> top = new ArrayList<>(sortedKnights.subList(0, 4));
        List<Knight> bottom = new ArrayList<>(sortedKnights.subList(4, 8));
        top = drawPlayersInPairs(top, false);
        bottom = drawPlayersInPairs(bottom, false);
        return asList(
                asList(
                        top.get(0),
                        top.get(1),
                        bottom.get(0),
                        bottom.get(1)),
                asList(
                        top.get(2),
                        top.get(3),
                        bottom.get(2),
                        bottom.get(3)
                ));
    }

    private Knight get_player_from(int rank, int pos) {
        Nation nation = gameWorld.getNationRating().getRankedNation(rank - 1);
        return nationalCups.get(nation).getResults().get(pos - 1);
    }

}

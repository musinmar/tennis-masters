package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.standard.GroupSubStage;
import tm.lib.domain.competition.standard.PlayoffSubStageResult;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Nation;
import tm.lib.domain.world.GameWorld;

import java.util.ArrayList;
import java.util.Collections;
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
    private final ChampionsLeagueCompetition championsLeague;
    private final FederationCupCompetition federationsCup;
    private final WorldCupCompetition worldCupCompetition;

    public SeasonCompetition(String name, GameWorld gameWorld, List<Knight> players) {
        super(name);

        this.gameWorld = gameWorld;

        List<Competition> tournaments = new ArrayList<>();

        for (Nation nation : Nation.values()) {
            List<Knight> nationKnights = players.stream().filter(p -> p.getNation() == nation).collect(toList());
            GroupSubStage nationalCup = new GroupSubStage("Кубок " + nation.getNameGenitive(), nationKnights.size());
            nationalCup.setActualParticipants(nationKnights);
            nationalCups.put(nation, nationalCup);
            tournaments.add(nationalCup);
        }

        nationalCups.get(OBERON_22).registerOnFinishedCallback(this::initCLFirstQualifyingStage);

        championsLeague = new ChampionsLeagueCompetition();
        championsLeague.getFirstQualifyingStage().registerOnFinishedCallback(this::onCLFirstQualifyingStageFinished);
        championsLeague.getSecondQualifyingStage().registerOnFinishedCallback(this::onCLSecondQualifyingStageFinished);
        championsLeague.getPlayoffStage().registerOnFinishedCallback(this::onCLPlayoffFinished);

        federationsCup = new FederationCupCompetition();
        federationsCup.getSecondQualifyingStage().registerOnFinishedCallback(this::initFCGroupStage);

        worldCupCompetition = new WorldCupCompetition();

        tournaments.addAll(asList(
                federationsCup,
                championsLeague,
                worldCupCompetition
        ));

        initStages(tournaments);
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public ChampionsLeagueCompetition getChampionsLeague() {
        return championsLeague;
    }

    public FederationCupCompetition getFederationsCup() {
        return federationsCup;
    }

    @Override
    public void setStartingDate(int date) {
        for (Nation nation : Nation.values()) {
            GroupSubStage nationalCup = nationalCups.get(nation);
            nationalCup.setStartingDate(date + nation.ordinal() * 8);
        }

        championsLeague.getFirstQualifyingStage().setStartingDateAfter(nationalCups.get(OBERON_22));
        federationsCup.getFirstQualifyingStage().setStartingDateAfter(championsLeague.getFirstQualifyingStage());
        championsLeague.getSecondQualifyingStage().setStartingDateAfter(federationsCup.getFirstQualifyingStage());
        federationsCup.getSecondQualifyingStage().setStartingDateAfter(championsLeague.getSecondQualifyingStage());
        federationsCup.getGroupStage().setStartingDateAfter(federationsCup.getSecondQualifyingStage());
        championsLeague.getGroupStage().setStartingDateAfter(federationsCup.getGroupStage());
        federationsCup.getPlayoffSemifinals().setStartingDateAfter(championsLeague.getGroupStage());
        championsLeague.getPlayoffSemifinals().setStartingDateAfter(federationsCup.getPlayoffSemifinals());
        federationsCup.getPlayoffFinal().setStartingDateAfter(championsLeague.getPlayoffSemifinals());
        championsLeague.getPlayoffFinal().setStartingDateAfter(federationsCup.getPlayoffFinal());
        worldCupCompetition.setStartingDateAfter(championsLeague.getPlayoffFinal());
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
        championsLeague.getFirstQualifyingStage().setActualParticipants(clQualifyingRound1);
    }

    private void onCLFirstQualifyingStageFinished() {
        List<Knight> clQualifyingRound2 = new ArrayList<>(asList(
                get_player_from(1, 3),
                get_player_from(2, 2),
                get_player_from(2, 3),
                get_player_from(3, 2),
                get_player_from(4, 1)));

        PlayoffSubStageResult results = championsLeague.getFirstQualifyingStage().getResults();
        clQualifyingRound2.addAll(results.getWinners());
        clQualifyingRound2 = drawPlayersInPairs(clQualifyingRound2, true);
        championsLeague.getSecondQualifyingStage().setActualParticipants(clQualifyingRound2);

        List<Knight> fcQualifyingRound1 = new ArrayList<>(Collections.singletonList(
                get_player_from(1, 5)));

        fcQualifyingRound1.addAll(results.getLosers());
        fcQualifyingRound1 = drawPlayersInPairs(fcQualifyingRound1, true);
        federationsCup.getFirstQualifyingStage().setActualParticipants(fcQualifyingRound1);
    }

    private void onCLSecondQualifyingStageFinished() {
        List<Knight> clGroupRound = new ArrayList<>(asList(
                get_player_from(1, 1),
                get_player_from(1, 2),
                get_player_from(2, 1),
                get_player_from(3, 1)));

        PlayoffSubStageResult clSecondQualifyingStageResults = championsLeague.getSecondQualifyingStage().getResults();
        clGroupRound.addAll(clSecondQualifyingStageResults.getWinners());
        List<List<Knight>> groups = makeCLGroups(clGroupRound);
        championsLeague.getGroupStage().setActualParticipantsByGroups(groups);

        List<Knight> fcQualifyingRound2 = new ArrayList<>(asList(
                get_player_from(2, 5),
                get_player_from(3, 5),
                get_player_from(4, 4),
                get_player_from(5, 4)));

        PlayoffSubStageResult fcFirstQualifyingStageResults = federationsCup.getFirstQualifyingStage().getResults();
        fcQualifyingRound2.addAll(clSecondQualifyingStageResults.getLosers());
        fcQualifyingRound2.addAll(fcFirstQualifyingStageResults.getWinners());
        fcQualifyingRound2 = drawPlayersInPairs(fcQualifyingRound2, true);
        federationsCup.getSecondQualifyingStage().setActualParticipants(fcQualifyingRound2);
    }

    private void initFCGroupStage() {
        List<Knight> fcGroupRound = new ArrayList<>(asList(
                get_player_from(2, 4),
                get_player_from(3, 4),
                get_player_from(5, 3)));

        PlayoffSubStageResult results = federationsCup.getSecondQualifyingStage().getResults();
        fcGroupRound.addAll(results.getWinners());
        List<List<Knight>> groups = makeCLGroups(fcGroupRound);
        federationsCup.getGroupStage().setActualParticipantsByGroups(groups);
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

    private void onCLPlayoffFinished() {
        List<Knight> worldCupParticipants = gameWorld.getEloRating().getPersonsByRating();
        worldCupCompetition.setActualParticipants(worldCupParticipants);
    }
}

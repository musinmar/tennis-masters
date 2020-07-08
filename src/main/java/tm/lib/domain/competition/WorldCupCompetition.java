package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.competition.standard.GroupStage;
import tm.lib.domain.competition.standard.GroupStageResult;
import tm.lib.domain.competition.standard.PlayoffStage;
import tm.lib.domain.competition.standard.PlayoffStageConfiguration;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class WorldCupCompetition extends MultiStageCompetition {

    private final GroupStage firstQualifyingStageGroupRound;
    private final PlayoffStage firstQualifyingStagePlayoff;
    private final GroupStage secondQualifyingStage;
    private final GroupStage groupStage;
    private final PlayoffStage playoffStage;

    private List<Knight> secondQualifyingStageParticipants;
    private ArrayList<Knight> groupRoundParticipants;

    public WorldCupCompetition() {
        super("Чемпионат Мира");

        firstQualifyingStageGroupRound = new GroupStage("Первый квалификационный раунд", 8);

        PlayoffStageConfiguration firstQualifyingRoundPlayoffConfiguration = new PlayoffStageConfiguration();
        firstQualifyingRoundPlayoffConfiguration.setRounds(1);
        firstQualifyingStagePlayoff = new PlayoffStage("Плей-офф первого квалификационного раунда", 4, firstQualifyingRoundPlayoffConfiguration);
        List<Participant> firstQualifyingStagePlayoffParticipants = firstQualifyingStagePlayoff.getParticipants();
        firstQualifyingStagePlayoffParticipants.get(0).setId("A1");
        firstQualifyingStagePlayoffParticipants.get(1).setId("B2");
        firstQualifyingStagePlayoffParticipants.get(2).setId("B1");
        firstQualifyingStagePlayoffParticipants.get(3).setId("A2");

        secondQualifyingStage = new GroupStage("Второй квалификационный раунд", 16);
        groupStage = new GroupStage("Групповой раунд", 16);
        playoffStage = new PlayoffStage("Плей-офф", 8);

        initStages(Arrays.asList(
                firstQualifyingStageGroupRound,
                firstQualifyingStagePlayoff,
                secondQualifyingStage,
                groupStage,
                playoffStage));
    }

    @Override
    public void setActualParticipants(List<Knight> players) {
        firstQualifyingStageGroupRound.setActualParticipants(players.subList(22, 30));
        secondQualifyingStageParticipants = newArrayList(players.subList(8, 22));
        groupRoundParticipants = newArrayList(players.subList(0, 8));
    }

    @Override
    public void onCompetitionEnded(Competition competition) {
        super.onCompetitionEnded(competition);

        if (competition == firstQualifyingStageGroupRound) {
            GroupStageResult results = firstQualifyingStageGroupRound.getResults();
            List<Knight> playoffParticipants = Arrays.asList(
                    results.getGroupPosition(0, 0),
                    results.getGroupPosition(1, 1),
                    results.getGroupPosition(1, 0),
                    results.getGroupPosition(0, 1)
            );
            firstQualifyingStagePlayoff.setActualParticipants(playoffParticipants);
        } else if (competition == firstQualifyingStagePlayoff) {
            List<Knight> winners = firstQualifyingStagePlayoff.getLastRoundResults().getWinners();
            secondQualifyingStageParticipants.addAll(winners);
            List<List<Knight>> groups = performPotBasedDraw(secondQualifyingStageParticipants);
            secondQualifyingStage.setActualParticipantsByGroups(groups);
        } else if (competition == secondQualifyingStage) {
            GroupStageResult results = secondQualifyingStage.getResults();
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 4; j++) {
                    groupRoundParticipants.add(results.getGroupPosition(j, i));
                }
            }
            List<List<Knight>> groups = performPotBasedDraw(groupRoundParticipants);
            groupStage.setActualParticipantsByGroups(groups);
        } else if (competition == groupStage) {
            GroupStageResult results = groupStage.getResults();
            List<Knight> playoffParticipants = Arrays.asList(
                    results.getGroupPosition(0, 0),
                    results.getGroupPosition(1, 1),
                    results.getGroupPosition(2, 0),
                    results.getGroupPosition(3, 1),
                    results.getGroupPosition(1, 0),
                    results.getGroupPosition(0, 1),
                    results.getGroupPosition(3, 0),
                    results.getGroupPosition(2, 1)
            );
            playoffStage.setActualParticipants(playoffParticipants);
        }
    }

    private static List<List<Knight>> performPotBasedDraw(List<Knight> players) {
        List<List<Knight>> pots = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            pots.add(new ArrayList<>());
        }
        for (int i = 0; i < players.size(); i++) {
            pots.get(i / 4).add(players.get(i));
        }
        for (int i = 0; i < 4; ++i) {
            Collections.shuffle(pots.get(i));
        }
        List<List<Knight>> groups = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            final int groupIndex = i;
            List<Knight> group = pots.stream().map(pot -> pot.get(groupIndex)).collect(Collectors.toList());
            groups.add(group);
        }
        return groups;
    }
}

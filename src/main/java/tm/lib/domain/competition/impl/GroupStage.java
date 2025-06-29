package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupStage extends MultiStageCompetition {
    private int groupCount;
    private int playersPerGroup;

    public GroupStage() {
    }

    public GroupStage(String id, String name, int groupCount, int playersPerGroup) {
        super(id, name);
        this.groupCount = groupCount;
        this.playersPerGroup = playersPerGroup;

        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            String groupId = String.valueOf((char) ('A' + i));
            GroupCompetition group = new GroupCompetition(groupId, "Group " + groupId, playersPerGroup);
            groups.add(group);
        }
        setStages(groups);
    }

    public int getParticipantCount() {
        return groupCount * playersPerGroup;
    }

    private static List<Competition> buildGroups(int groupCount, int playersInGroup) {
        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            String groupId = String.valueOf((char) ('A' + i));
            GroupCompetition group = new GroupCompetition(groupId, "Group " + groupId, playersInGroup);
            groups.add(group);
        }
        return groups;
    }

    public GroupStageResult getResults() {
        return new GroupStageResult(getStages().stream()
                .map(stage -> ((GroupCompetition) stage).getResults())
                .collect(Collectors.toList()));
    }

    public void setActualParticipantsByGroups(List<List<Knight>> players) {
        var groups = getGroups();
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setActualParticipants(players.get(i));
        }
    }

    public void setActualParticipants(List<Knight> players) {
        int groupSize = getGroups().getFirst().getParticipants().size();
        var grouped = ListUtils.partition(players, groupSize);
        setActualParticipantsByGroups(grouped);
    }

    @SuppressWarnings("unchecked")
    public List<GroupCompetition> getGroups() {
        return (List<GroupCompetition>) (List<?>) getStages();
    }

    @Override
    public Knight getWinner() {
        if (getStages().size() == 1) {
            return super.getWinner();
        } else {
            throw new IllegalStateException("Can not determine single winner of a multi-group group stage");
        }
    }

}

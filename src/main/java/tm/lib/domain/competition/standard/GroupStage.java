package tm.lib.domain.competition.standard;

import lombok.Getter;
import org.apache.commons.collections4.ListUtils;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupStage extends MultiStageCompetition {
    private final int groupCount;
    private final int playersPerGroup;

    public GroupStage(String id, String name, int groupCount, int playersPerGroup) {
        super(id, name);
        this.groupCount = groupCount;
        this.playersPerGroup = playersPerGroup;

        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            String groupId = String.valueOf((char) ('A' + i));
            GroupSubStage group = new GroupSubStage(groupId, "Группа " + groupId, playersPerGroup);
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
            GroupSubStage group = new GroupSubStage(groupId, "Группа " + groupId, playersInGroup);
            groups.add(group);
        }
        return groups;
    }

    public GroupStageResult getResults() {
        return new GroupStageResult(getStages().stream()
                .map(stage -> ((GroupSubStage) stage).getResults())
                .collect(Collectors.toList()));
    }

    @Override
    public void setStartingDate(int date) {
        for (Competition stage : getStages()) {
            stage.setStartingDate(date);
        }
    }

    public void setActualParticipantsByGroups(List<List<Knight>> players) {
        var groups = getGroupSubStages();
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setActualParticipants(players.get(i));
        }
    }

    public void setActualParticipants(List<Knight> players) {
        int groupSize = getGroupSubStages().getFirst().getParticipants().size();
        var grouped = ListUtils.partition(players, groupSize);
        setActualParticipantsByGroups(grouped);
    }

    @SuppressWarnings("unchecked")
    public List<GroupSubStage> getGroupSubStages() {
        return (List<GroupSubStage>) (List<?>) getStages();
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

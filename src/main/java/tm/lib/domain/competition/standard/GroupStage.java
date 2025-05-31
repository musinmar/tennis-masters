package tm.lib.domain.competition.standard;

import org.apache.commons.collections4.ListUtils;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupStage extends MultiStageCompetition {
    public GroupStage(String id, String name, int playerCount) {
        super(id, name);

        int groupCount = playerCount / 4;
        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            String groupId = String.valueOf((char) ('A' + i));
            GroupSubStage group = new GroupSubStage(groupId, "Группа " + groupId, 4);
            groups.add(group);
        }
        initStages(groups);
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

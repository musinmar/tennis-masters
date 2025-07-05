package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

@Getter
@Setter
public class GroupStage extends AbstractMultiStageCompetition {

    private List<GroupCompetition> groups;

    public GroupStage(String id, String name) {
        super(id, name);
    }

    public void setGroups(List<GroupCompetition> groups) {
        groups.forEach(g -> g.setParent(this));
        this.groups = unmodifiableList(groups);
    }

    @Override
    public List<Competition> getChildren() {
        return new ArrayList<>(getGroups());
    }

    public int getParticipantCount() {
        return getGroups().stream().mapToInt(g -> g.getParticipants().size()).sum();
    }

    public GroupStageResult getResults() {
        return new GroupStageResult(getGroups().stream()
                .map(GroupCompetition::getResults)
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

    @Override
    public Knight getWinner() {
        if (getChildren().size() == 1) {
            return super.getWinner();
        } else {
            throw new IllegalStateException("Can not determine single winner of a multi-group group stage");
        }
    }

}

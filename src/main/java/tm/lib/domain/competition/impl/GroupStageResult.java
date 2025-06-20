package tm.lib.domain.competition.impl;

import lombok.Getter;
import tm.lib.domain.core.Knight;

import java.util.List;

@Getter
public class GroupStageResult {
    private final List<List<Knight>> groupResults;

    public GroupStageResult(List<List<Knight>> groupResults) {
        this.groupResults = groupResults;
    }

    public int getGroupCount() {
        return groupResults.size();
    }

    public Knight getGroupPosition(int group, int place) {
        return groupResults.get(group).get(place);
    }
}

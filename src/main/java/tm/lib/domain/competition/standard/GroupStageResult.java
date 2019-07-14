package tm.lib.domain.competition.standard;

import tm.lib.domain.core.Knight;

import java.util.List;

public class GroupStageResult {
    private final List<List<Knight>> groupResults;

    public GroupStageResult(List<List<Knight>> groupResults) {
        this.groupResults = groupResults;
    }

    public List<List<Knight>> getGroupResults() {
        return groupResults;
    }

    public Knight getGroupPosition(int group, int place) {
        return groupResults.get(group).get(place);
    }
}

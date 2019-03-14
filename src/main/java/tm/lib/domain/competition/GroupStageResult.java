package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.List;

public class GroupStageResult {
    private final List<List<Person>> groupResults;

    public GroupStageResult(List<List<Person>> groupResults) {
        this.groupResults = groupResults;
    }

    public List<List<Person>> getGroupResults() {
        return groupResults;
    }
}

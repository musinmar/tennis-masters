package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupStage extends MultiStageCompetition {
    public GroupStage(Competition parentCompetition, String name, List<Person> players) {
        super(parentCompetition, name);
        setParticipants(players);

        int groupCount = players.size() / 4;
        List<Competition> groups = new ArrayList<>();

        for (int i = 0; i < groupCount; ++i) {
            List<Person> groupPlayers = new ArrayList<>();
            for (int j = 0; j < 4; ++j) {
                groupPlayers.add(players.get(i * 4 + j));
            }
            GroupSubStage group = new GroupSubStage(this, "Группа " + (i + 1), groupPlayers);
            groups.add(group);
        }
        setStages(groups);
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
}

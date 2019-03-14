package tm.lib.domain.competition;

import tm.lib.domain.core.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GroupStage extends MultiStageCompetition {
    public GroupStage(Competition parentCompetition, List<Person> players) {
        super(parentCompetition);
        setName("Групповой этап");
        setParticipants(players);

        int groupCount = players.size() / 4;
        Competition[] groups = new Competition[groupCount];

        for (int i = 0; i < groupCount; ++i) {
            List<Person> groupPlayers = new ArrayList<>();
            for (int j = 0; j < 4; ++j) {
                groupPlayers.add(players.get(i * 4 + j));
            }
            GroupSubStage group = new GroupSubStage(this, groupPlayers);
            group.setName("Группа " + (i + 1));
            groups[i] = group;
        }
        setStages(groups);
    }

    @Override
    public List<Person> getPositions() {
        return Arrays.stream(getStages())
                .flatMap(stage -> stage.getPositions().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void setStartingDate(int date) {
        for (int i = 0; i < getStages().length; i++) {
            getStages()[i].setStartingDate(date);
        }
    }
}

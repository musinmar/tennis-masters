package tm.lib.domain.competition;

import tm.lib.domain.core.Person;
import tm.lib.domain.world.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupStage extends MultiStageCompetition {
    public GroupStage(Season season, String name, int playerCount) {
        super(season, name);
        setParticipants(Participant.createNewList(playerCount));

        int groupCount = playerCount / 4;
        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            List<Participant> groupParticipants = getParticipants().subList(i * 4, i * 4 + 4);
            GroupSubStage group = new GroupSubStage(season, "Группа " + (i + 1), groupParticipants);
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
}

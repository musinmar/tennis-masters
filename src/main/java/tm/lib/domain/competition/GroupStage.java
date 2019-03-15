package tm.lib.domain.competition;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.world.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupStage extends MultiStageCompetition {
    public GroupStage(Season season, String name, int playerCount) {
        super(season, name);

        int groupCount = playerCount / 4;
        List<Competition> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; ++i) {
            GroupSubStage group = new GroupSubStage(season, "Группа " + (i + 1), 4);
            groups.add(group);
        }
        initStages(groups);

        List<Participant> participants = groups.stream()
                .flatMap(group -> group.getParticipants().stream())
                .collect(Collectors.toList());
        setParticipants(participants);
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

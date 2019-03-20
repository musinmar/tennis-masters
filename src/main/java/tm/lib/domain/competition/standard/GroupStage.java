package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.core.Person;
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
            char groupId = (char) ('A' + i);
            GroupSubStage group = new GroupSubStage(season, "Группа " + groupId, 4);
            group.setParticipantPrefix(groupId + "");
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

    public void setActualParticipantsByGroups(List<List<Person>> players) {
        for (int i = 0; i < getStages().size(); i++) {
            getStages().get(i).setActualParticipants(players.get(i));
        }
    }
}

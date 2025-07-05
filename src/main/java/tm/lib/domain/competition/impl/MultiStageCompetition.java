package tm.lib.domain.competition.impl;

import lombok.Getter;
import tm.lib.domain.competition.Competition;

import java.util.List;

import static java.util.Collections.unmodifiableList;

@Getter
public class MultiStageCompetition extends AbstractMultiStageCompetition {

    private List<Competition> stages;

    public MultiStageCompetition(String id, String name) {
        super(id, name);
    }

    public void setStages(List<Competition> stages) {
        stages.forEach(s -> s.setParent(this));
        this.stages = unmodifiableList(stages);
    }
}

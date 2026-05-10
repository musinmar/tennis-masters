package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.CompetitionEvent;
import tm.lib.domain.competition.MatchResult;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Getter
@Setter
abstract public class AbstractCompetition implements Competition {
    private Competition parent;
    private final String id;
    private final String name;
    private List<CompetitionEvent> events = emptyList();
    private List<Knight> assignedParticipants = new ArrayList<>();

    protected AbstractCompetition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void print(PrintStream stream) {
        stream.println(getName());
        stream.println();
    }

    public List<CompetitionEvent> getAllEvents() {
        return getEvents();
    }

    public void assignParticipants(List<Knight> players) {
        assignedParticipants.addAll(players);
    }

    @Override
    public void processMatchResult(MatchEvent match, MatchResult result) {
        assert match.getCompetition() == this && getNextMatch().equals(Optional.of(match));
        match.setResult(result);
        afterMatchResultAssigned(match, result.getScore());
        triggerOnFinishedChainIfNecessary();
    }

    private void triggerOnFinishedChainIfNecessary() {
        Competition current = this;
        while (current.isFinished() && current.getParent() != null) {
            current.getParent().processChildCompetitionFinished(current);
            current = current.getParent();
        }
    }

    @Override
    public void processChildCompetitionFinished(Competition child) {
        // Do nothing by default
    }

    protected void afterMatchResultAssigned(MatchEvent match, MatchScore score) {
    }
}

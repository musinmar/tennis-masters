package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.Participant;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Getter
@Setter
abstract public class SimpleCompetition extends AbstractCompetition {

    private List<Participant> participants;
    private List<MatchEvent> matches;
    private int nextMatchIndex = 0;

    protected SimpleCompetition() {
    }

    protected SimpleCompetition(String id, String name, List<Participant> participants) {
        super(id, name);
        this.participants = participants;
    }

    @Override
    public Optional<Competition> getChild(String id) {
        return Optional.empty();
    }

    protected static List<Participant> createParticipants(String stageParticipantId, int count) {
        return IntStream.range(0, count).mapToObj(i -> new Participant(stageParticipantId + (i + 1))).toList();
    }

    private void setActualParticipants(int fromIndex, List<Knight> players) {
        for (int i = 0; i < players.size(); i++) {
            participants.get(fromIndex + i).setPlayer(players.get(i));
        }
    }

    public void setActualParticipants(List<Knight> players) {
        setActualParticipants(0, players);
    }

    @Override
    public void print(PrintStream stream) {
        super.print(stream);
        for (MatchEvent match : getAllMatches()) {
            stream.println(match.toString());
        }
    }

    @Override
    public Optional<MatchEvent> getNextMatch() {
        if (nextMatchIndex < matches.size()) {
            return Optional.of(matches.get(nextMatchIndex));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<MatchEvent> getAllMatches() {
        return matches;
    }

    @Override
    protected void afterMatchResultAssigned(MatchEvent match, MatchScore score) {
        ++nextMatchIndex;
    }

    public void setStartDate(int date) {
        getAllMatches().forEach(match -> match.setDate(date));
    }
}

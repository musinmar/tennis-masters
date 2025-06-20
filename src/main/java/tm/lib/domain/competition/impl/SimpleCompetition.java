package tm.lib.domain.competition.impl;

import lombok.Getter;
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
abstract public class SimpleCompetition extends AbstractCompetition {

    private final List<Participant> participants;
    private List<MatchEvent> matches;
    private int nextMatchIndex = 0;

    protected SimpleCompetition(String id, String name, List<Participant> participants) {
        super(id, name);
        this.participants = participants;
    }

    @Override
    public Competition getChild(String id) {
        throw new IllegalArgumentException("Competition " + getId() + " is not a multi-stage competition");
    }

    protected int getNextMatchIndex() {
        return nextMatchIndex;
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

    protected void setMatches(List<MatchEvent> matches) {
        this.matches = matches;
    }

    @Override
    public final void processMatchResult(MatchEvent match, MatchScore score) {
        assert getNextMatch().equals(Optional.of(match));
        match.setResult(score);
        doProcessMatchResult(match, score);
        ++nextMatchIndex;
    }

    protected void doProcessMatchResult(MatchEvent match, MatchScore score) {
    }

    @Override
    public int getStartingDate() {
        return getAllMatches().get(0).getDate();
    }

    @Override
    public void setStartingDate(int date) {
        getAllMatches().forEach(match -> match.setDate(date));
    }

    @Override
    public int getLastDate() {
        return getAllMatches().get(getAllMatches().size() - 1).getDate();
    }
}

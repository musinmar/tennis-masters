package tm.lib.domain.competition.base;

import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

abstract public class SimpleCompetition extends Competition {

    private final List<Participant> participants;
    private List<MatchEvent> matches;
    private int nextMatchIndex = 0;

    protected SimpleCompetition(String id, String name, List<Participant> participants) {
        super(id, name);
        this.participants = participants;
    }

    protected int getNextMatchIndex() {
        return nextMatchIndex;
    }

    public List<Participant> getParticipants() {
        return participants;
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
        checkIfCompetitionFinished(this);
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

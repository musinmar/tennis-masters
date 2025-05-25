package tm.lib.domain.competition.base;

import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;

abstract public class SimpleCompetition extends Competition {

    private List<Participant> participants;
    private List<MatchEvent> matches;
    private int nextMatchIndex;

    protected SimpleCompetition(String name, List<Participant> participants) {
        super(name);
        this.participants = participants;
        nextMatchIndex = 0;
    }


    protected int getNextMatchIndex() {
        return nextMatchIndex;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public static List<Participant> createParticipants(String stageParticipantId, int count) {
        return IntStream.range(0, count).mapToObj(i -> new Participant(stageParticipantId + (i + 1))).toList();
    }

    public void setActualParticipants(int fromIndex, List<Knight> players) {
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
    public MatchEvent getNextMatch() {
        if (nextMatchIndex < matches.size()) {
            return matches.get(nextMatchIndex);
        } else {
            return null;
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
        assert match == getNextMatch();
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

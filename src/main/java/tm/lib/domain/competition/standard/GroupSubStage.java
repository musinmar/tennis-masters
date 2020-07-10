package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.competition.base.SimpleCompetition;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

public class GroupSubStage extends SimpleCompetition {

    private List<GroupResult> groupResults;

    public GroupSubStage(String name, int playerCount) {
        super(name);
        setParticipants(Participant.createNewList(playerCount));

        List<MatchEvent> matches = createSchedule(getParticipants());
        setMatches(matches);

        initGroupResults(getParticipants());
    }

    private List<MatchEvent> createSchedule(List<Participant> participants) {
        int size = participants.size();
        List<Participant> ps = getParticipants();
        if (size == 4) {
            return createScheduleFor6(ps);
        } else if (size % 2 == 0) {
            return createScheduleForEven(size, ps);
        } else {
            throw new IllegalArgumentException("Can't create group schedule for " + size + " players");
        }
    }

    private List<MatchEvent> createScheduleForEven(int count, List<Participant> ps) {
        List<MatchEvent> matchEvents = new ArrayList<>();

        int[] buf = new int[count];
        for (int i = 0; i < count; ++i) {
            buf[i] = i;
        }

        int halfCount = count / 2;
        for (int i = 1; i < count; ++i) {
            for (int j = 0; j < halfCount; ++j) {
                matchEvents.add(new MatchEvent(this, ps.get(buf[j]), ps.get(buf[j + halfCount]), 2, false));
            }

            int[] buf2 = new int[count];
            buf2[0] = buf[0];
            buf2[1] = buf[halfCount];
            for (int j = 2; j < halfCount; ++j) {
                buf2[j] = buf[j - 1];
            }
            for (int j = halfCount; j < count - 1; j++) {
                buf2[j] = buf[j + 1];
            }
            buf2[count - 1] = buf[halfCount - 1];
            for (int j = 0; j < count; ++j) {
                buf[j] = buf2[j];
            }
        }

        return matchEvents;
    }

    private List<MatchEvent> createScheduleFor6(List<Participant> ps) {
        return Arrays.asList(
                new MatchEvent(this, ps.get(0), ps.get(2), 2, false),
                new MatchEvent(this, ps.get(1), ps.get(3), 2, false),
                new MatchEvent(this, ps.get(0), ps.get(3), 2, false),
                new MatchEvent(this, ps.get(1), ps.get(2), 2, false),
                new MatchEvent(this, ps.get(0), ps.get(1), 2, false),
                new MatchEvent(this, ps.get(2), ps.get(3), 2, false)
        );
    }

    private void initGroupResults(List<Participant> participants) {
        groupResults = new ArrayList<>();
        for (int i = 0; i < participants.size(); ++i) {
            groupResults.add(new GroupResult(participants.get(i)));
        }
    }

    @Override
    public void print(PrintStream stream) {
        super.print(stream);
        stream.println();
        printGroupResults(stream);
    }

    public String printGroupResultsToString() {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printGroupResults(printStream);
        return outputStream.toString();
    }

    private void printGroupResults(PrintStream stream) {
        int len = groupResults.size();
        int maxNameLength = groupResults.stream()
                .map(r -> r.getParticipant().getFullNameOrId())
                .mapToInt(String::length)
                .max()
                .orElse(0);

        String formatString = "%d. %-" + (maxNameLength + 1) + "s  %-3d %3d:%-3d %3d";
        for (int i = 0; i < len; i++) {
            GroupResult r = groupResults.get(i);
            stream.printf(formatString, (i + 1), r.getParticipant().getFullNameOrId(),
                    r.getMatchesPlayed(), r.getGamesWon(), r.getGamesLost(), r.getPoints());
            stream.println();
        }
    }

    public List<Knight> getResults() {
        return groupResults.stream()
                .map(groupResult -> groupResult.getParticipant().getPlayer())
                .collect(Collectors.toList());
    }

    @Override
    public void setStartingDate(int date) {
        List<MatchEvent> matches = getAllMatches();
        int roundSize = getParticipants().size() / 2;
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).setDate(date + i / roundSize);
        }
    }

    @Override
    protected void doProcessMatchResult(MatchEvent match, MatchScore score) {
        applyMatchResult(match);
        sortGroupResults();
    }

    private void sortGroupResults() {
        groupResults.sort(reverseOrder(buildGroupResultComparator()));
    }

    private void applyMatchResult(MatchEvent match) {
        GroupResult groupResult1 = findGroupResult(match.getHomePlayer());
        groupResult1.applyMatchResult(match.getResult());
        GroupResult groupResult2 = findGroupResult(match.getAwayPlayer());
        groupResult2.applyMatchResult(match.getResult().reversed());
    }

    private Comparator<GroupResult> buildGroupResultComparator() {
        return Comparator.comparingDouble(GroupResult::getPoints)
                .thenComparingDouble((GroupResult gr) -> gr.getGamesWon() - gr.getGamesLost())
                .thenComparingDouble(GroupResult::getGamesWon);
    }

    private GroupResult findGroupResult(Participant participant) {
        return groupResults.stream()
                .filter(gr -> gr.getParticipant() == participant)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    public boolean doesNextMatchStartNewRound() {
        int ind = getNextMatchIndex();
        int roundSize = getParticipants().size() / 2;
        return ind % roundSize == 0;
    }
}

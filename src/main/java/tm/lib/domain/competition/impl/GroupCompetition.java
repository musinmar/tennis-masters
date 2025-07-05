package tm.lib.domain.competition.impl;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.competition.Participant;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.MatchScore;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class GroupCompetition extends SimpleCompetition {

    private List<GroupResult> groupResults;

    public GroupCompetition(String id, String name) {
        super(id, name);
    }

    public GroupCompetition(String id, String name, int playerCount) {
        super(id, name, createParticipants(id, playerCount));

        List<MatchEvent> matches = createSchedule(getParticipants());
        setMatches(matches);

        groupResults = initGroupResults(getParticipants());
    }

    private List<MatchEvent> createSchedule(List<Participant> participants) {
        int size = participants.size();
        List<Participant> ps = getParticipants();
        if (size == 4) {
            return createScheduleFor6(ps);
        } else if (size % 2 == 0) {
            return createScheduleForEvenPlayerCount(size, ps);
        } else {
            throw new IllegalArgumentException("Can't create group schedule for " + size + " players");
        }
    }

    private List<MatchEvent> createScheduleForEvenPlayerCount(int count, List<Participant> participants) {
        List<MatchEvent> matches = new ArrayList<>();

        for (int round = 0; round < count - 1; round++) {
            for (int match = 0; match < count / 2; match++) {
                int home = (round + match) % (count - 1);
                int away = (round + count - 1 - match) % (count - 1);

                if (match == 0) {
                    home = count - 1;
                } else {
                    if (home >= count - 1) home = 0;
                    if (away >= count - 1) away = 0;
                }

                matches.add(new MatchEvent(this, participants.get(home),
                        participants.get(away), 2, false));
            }
        }

        return matches;
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

    private static List<GroupResult> initGroupResults(List<Participant> participants) {
        return participants.stream().map(GroupResult::new).collect(toList());
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
                .map(groupResult -> groupResult.getParticipant().getPlayerOrFail())
                .collect(toList());
    }

    @Override
    public void setStartDate(int date) {
        List<MatchEvent> matches = getAllMatches();
        int roundSize = getParticipants().size() / 2;
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).setDate(date + i / roundSize);
        }
    }

    @Override
    protected void afterMatchResultAssigned(MatchEvent match, MatchScore score) {
        super.afterMatchResultAssigned(match, score);
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

    @Override
    public Knight getWinner() {
        return getResults().getFirst();
    }
}

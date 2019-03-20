package tm.lib.domain.competition.standard;

import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.Participant;
import tm.lib.domain.competition.base.SimpleCompetition;
import tm.lib.domain.core.Person;
import tm.lib.domain.world.Season;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

public class GroupSubStage extends SimpleCompetition {

    private List<GroupResult> groupResults;

    public GroupSubStage(Season season, String name, int playerCount) {
        super(season, name);
        setParticipants(Participant.createNewList(playerCount));

        List<MatchEvent> matches = Arrays.asList(
                new MatchEvent(this, getParticipants().get(0), getParticipants().get(2), 2, false),
                new MatchEvent(this, getParticipants().get(1), getParticipants().get(3), 2, false),
                new MatchEvent(this, getParticipants().get(0), getParticipants().get(3), 2, false),
                new MatchEvent(this, getParticipants().get(1), getParticipants().get(2), 2, false),
                new MatchEvent(this, getParticipants().get(0), getParticipants().get(1), 2, false),
                new MatchEvent(this, getParticipants().get(2), getParticipants().get(3), 2, false));
        initMatches(matches);

        initGroupResults(getParticipants());
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

    public List<Person> getResults() {
        return groupResults.stream()
                .map(groupResult -> groupResult.getParticipant().getPlayer())
                .collect(Collectors.toList());
    }

    @Override
    public void setStartingDate(int date) {
        List<MatchEvent> matches = getAllMatches();
        matches.get(0).setDate(date);
        matches.get(1).setDate(date);
        matches.get(2).setDate(date + 1);
        matches.get(3).setDate(date + 1);
        matches.get(4).setDate(date + 2);
        matches.get(5).setDate(date + 2);
    }

    @Override
    public void onMatchEnded(MatchEvent match) {
        applyMatchResult(match);
        sortGroupResults();
        super.onMatchEnded(match);
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
}

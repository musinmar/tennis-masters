package tm.lib.domain.competition.base;

import tm.lib.domain.core.Person;

import java.util.ArrayList;
import java.util.List;

public class Participant {
    private String id;
    private Person player;

    public Participant(String id, Person player) {
        this.id = id;
        this.player = player;
    }

    public Participant(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person getPlayer() {
        return player;
    }

    public void setPlayer(Person player) {
        this.player = player;
    }

    public String getFullNameOrId() {
        return player == null ? id : player.getFullName();
    }

    public static List<Participant> createNewList(int count) {
        ArrayList<Participant> participants = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            participants.add(new Participant("ID" + (i + 1)));
        }
        return participants;
    }

    public static List<Participant> createNewList(List<Person> players) {
        ArrayList<Participant> participants = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            participants.add(new Participant("ID" + (i + 1), players.get(i)));
        }
        return participants;
    }
}

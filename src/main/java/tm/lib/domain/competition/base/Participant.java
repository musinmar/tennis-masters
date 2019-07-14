package tm.lib.domain.competition.base;

import tm.lib.domain.core.Knight;

import java.util.ArrayList;
import java.util.List;

public class Participant {
    private String id;
    private Knight player;

    public Participant(String id, Knight player) {
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

    public Knight getPlayer() {
        return player;
    }

    public void setPlayer(Knight player) {
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

    public static List<Participant> createNewList(List<Knight> players) {
        ArrayList<Participant> participants = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            participants.add(new Participant("ID" + (i + 1), players.get(i)));
        }
        return participants;
    }
}

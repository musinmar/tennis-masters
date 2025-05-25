package tm.lib.domain.competition.base;

import tm.lib.domain.core.Knight;

import java.util.List;
import java.util.stream.IntStream;

public class Participant {
    public static final String DEFAULT_STAGE_PARTICIPANT_ID = "ID";

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

    public static List<Participant> createNewList(String stageParticipantId, int count) {
        return IntStream.range(0, count).mapToObj(i -> new Participant(stageParticipantId + (i + 1))).toList();
    }
}

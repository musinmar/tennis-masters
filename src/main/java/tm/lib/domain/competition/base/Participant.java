package tm.lib.domain.competition.base;

import lombok.Getter;
import lombok.Setter;
import tm.lib.domain.core.Knight;

import java.util.Optional;

@Getter
@Setter
public class Participant {
    public static final String DEFAULT_STAGE_PARTICIPANT_ID = "ID";

    private String id;
    private Optional<Knight> player;

    public Participant(String id, Optional<Knight> player) {
        this.id = id;
        this.player = player;
    }

    public Participant(String id) {
        this(id, Optional.empty());
    }

    public void setPlayer(Knight player) {
        this.player = Optional.of(player);
    }

    public Knight getPlayerOrFail() {
        return player.orElseThrow(() -> new IllegalStateException("Player has not yet been assigned"));
    }

    public String getFullNameOrId() {
        return player.map(Knight::getFullName).orElse(id);
    }
}

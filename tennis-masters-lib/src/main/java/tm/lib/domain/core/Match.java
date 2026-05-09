package tm.lib.domain.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Match {
    private Knight homePlayer;
    private Knight awayPlayer;
    private int sets;
    private boolean isPlayOff;
    private Stadium venue;
}

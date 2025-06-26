package tm.lib.domain.world;

import tm.lib.domain.core.Knight;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;

public class KnightRepository {

    private List<Knight> knights = emptyList();
    private Map<Integer, Knight> knightsById = emptyMap();

    public KnightRepository() {
    }

    public void setKnights(List<Knight> knights) {
        this.knights = knights;
        this.knightsById = knights.stream().collect(Collectors.toMap(Knight::getId, identity()));
    }

    public List<Knight> getAll() {
        return knights;
    }

    @Nonnull
    public Knight getById(Integer id) {
        var result = knightsById.get(id);
        if (result == null) {
            throw new IllegalArgumentException("Knight with id=" + id + " does not exist");
        }
        return result;
    }
}

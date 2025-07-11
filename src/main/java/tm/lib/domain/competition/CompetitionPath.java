package tm.lib.domain.competition;

import org.apache.commons.lang3.StringUtils;
import tm.lib.domain.competition.exception.UnresolvableCompetitionPathException;

import java.util.ArrayList;

public class CompetitionPath {
    public static final String PATH_SEPARATOR = "/";
    public static final String PARENT = "..";
    public static final String SELF = "self";

    public static Competition resolveCompetitionPath(Competition competition, String path) {
        var elements = StringUtils.split(path, PATH_SEPARATOR);
        Competition cur = competition;
        for (var el : elements) {
            if (SELF.equals(el)) {
                return cur;
            } else if (PARENT.equals(el)) {
                cur = cur.getParent();
                if (cur == null) {
                    throw new UnresolvableCompetitionPathException("Cannot navigate to parent - already at root competition");
                }
            } else {
                final var curId = cur.getId();
                cur = cur.getChild(el).orElseThrow(() ->
                        new UnresolvableCompetitionPathException("Competition " + curId + " does not have child " + el));
            }
        }
        return cur;
    }

    public static String buildPathTo(Competition from, Competition to) {
        if (from == null) {
            throw new IllegalArgumentException("Can't build path from null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Can't build path to null");
        }
        var buffer = new ArrayList<String>();
        while (to != from && to != null) {
            buffer.add(to.getId());
            to = to.getParent();
        }
        if (to == null) {
            throw new IllegalArgumentException(to.getId() + " is not direct or indirect child of " + from.getId());
        }
        return buildPath(buffer.reversed().toArray(new String[0]));
    }

    public static String buildPath(String... sections) {
        return StringUtils.join(sections, PATH_SEPARATOR);
    }
}
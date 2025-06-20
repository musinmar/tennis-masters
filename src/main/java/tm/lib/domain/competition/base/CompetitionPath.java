package tm.lib.domain.competition.base;

import org.apache.commons.lang3.StringUtils;

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
            } else {
                cur = cur.getChild(el);
            }
        }
        return cur;
    }

    public static String buildPath(String... sections) {
        return StringUtils.join(sections, PATH_SEPARATOR);
    }
}
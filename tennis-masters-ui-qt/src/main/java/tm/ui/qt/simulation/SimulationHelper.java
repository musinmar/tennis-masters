package tm.ui.qt.simulation;

import io.qt.widgets.QWidget;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.StrategyHolder;
import tm.ui.qt.MatchDialog;

public class SimulationHelper {
    public static MatchScore simulateMatch(Match match, StrategyHolder strategyHolder) {
        MatchSimulator matchSimulator = new MatchSimulator(match, strategyHolder);
        MatchSimulator.Status status;
        do {
            status = matchSimulator.proceed();
        } while (status != MatchSimulator.Status.MATCH_ENDED);
        return matchSimulator.getCurrentScore();
    }

    public static MatchScore showMatch(Match match, StrategyHolder strategyHolder, QWidget parent) {
        MatchDialog matchDialog = new MatchDialog(match, strategyHolder, parent);
        matchDialog.exec();
        return matchDialog.getFinalScore();
    }
}

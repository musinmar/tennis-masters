package tm.ui.qt.simulation;

import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.StrategyProvider;
import tm.lib.engine.strategies.NeuralNetworkStrategy;
import tm.lib.engine.strategies.StandardStrategy;
import tm.ui.qt.MatchDialog;

public class SimulationHelper {
    public static MatchScore simulateMatch(Match match, StrategyProvider strategyProvider) {
        MatchSimulator matchSimulator = new MatchSimulator(match, strategyProvider);
        MatchSimulator.State state;
        do {
            state = matchSimulator.proceed();
        } while (state != MatchSimulator.State.MATCH_ENDED);
        return matchSimulator.getCurrentScore();
    }

    public static MatchScore showMatch(Match match, StrategyProvider strategyProvider, QWidget parent) {
        MatchDialog matchDialog = new MatchDialog(match, strategyProvider, parent);
        matchDialog.exec();
        return matchDialog.getFinalScore();
    }
}

package tm.lib.engine;

import com.google.common.collect.ImmutableMap;
import tm.lib.engine.strategies.StandardStrategy;

import java.util.Map;

public class StrategyProvider {
    private final Map<Side, Strategy> strategies;

    public StrategyProvider(Strategy homePlayerStrategy, Strategy awayPlayerStrategy) {
        strategies = ImmutableMap.of(
                Side.HOME, homePlayerStrategy,
                Side.AWAY, awayPlayerStrategy);
    }

    public Strategy getStrategy(Side side) {
        return strategies.get(side);
    }

    public static StrategyProvider standard() {
        return new StrategyProvider(new StandardStrategy(), new StandardStrategy());
    }
}

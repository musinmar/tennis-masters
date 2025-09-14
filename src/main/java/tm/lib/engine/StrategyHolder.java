package tm.lib.engine;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import tm.lib.engine.strategies.ManualStrategy;
import tm.lib.engine.strategies.StandardStrategy;

import java.util.Map;

public class StrategyHolder {
    private final Map<Side, Strategy> strategies;

    public StrategyHolder(Strategy homePlayerStrategy, Strategy awayPlayerStrategy) {
        Validate.isTrue(!(homePlayerStrategy instanceof ManualStrategy && awayPlayerStrategy instanceof ManualStrategy),
                "Can't manually control both home and away player");
        strategies = ImmutableMap.of(
                Side.HOME, homePlayerStrategy,
                Side.AWAY, awayPlayerStrategy
        );
    }

    public Strategy getStrategy(Side side) {
        return strategies.get(side);
    }

    public static StrategyHolder standard() {
        return new StrategyHolder(new StandardStrategy(), new StandardStrategy());
    }
}

package tm.lib.engine;

public record GameState(
        long time,
        PlayerState homePlayer,
        PlayerState awayPlayer,
        BallState ball,
        Side lastTouchedBall
) {
}

package tm.lib.engine;

import tm.lib.domain.core.Person;
import tm.lib.domain.core.Stadium;

public class Pitch {

    private final Player homePlayer;
    private final Player awayPlayer;
    private final Ball ball;
    private final Stadium venue;

    static public final double WIDTH = 500;
    static public final double HEIGHT = 700;
    static public final double HALF_HEIGHT = HEIGHT / 2;
    static public final double PLAYER_SIZE = 15;
    static public final double BALL_SIZE = 6;
    static public final double TARGET_SIZE = 2;
    static public final double FAKE_TARGET_SIZE = 8;

    public Pitch(Person homePlayer, Person awayPlayer, Stadium venue) {
        this.homePlayer = new Player(homePlayer, Side.HOME);
        this.awayPlayer = new Player(awayPlayer, Side.AWAY);
        this.ball = new Ball();
        this.venue = venue;
    }

    public Player getPlayer(Side side) {
        return side == Side.HOME ? homePlayer : awayPlayer;
    }

    public Player getOppositePlayer(Player player) {
        return player == homePlayer ? awayPlayer : homePlayer;
    }

    public Ball getBall() {
        return ball;
    }

    public Stadium getVenue() {
        return venue;
    }
    
    public void setInitialPositions(Side startingSide) {
        Point2d startingPosition = new Point2d(WIDTH / 2, HALF_HEIGHT / 3 * 2);
        Point2d startingDirection = new Point2d(0, -1);
        
        homePlayer.resetState(startingPosition, startingDirection);
        awayPlayer.resetState(VectorUtils.mirror(startingPosition), VectorUtils.mirror(startingDirection));

        Point2d startingPlayerPosition = getPlayer(startingSide).getPosition();
        Point2d startingPlayerDirection = getPlayer(startingSide).getDirection();
        ball.setPosition(startingPlayerPosition.add(startingPlayerDirection.scalarMultiply(10)));
        ball.setRealTarget(startingPlayerPosition.add(startingPlayerDirection.scalarMultiply(50)));
        ball.setFakeTarget(startingPlayerPosition.add(startingPlayerDirection.scalarMultiply(70)));
    }
}

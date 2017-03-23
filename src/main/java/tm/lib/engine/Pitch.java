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
        ball = new Ball();
        this.venue = venue;

        setInitialPositions(Side.HOME);
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
        homePlayer.position.x = WIDTH / 2;
        homePlayer.position.y = HALF_HEIGHT / 3 * 2;
        homePlayer.direction = new Point2d(0, -1);
        homePlayer.speed = 0;
        homePlayer.lying = false;
        awayPlayer.position.x = WIDTH / 2;
        awayPlayer.position.y = -HALF_HEIGHT / 3 * 2;
        awayPlayer.direction = new Point2d(0, 1);
        awayPlayer.speed = 0;
        awayPlayer.lying = false;

        ball.position.set(startingSide == Side.HOME ? homePlayer.position : awayPlayer.position);
        double modifier = startingSide.getModifier();
        ball.position.y -= modifier * 10;
        ball.target.set(ball.position);
        ball.fake_target.set(ball.position);
        ball.target.y -= modifier * 50;
        ball.fake_target.y -= modifier * 70;
    }
}

package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
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
        Vector2D startingPosition = new Vector2D(WIDTH / 2, HALF_HEIGHT / 3 * 2);
        Vector2D startingDirection = new Vector2D(0, -1);
        
        homePlayer.resetState(startingPosition, startingDirection);
        awayPlayer.resetState(VectorUtils.mirror(startingPosition), VectorUtils.mirror(startingDirection));

        Vector2D startingPlayerPosition = getPlayer(startingSide).getPosition();
        Vector2D startingPlayerDirection = getPlayer(startingSide).getDirection();
        ball.setPosition(startingPlayerPosition.add(10, startingPlayerDirection));
        ball.setRealTarget(startingPlayerPosition.add(50, startingPlayerDirection));
        ball.setFakeTarget(startingPlayerPosition.add(70, startingPlayerDirection));
    }
}

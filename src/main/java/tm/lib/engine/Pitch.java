package tm.lib.engine;

import tm.lib.domain.core.Person;
import tm.lib.domain.core.Stadium;

public class Pitch
{
    public final Player player_1;
    public final Player player_2;
    public Ball ball;
    public Stadium venue;

    static public final double WIDTH = 500;
    static public final double HEIGHT = 700;
    static public final double HHEIGHT = HEIGHT / 2;
    static public final double PLAYER_SIZE = 15;
    static public final double BALL_SIZE = 6;
    static public final double TARGET_SIZE = 2;
    static public final double FAKE_TARGET_SIZE = 8;

    public Pitch(Person firstPlayer, Person secondPlayer, Stadium venue)
    {
        player_1 = new Player(firstPlayer, Side.HOME);
        player_2 = new Player(secondPlayer, Side.AWAY);
        ball = new Ball();
        this.venue = venue;

        setInitialPositions(Side.HOME);
    }

    public void setInitialPositions(Side startingSide)
    {
        player_1.position.x = WIDTH / 2;
        player_1.position.y = HHEIGHT / 3 * 2;
        player_1.direction = new Point2d(0, -1);
        player_1.speed = 0;
        player_1.lying = false;
        player_2.position.x = WIDTH / 2;
        player_2.position.y = -HHEIGHT / 3 * 2;
        player_2.direction = new Point2d(0, 1);
        player_2.speed = 0;
        player_2.lying = false;

        ball.position.set(startingSide == Side.HOME ? player_1.position : player_2.position);
        double modifier = startingSide.getModifier();
        ball.position.y -= modifier * 10;
        ball.target.set(ball.position);
        ball.fake_target.set(ball.position);
        ball.target.y -= modifier * 50;
        ball.fake_target.y -= modifier * 70;
    }
}

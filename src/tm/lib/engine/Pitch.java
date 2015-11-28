package tm.lib.engine;

import tm.lib.base.*;

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

    public Pitch(Match match)
    {
        player_1 = new Player(match.getFirstPlayer(), 1);
        player_2 = new Player(match.getSecondPlayer(), 2);
        ball = new Ball();
        venue = match.getVenue();

        set_initial_pos(1);
    }

    public void set_initial_pos(int start_player)
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

        double p_coef = 1;
        if (start_player == 1)
        {
            ball.position.set(player_1.position);
            p_coef = 1;
        }
        else
        {
            ball.position.set(player_2.position);
            p_coef = -1;
        }

        ball.position.y -= p_coef * 10;
        ball.target.set(ball.position);
        ball.fake_target.set(ball.position);
        ball.target.y -= p_coef * 50;
        ball.fake_target.y -= p_coef * 70;
    }
}

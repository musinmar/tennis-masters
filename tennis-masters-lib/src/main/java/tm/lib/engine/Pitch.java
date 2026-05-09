package tm.lib.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.Precision;
import tm.lib.domain.core.Knight;

/**
 * Represents complete game state
 */
@Getter
@Setter
public class Pitch {

    private final Player homePlayer;
    private final Player awayPlayer;
    private final BallState ball;
    private Side lastTouchedBall;
    private final StatsCalculator statsCalculator;

    // Pitch is a rectangle with coordinates in range (0, WIDTH), (-HEIGHT / 2, HEIGHT / 2)
    static public final double WIDTH = 500;
    static public final double HEIGHT = 700;
    static public final double HALF_HEIGHT = HEIGHT / 2;

    static private final PolygonsSet HOME_ZONE = new PolygonsSet(0, WIDTH, 0, HALF_HEIGHT, VectorUtils.DEFAULT_TOLERANCE);
    static private final PolygonsSet AWAY_ZONE = new PolygonsSet(0, WIDTH, -HALF_HEIGHT, 0, VectorUtils.DEFAULT_TOLERANCE);

    public Pitch(Knight homePlayer, Knight awayPlayer, StatsCalculator statsCalculator) {
        this(
                new Player(homePlayer, Side.HOME),
                new Player(awayPlayer, Side.AWAY),
                statsCalculator,
                new BallState());
    }

    private Pitch(Player homePlayer, Player awayPlayer, StatsCalculator statsCalculator, BallState ball) {
        this.homePlayer = homePlayer;
        this.awayPlayer = awayPlayer;
        this.ball = ball;
        this.lastTouchedBall = null;
        this.statsCalculator = statsCalculator;
    }

    public Player getPlayer(Side side) {
        return side == Side.HOME ? homePlayer : awayPlayer;
    }

    public Player getOppositePlayer(Player player) {
        return player == homePlayer ? awayPlayer : homePlayer;
    }

    public Pitch createMirrored() {
        return new Pitch(
                awayPlayer.createMirrored(),
                homePlayer.createMirrored(),
                statsCalculator,
                ball.createMirrored());
    }

    public void reset(Side servingSide) {
        Vector2D startingPosition = new Vector2D(WIDTH / 2, HALF_HEIGHT / 3 * 2);
        Vector2D startingDirection = new Vector2D(0, -1);

        homePlayer.getState().reset(startingPosition, startingDirection);
        awayPlayer.getState().reset(VectorUtils.mirror(startingPosition), VectorUtils.mirror(startingDirection));

        var servingPlayer = getPlayer(servingSide);
        Vector2D servingPlayerPosition = servingPlayer.getState().getPosition();
        Vector2D servingPlayerDirection = servingPlayer.getState().getDirection();
        ball.setPosition(servingPlayerPosition.add(10, servingPlayerDirection));
        ball.setRealTarget(servingPlayerPosition.add(50, servingPlayerDirection));
        ball.setVisibleTarget(servingPlayerPosition.add(70, servingPlayerDirection));
        ball.setFlyingAboveNet(true);

        lastTouchedBall = null;
    }

    public Side getBallSide(BallState b) {
        Vector2D position = b.getPosition();
        if (position.getX() >= 0 && position.getX() <= Pitch.WIDTH
                && position.getY() >= -Pitch.HALF_HEIGHT && position.getY() <= Pitch.HALF_HEIGHT) {
            if (position.getY() >= 0) {
                return Side.HOME;
            } else {
                return Side.AWAY;
            }
        } else {
            return null;
        }
    }

    public boolean hasBallTouchedGround() {
        return VectorUtils.equalsWithTolerance(ball.getRealTarget(), ball.getPosition(), 0.001);
    }

    public boolean isInsideZone(Side side, Vector2D position) {
        PolygonsSet zone = side == Side.HOME ? Pitch.HOME_ZONE : Pitch.AWAY_ZONE;
        Region.Location location = zone.checkPoint(position);
        return location == Region.Location.BOUNDARY || location == Region.Location.INSIDE;
    }

    public double calculateDistanceToZone(Side side, Vector2D position) {
        PolygonsSet zone = side == Side.HOME ? Pitch.HOME_ZONE : Pitch.AWAY_ZONE;
        return zone.projectToBoundary(position).getOffset();
    }

    public Vector2D getClosestPointInZone(Side side, Vector2D target) {
        if (isInsideZone(side, target)) {
            return target;
        }
        PolygonsSet zone = side == Side.HOME ? Pitch.HOME_ZONE : Pitch.AWAY_ZONE;
        return (Vector2D) zone.projectToBoundary(target).getProjected();
    }

    private double calculateNetBlockedZoneLength(Vector2D fromPosition) {
        double length = Math.abs(fromPosition.getY()) / Pitch.HALF_HEIGHT * statsCalculator.getNetZone();
        return Math.max(length, 0);
    }

    public double calculateNetBlockedZoneLength(Player player) {
        return calculateNetBlockedZoneLength(player.getState().getPosition());
    }

    public boolean canPlayerHitBall(Player player) {
        double distanceToBall = player.getState().getPosition().distance(ball.getPosition());
        return Precision.compareTo(distanceToBall, MatchEngineConstants.PLAYER_HAND_LENGTH, VectorUtils.DEFAULT_TOLERANCE) <= 0;
    }

    public boolean canPlayerSaveBall(Player player, Vector2D position) {
        double distanceToBall = player.getState().getPosition().distance(position);
        double saveRange = MatchEngineConstants.PLAYER_HAND_LENGTH + getStatsCalculator().getActualSaveAddDistance(player);
        return Precision.compareTo(distanceToBall, saveRange, VectorUtils.DEFAULT_TOLERANCE) <= 0;
    }

    public static Vector2D normalize(Vector2D vector2D) {
        return normalize(vector2D.getX(), vector2D.getY());
    }

    public static Vector2D normalize(double x, double y) {
        //return new Vector2D((x - Pitch.WIDTH / 2) / (2 * Pitch.WIDTH), y / Pitch.HEIGHT);
        return new Vector2D((x + Pitch.WIDTH / 2) / (2 * Pitch.WIDTH), (y + Pitch.HEIGHT) / (2 * Pitch.HEIGHT));
    }

    public static Vector2D denormalize(double x, double y) {
        return new Vector2D((x - 0.5) * 2 * Pitch.WIDTH + Pitch.WIDTH / 2, (y - 0.5) * 2 * Pitch.HEIGHT);
    }
}

package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.Precision;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Stadium;

public class Pitch {

    private final Player homePlayer;
    private final Player awayPlayer;
    private final Ball ball;
    private final Stadium venue;
    private final StatsCalculator statsCalculator;

    // Pitch is a rectangle with coordinates in range (0, WIDTH), (-HEIGHT / 2, HEIGHT / 2)
    static public final double WIDTH = 500;
    static public final double HEIGHT = 700;
    static public final double HALF_HEIGHT = HEIGHT / 2;

    static private final PolygonsSet HOME_ZONE = new PolygonsSet(0, WIDTH, 0, HALF_HEIGHT, VectorUtils.DEFAULT_TOLERANCE);
    static private final PolygonsSet AWAY_ZONE = new PolygonsSet(0, WIDTH, -HALF_HEIGHT, 0, VectorUtils.DEFAULT_TOLERANCE);

    public Pitch(Knight homePlayer, Knight awayPlayer, Stadium venue) {
        this(
                new Player(homePlayer, Side.HOME),
                new Player(awayPlayer, Side.AWAY),
                new Ball(),
                venue);
    }

    private Pitch(Player homePlayer, Player awayPlayer, Ball ball, Stadium venue) {
        this.homePlayer = homePlayer;
        this.awayPlayer = awayPlayer;
        this.ball = ball;
        this.venue = venue;
        statsCalculator = new StatsCalculator(venue);
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

    public StatsCalculator getStatsCalculator() {
        return statsCalculator;
    }

    public Pitch createMirrored() {
        return new Pitch(
                awayPlayer.createMirrored(),
                homePlayer.createMirrored(),
                ball.createMirrored(),
                venue);
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
        ball.setVisibleTarget(startingPlayerPosition.add(70, startingPlayerDirection));
        ball.setFlyingAboveNet(true);
    }

    public Side getBallSide(Ball b) {
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
        return calculateNetBlockedZoneLength(player.getPosition());
    }

    public boolean canPlayerHitBall(Player player) {
        double distanceToBall = player.getPosition().distance(ball.getPosition());
        return Precision.compareTo(distanceToBall, MatchEngineConstants.PLAYER_HAND_LENGTH, VectorUtils.DEFAULT_TOLERANCE) <= 0;
    }

    public boolean canPlayerSaveBall(Player player, Vector2D position) {
        double distanceToBall = player.getPosition().distance(position);
        double saveRange = MatchEngineConstants.PLAYER_HAND_LENGTH + getStatsCalculator().getActualSaveAddDistance(player);
        return Precision.compareTo(distanceToBall, saveRange, VectorUtils.DEFAULT_TOLERANCE) <= 0;
    }
}

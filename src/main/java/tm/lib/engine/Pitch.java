package tm.lib.engine;

import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;
import tm.lib.domain.core.Person;
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

    public Pitch(Person homePlayer, Person awayPlayer, Stadium venue) {
        this.homePlayer = new Player(homePlayer, Side.HOME);
        this.awayPlayer = new Player(awayPlayer, Side.AWAY);
        this.ball = new Ball();
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
    }
    
    boolean isInsideZone(Side side, Vector2D position) {
        PolygonsSet zone = side == Side.HOME ? Pitch.HOME_ZONE : Pitch.AWAY_ZONE;
        Region.Location location = zone.checkPoint(position);
        return location == Region.Location.BOUNDARY || location == Region.Location.INSIDE;
    }
    
    double calculateDistanceToZone(Side side, Vector2D position) {
        PolygonsSet zone = side == Side.HOME ? Pitch.HOME_ZONE : Pitch.AWAY_ZONE;
        return zone.projectToBoundary(position).getOffset();
    }
    
    Vector2D getClosestPointInZone(Side side, Vector2D target) {
        if (isInsideZone(side, target)) {
            return target;
        }
        PolygonsSet zone = side == Side.HOME ? Pitch.HOME_ZONE : Pitch.AWAY_ZONE;
        return (Vector2D) zone.projectToBoundary(target).getProjected();
    }
    
    public double calculateNetBlockedZoneLength(Vector2D fromPosition) {
        double length = Math.abs(fromPosition.getY()) / Pitch.HALF_HEIGHT * statsCalculator.getNetZone();
        return Math.max(length, 0);
    }
}

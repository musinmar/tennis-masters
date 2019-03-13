package tm.ui.qt;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QSizeF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QWidget;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.engine.Ball;
import tm.lib.engine.Pitch;
import tm.lib.engine.Player;
import tm.lib.engine.Side;

public class PitchWidget extends QFrame {

    private static final int HORIZONTAL_MARGIN = 5;
    private static final int VERTICAL_MARGIN = 10;

    private static final QColor PITCH_COLOR = new QColor(254, 251, 126);
    private static final QColor DARK_PITCH_COLOR = new QColor(203, 200, 75);

    private final Pitch pitch;
    private QRectF pitchRect = new QRectF();

    public PitchWidget(Pitch pitch, QWidget parent) {
        super(parent);
        this.pitch = pitch;
        setupUi();
    }

    private void setupUi() {
        setFrameShape(QFrame.Shape.Box);
        setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    }

    @Override
    protected void resizeEvent(QResizeEvent resizeEvent) {
        super.resizeEvent(resizeEvent);

        QSizeF currentSize = new QSizeF(resizeEvent.size().width() - 2 * HORIZONTAL_MARGIN,
                resizeEvent.size().height() - 2 * VERTICAL_MARGIN);
        QSizeF realSize = new QSizeF(Pitch.WIDTH, Pitch.HEIGHT);

        realSize.scale(currentSize, Qt.AspectRatioMode.KeepAspectRatio);

        QPointF topLeft = new QPointF((currentSize.width() - realSize.width()) / 2 + HORIZONTAL_MARGIN,
                (currentSize.height() - realSize.height()) / 2 + VERTICAL_MARGIN);
        pitchRect = new QRectF(topLeft, realSize);
    }

    @Override
    protected void paintEvent(QPaintEvent paintEvent) {
        super.paintEvent(paintEvent);

        if (!pitchRect.isValid()) {
            return;
        }

        QPainter painter = new QPainter(this);
        painter.setBrush(new QColor(Qt.GlobalColor.black));
        painter.translate(pitchRect.topLeft());
        painter.scale(pitchRect.width() / Pitch.WIDTH, pitchRect.height() / Pitch.HEIGHT);
        painter.setRenderHints(new QPainter.RenderHints(QPainter.RenderHint.SmoothPixmapTransform, QPainter.RenderHint.Antialiasing));

        drawPitch(painter);
        drawPlayer(painter, Side.HOME);
        drawPlayer(painter, Side.AWAY);
        drawBall(painter);
    }

    private QPointF map(Vector2D position) {
        return new QPointF(position.getX(), Pitch.HALF_HEIGHT - position.getY());
    }

    private void drawPitch(QPainter painter) {
        painter.setBrush(PITCH_COLOR);
        QPen borderPen = new QPen(new QColor(Qt.GlobalColor.darkRed), 2);
        painter.setPen(borderPen);
        painter.drawRect(new QRectF(0, 0, Pitch.WIDTH, Pitch.HEIGHT));
        painter.drawLine(new QPointF(0, Pitch.HALF_HEIGHT), new QPointF(Pitch.WIDTH, Pitch.HALF_HEIGHT));

        /*gc.setBackground(PITCH_COLOR);
        gc.setLineWidth(2);
        Rectangle pitchRectangle = mapRect(0, Pitch.HALF_HEIGHT, Pitch.WIDTH, -Pitch.HALF_HEIGHT);
        gc.fillRectangle(pitchRectangle);
        gc.setBackground(DARK_PITCH_COLOR);

        if (DRAW_BLOCKED_ZONES) {
            double homePlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.HOME));
            gc.fillRectangle(mapRect(0, 0, Pitch.WIDTH, -homePlayerBlockedZoneLength));
            double awayPlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.AWAY));
            gc.fillRectangle(mapRect(0, awayPlayerBlockedZoneLength, Pitch.WIDTH, 0));
        }

        gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_RED));
        gc.drawRectangle(pitchRectangle);
        gc.drawLine(pitchPos.x, pitchPos.y + pitchSize.y / 2, pitchPos.x + pitchSize.x, pitchPos.y + pitchSize.y / 2);
         */
    }

    private void drawPlayer(QPainter painter, Side side) {
        Player player = pitch.getPlayer(side);

        if (!player.isLying()) {
            painter.setPen(new QPen(new QColor(Qt.GlobalColor.darkBlue), 2));
            painter.setBrush(new QColor(Qt.GlobalColor.blue));
        } else {
            painter.setPen(new QPen(new QColor(Qt.GlobalColor.darkRed), 2));
            painter.setBrush(new QColor(Qt.GlobalColor.red));
        }

        final double PLAYER_SIZE = 7.5;
        painter.drawEllipse(map(player.getPosition()), PLAYER_SIZE, PLAYER_SIZE);
    }

    private void drawBall(QPainter painter) {
        Ball ball = pitch.getBall();
        final double BALL_SIZE = 2;

        painter.setPen(new QPen(new QColor(Qt.GlobalColor.black), 2));
        Qt.GlobalColor ballColor = ball.isFlyingAboveNet() ? Qt.GlobalColor.black : Qt.GlobalColor.red;
        painter.setBrush(new QColor(ballColor));
        painter.drawEllipse(map(ball.getPosition()), BALL_SIZE, BALL_SIZE);

        final double TARGET_SIZE = 0.5;
        painter.drawEllipse(map(ball.getRealTarget()), TARGET_SIZE, TARGET_SIZE);

        final double VISIBLE_TARGET_SIZE = 3;
        QPen visibleTargetPen = new QPen(painter.pen());
        visibleTargetPen.setWidth(1);
        painter.setPen(visibleTargetPen);
        QPointF visibleTarget = map(ball.getVisibleTarget());
        painter.drawLine(new QPointF(visibleTarget.x() - VISIBLE_TARGET_SIZE, visibleTarget.y() - VISIBLE_TARGET_SIZE),
                new QPointF(visibleTarget.x() + VISIBLE_TARGET_SIZE, visibleTarget.y() + VISIBLE_TARGET_SIZE));
        painter.drawLine(new QPointF(visibleTarget.x() - VISIBLE_TARGET_SIZE, visibleTarget.y() + VISIBLE_TARGET_SIZE),
                new QPointF(visibleTarget.x() + VISIBLE_TARGET_SIZE, visibleTarget.y() - VISIBLE_TARGET_SIZE));

        /*
        Point pos = mapPos(ball.getPosition());
        double coef = getPitchSizeCoef();
        int radius = (int) (coef * BALL_SIZE / 2);
        if (radius < 1) {
            radius = 1;
        }
        gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_BLACK));
        int ballColorId = ball.isFlyingAboveNet() ? SWT.COLOR_BLACK : SWT.COLOR_RED;
        gc.setForeground(TenisMasters.display.getSystemColor(ballColorId));
        gc.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);

        pos = mapPos(ball.getRealTarget());
        int size = (int) (coef * TARGET_SIZE / 2);
        if (size < 1) {
            size = 1;
        }
        gc.drawOval(pos.x - size, pos.y - size, size * 2, size * 2);

        pos = mapPos(ball.getVisibleTarget());
        size = (int) (coef * VISIBLE_TARGET_SIZE / 2);
        if (size < 1) {
            size = 1;
        }
        gc.setLineWidth(1);
        gc.drawLine(pos.x - size, pos.y - size, pos.x + size, pos.y + size);
        gc.drawLine(pos.x - size, pos.y + size, pos.x + size, pos.y - size);*/
    }
}

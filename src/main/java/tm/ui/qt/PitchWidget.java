package tm.ui.qt;

import io.qt.core.QPoint;
import io.qt.core.QPointF;
import io.qt.core.QRectF;
import io.qt.core.QSize;
import io.qt.core.QSizeF;
import io.qt.core.Qt;
import io.qt.gui.QColor;
import io.qt.gui.QPaintEvent;
import io.qt.gui.QPainter;
import io.qt.gui.QPen;
import io.qt.gui.QResizeEvent;
import io.qt.widgets.QFrame;
import io.qt.widgets.QLabel;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QWidget;
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
    private static final QColor PITCH_BORDER_COLOR = new QColor(Qt.GlobalColor.darkRed);

    private static final boolean DRAW_BLOCKED_ZONES = true;

    private QLabel infoLabel;

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

        infoLabel = new QLabel(this);
        infoLabel.setStyleSheet("QLabel {background-color: white; padding: 6px;}");
        infoLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);
        infoLabel.setVisible(false);
    }

    @Override
    protected void resizeEvent(QResizeEvent resizeEvent) {
        super.resizeEvent(resizeEvent);

        updatePitchRect(resizeEvent.size());
        updateInfoLabelPosition();
    }

    private void updatePitchRect(QSize newWidgetSize) {
        QSizeF currentSize = new QSizeF(newWidgetSize.width() - 2 * HORIZONTAL_MARGIN,
                newWidgetSize.height() - 2 * VERTICAL_MARGIN);
        QSizeF realSize = new QSizeF(Pitch.WIDTH, Pitch.HEIGHT);

        realSize.scale(currentSize, Qt.AspectRatioMode.KeepAspectRatio);

        QPointF topLeft = new QPointF((currentSize.width() - realSize.width()) / 2 + HORIZONTAL_MARGIN,
                (currentSize.height() - realSize.height()) / 2 + VERTICAL_MARGIN);
        pitchRect = new QRectF(topLeft, realSize);
    }

    private void updateInfoLabelPosition() {
        QSize infoLabelSize = infoLabel.size();
        QSize size = size();
        QPoint position = new QPoint(size.width() / 2 - infoLabelSize.width() / 2,
                size.height() / 2 - infoLabelSize.height() / 2);
        infoLabel.move(position);
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
        painter.setPen(Qt.PenStyle.NoPen);
        painter.drawRect(new QRectF(0, 0, Pitch.WIDTH, Pitch.HEIGHT));
        if (DRAW_BLOCKED_ZONES) {
            painter.setBrush(DARK_PITCH_COLOR);
            double homePlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.HOME));
            double awayPlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.AWAY));
            painter.drawRect(new QRectF(0, Pitch.HALF_HEIGHT - awayPlayerBlockedZoneLength,
                    Pitch.WIDTH, homePlayerBlockedZoneLength + awayPlayerBlockedZoneLength));
        }
        QPen borderPen = new QPen(PITCH_BORDER_COLOR, 2);
        painter.setPen(borderPen);
        painter.setBrush(Qt.BrushStyle.NoBrush);
        painter.drawRect(new QRectF(0, 0, Pitch.WIDTH, Pitch.HEIGHT));
        painter.drawLine(new QPointF(0, Pitch.HALF_HEIGHT), new QPointF(Pitch.WIDTH, Pitch.HALF_HEIGHT));
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
    }

    public void showInfoLabel(String text) {
        if (text == null) {
            infoLabel.setVisible(false);
        } else {
            infoLabel.setText(text);
            infoLabel.resize(infoLabel.sizeHint());
            updateInfoLabelPosition();
            infoLabel.setVisible(true);
        }
    }
}

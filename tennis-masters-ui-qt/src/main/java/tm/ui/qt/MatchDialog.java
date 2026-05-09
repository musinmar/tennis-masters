package tm.ui.qt;

import io.qt.Nullable;
import io.qt.core.QPointF;
import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.gui.QFont;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.QDialog;
import io.qt.widgets.QFrame;
import io.qt.widgets.QGridLayout;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLayout;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QSpacerItem;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.engine.MatchEngine;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.Side;
import tm.lib.engine.StrategyHolder;
import tm.lib.engine.VectorUtils;
import tm.lib.engine.strategies.ManualStrategy;

import java.time.Duration;

@Slf4j
public class MatchDialog extends QDialog {

    private static final double ANIMATION_SPEED_FACTOR = 1;
    private static final int INFO_LABEL_DURATION_MS = 3000;

    private final Match match;
    private final MatchSimulator matchSimulator;

    private boolean paused = true;
    private MatchScore finalScore;

    private QTimer activeTimer;
    private QPushButton startButton;
    private PitchWidget pitchWidget;
    private QLabel matchTimeLabel;
    private MatchSimulator.State currentState;
    private QLabel matchScoreLabel;
    private PlayerInfoWidget homePlayerInfoWidget;
    private PlayerInfoWidget awayPlayerInfoWidget;

    private ManualControls manualControls;

    @RequiredArgsConstructor
    private class ManualControls {

        private final ManualStrategy strategy;
        private final Side side;

        private boolean leftPressed = false;
        private boolean rightPressed = false;
        private boolean upPressed = false;
        private boolean downPressed = false;
        private Vector2D clickedPosition = Vector2D.ZERO;

        private void applyManualControls() {
            var rawPosition = matchSimulator.getPitch().getPlayer(side).getState().getPosition();
            var position = side == Side.AWAY ? VectorUtils.mirror(rawPosition) : rawPosition;
            Vector2D verticalMovement = Vector2D.ZERO;
            if (upPressed && !downPressed) {
                verticalMovement = new Vector2D(0, -200);
            } else if (downPressed && !upPressed) {
                verticalMovement = new Vector2D(0, 200);
            }
            verticalMovement = side == Side.HOME ? VectorUtils.mirror(verticalMovement) : verticalMovement;
            position = position.add(verticalMovement);
            if (rightPressed && !leftPressed) {
                position = position.add(new Vector2D(200, 0));
            } else if (leftPressed && !rightPressed) {
                position = position.add(new Vector2D(-200, 0));
            }
            strategy.setMoveToPosition(position);

            var target = side == Side.HOME ? VectorUtils.mirror(clickedPosition) : clickedPosition;
            strategy.setTarget(target);
        }
    }

    public MatchDialog(Match match, StrategyHolder strategyHolder, QWidget parent) {
        super(parent);
        this.match = match;
        matchSimulator = new MatchSimulator(match, strategyHolder);
        if (strategyHolder.getStrategy(Side.HOME) instanceof ManualStrategy homeManualStrategy) {
            manualControls = new ManualControls(homeManualStrategy, Side.HOME);
        } else if (strategyHolder.getStrategy(Side.AWAY) instanceof ManualStrategy awayManualStrategy) {
            manualControls = new ManualControls(awayManualStrategy, Side.AWAY);
        } else {
            manualControls = null;
        }
        setupUi();
    }

    private void setupUi() {
        setWindowTitle("Match");
        setWindowState(Qt.WindowState.WindowMaximized);

        QFont font = new QFont(font());
        font.setPointSize(12);
        setFont(font);

        QLayout mainLayout = new QHBoxLayout(this);

        QGridLayout pitchPanelLayout = new QGridLayout();
        mainLayout.addItem(pitchPanelLayout);

        homePlayerInfoWidget = new PlayerInfoWidget(matchSimulator.getPitch().getPlayer(Side.HOME), this);
        pitchPanelLayout.addWidget(homePlayerInfoWidget, 0, 0, Qt.AlignmentFlag.AlignHCenter);

        pitchWidget = new PitchWidget(matchSimulator.getPitch(), this);
        pitchWidget.clicked.connect(this::onPitchClicked);
        pitchPanelLayout.addWidget(pitchWidget, 1, 0);

        awayPlayerInfoWidget = new PlayerInfoWidget(matchSimulator.getPitch().getPlayer(Side.AWAY), this);
        pitchPanelLayout.addWidget(awayPlayerInfoWidget, 2, 0, Qt.AlignmentFlag.AlignHCenter);

        QFrame infoWidget = new QFrame(this);
        infoWidget.setMinimumWidth(200);
        infoWidget.setMaximumWidth(300);
        infoWidget.setFrameShape(QFrame.Shape.Box);
        mainLayout.addWidget(infoWidget);

        QLayout infoPanelLayout = new QVBoxLayout();
        infoWidget.setLayout(infoPanelLayout);

        QLabel playersLabel = new QLabel();
        infoPanelLayout.addWidget(playersLabel);
        playersLabel.setAlignment(Qt.AlignmentFlag.AlignHCenter);
        playersLabel.setText(match.getHomePlayer().getFullName() + " - " + match.getAwayPlayer().getFullName());

        matchScoreLabel = new QLabel();
        matchScoreLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);
        updateMatchScoreLabel();
        infoPanelLayout.addWidget(matchScoreLabel);

        matchTimeLabel = new QLabel();
        matchTimeLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);
        updateMatchTimeLabel();
        infoPanelLayout.addWidget(matchTimeLabel);

        QLabel resultLabel = new QLabel();
        infoPanelLayout.addWidget(resultLabel);

        QLabel timeLabel = new QLabel();
        infoPanelLayout.addWidget(timeLabel);

        infoPanelLayout.addItem(new QSpacerItem(10, 10, QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Expanding));

        startButton = new QPushButton(this);
        startButton.setText("Start");
        startButton.clicked.connect(this::onStartButtonClicked);
        infoPanelLayout.addWidget(startButton);

        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Close");
        closeButton.clicked.connect(this::onCloseButtonClicked);
        infoPanelLayout.addWidget(closeButton);
    }

    @Override
    protected void keyPressEvent(@Nullable QKeyEvent event) {
        if (manualControls != null) {
            setKeyPressed(event.key(), true);
            event.accept();
        } else {
            super.keyPressEvent(event);
        }
    }

    @Override
    protected void keyReleaseEvent(@Nullable QKeyEvent event) {
        if (manualControls != null) {
            setKeyPressed(event.key(), false);
            event.accept();
        } else {
            super.keyReleaseEvent(event);
        }
    }

    private void setKeyPressed(int key, boolean pressed) {
        if (key == Qt.Key.Key_W.value()) {
            manualControls.upPressed = pressed;
        } else if (key == Qt.Key.Key_S.value()) {
            manualControls.downPressed = pressed;
        } else if (key == Qt.Key.Key_D.value()) {
            manualControls.rightPressed = pressed;
        } else if (key == Qt.Key.Key_A.value()) {
            manualControls.leftPressed = pressed;
        }
    }

    private void onPitchClicked(QPointF position) {
        if (manualControls != null) {
            manualControls.clickedPosition = new Vector2D(position.x(), position.y());
        }
    }

    public MatchScore getFinalScore() {
        return finalScore;
    }

    private void onCloseButtonClicked() {
        if (finalScore == null) {
            if (activeTimer != null) {
                activeTimer.stop();
            }
            MatchSimulator.State state;
            do {
                state = matchSimulator.proceed();
            } while (state != MatchSimulator.State.MATCH_ENDED);
            finishMatch();
        }

        close();
    }

    private void onStartButtonClicked() {
        if (paused) {
            if (activeTimer == null) {
                activeTimer = createSimulationTimer();
            }
            startButton.setText("Pause");
            paused = false;
            activeTimer.start();
        } else {
            startButton.setText("Continue");
            paused = true;
            activeTimer.stop();
        }
    }

    private QTimer createSimulationTimer() {
        QTimer timer = new QTimer(this);
        timer.setInterval((int) (MatchEngine.TIME_STEP_MILLIS / ANIMATION_SPEED_FACTOR));
        timer.timeout.connect(this::advanceSimulation);
        return timer;
    }

    private QTimer createNextGameTimer() {
        QTimer timer = new QTimer(this);
        timer.setInterval(INFO_LABEL_DURATION_MS);
        timer.setSingleShot(true);
        timer.timeout.connect(this::startNextGame);
        return timer;
    }

    private QTimer createNextSetTimer() {
        QTimer timer = new QTimer(this);
        timer.setInterval(INFO_LABEL_DURATION_MS);
        timer.setSingleShot(true);
        timer.timeout.connect(this::startNextSet);
        return timer;
    }

    private void advanceSimulation() {
        if (manualControls != null) {
            manualControls.applyManualControls();
        }
        currentState = matchSimulator.proceed();
        if (currentState == MatchSimulator.State.PLAYING) {
            pitchWidget.update();
            homePlayerInfoWidget.updateEnergy();
            awayPlayerInfoWidget.updateEnergy();
            updateMatchTimeLabel();
        } else {
            activeTimer.stop();
            String info = String.format("Game played, winner - %s<br>Score in game: %s",
                    matchSimulator.getLastGameWinner().getFullName(), matchSimulator.getCurrentScore());
            updateMatchScoreLabel();
            pitchWidget.showInfoLabel(info);
            activeTimer = createNextGameTimer();
            activeTimer.start();
        }
    }

    private void startNextGame() {
        activeTimer.stop();
        if (currentState == MatchSimulator.State.GAME_ENDED) {
            pitchWidget.showInfoLabel(null);
            activeTimer = createSimulationTimer();
        } else {
            String info = String.format("Set played, winner - %s<br>Score in game: %s",
                    matchSimulator.getLastGameWinner().getFullName(), matchSimulator.getCurrentScore());
            pitchWidget.showInfoLabel(info);
            activeTimer = createNextSetTimer();
        }
        activeTimer.start();
    }

    private void startNextSet() {
        activeTimer.stop();
        if (currentState == MatchSimulator.State.SET_ENDED) {
            pitchWidget.showInfoLabel(null);
            activeTimer = createSimulationTimer();
            activeTimer.start();
        } else {
            String info = String.format("Match finished, winner - %s<br>Final score: %s",
                    matchSimulator.getLastGameWinner().getFullName(), matchSimulator.getCurrentScore());
            pitchWidget.showInfoLabel(info);
            finishMatch();
            activeTimer = null;
        }
    }

    private void updateMatchTimeLabel() {
        Duration matchDuration = Duration.ofMillis(matchSimulator.getMatchTime());
        matchTimeLabel.setText(String.format("%02d:%02d.%04d", matchDuration.toMinutes(), matchDuration.getSeconds() % 60, matchDuration.getNano() / 100000));
    }

    private void updateMatchScoreLabel() {
        matchScoreLabel.setText(matchSimulator.getCurrentScore().toString());
    }

    private void finishMatch() {
        startButton.setDisabled(true);
        finalScore = matchSimulator.getCurrentScore();
    }
}

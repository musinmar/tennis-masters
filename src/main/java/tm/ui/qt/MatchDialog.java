package tm.ui.qt;

import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.gui.QFont;
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
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.engine.MatchEngine;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.Side;
import tm.lib.engine.StrategyProvider;

import java.time.Duration;

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

    public MatchDialog(Match match, StrategyProvider strategyProvider, QWidget parent) {
        super(parent);
        this.match = match;
        matchSimulator = new MatchSimulator(match, strategyProvider);
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
        startButton.setText("Начать");
        startButton.clicked.connect(this, "onStartButtonClicked()");
        infoPanelLayout.addWidget(startButton);

        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Закрыть");
        closeButton.clicked.connect(this, "onCloseButtonClicked()");
        infoPanelLayout.addWidget(closeButton);
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
            startButton.setText("Пауза");
            paused = false;
            activeTimer.start();
        } else {
            startButton.setText("Продолжить");
            paused = true;
            activeTimer.stop();
        }
    }

    private QTimer createSimulationTimer() {
        QTimer timer = new QTimer(this);
        timer.setInterval((int) (MatchEngine.TIME_STEP * 1000 / ANIMATION_SPEED_FACTOR));
        timer.timeout.connect(this, "advanceSimulation()");
        return timer;
    }

    private QTimer createNextGameTimer() {
        QTimer timer = new QTimer(this);
        timer.setInterval(INFO_LABEL_DURATION_MS);
        timer.setSingleShot(true);
        timer.timeout.connect(this, "startNextGame()");
        return timer;
    }

    private QTimer createNextSetTimer() {
        QTimer timer = new QTimer(this);
        timer.setInterval(INFO_LABEL_DURATION_MS);
        timer.setSingleShot(true);
        timer.timeout.connect(this, "startNextSet()");
        return timer;
    }

    private void advanceSimulation() {
        currentState = matchSimulator.proceed();
        if (currentState == MatchSimulator.State.PLAYING) {
            pitchWidget.update();
            homePlayerInfoWidget.updateEnergy();
            awayPlayerInfoWidget.updateEnergy();
            updateMatchTimeLabel();
        } else {
            activeTimer.stop();
            String info = String.format("Гейм разыгран, победитель - %s<br>Счёт в игре: %s",
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
            String info = String.format("Сет разыгран, победитель - %s<br>Счёт в игре: %s",
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
            String info = String.format("Матч закончен, победитель - %s<br>Итоговый счёт: %s",
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

package tm.ui.qt;

import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QSpacerItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.competition.Match;
import tm.lib.engine.MatchEngine;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.Side;

import java.time.Duration;

public class MatchDialog extends QDialog {

    private static final double ANIMATION_SPEED_FACTOR = 1;
    private static final int INFO_LABEL_DURATION_MS = 3000;

    private final Match match;
    private final MatchSimulator matchSimulator;

    private boolean paused = true;
    private QTimer activeTimer;
    private QPushButton startButton;
    private PitchWidget pitchWidget;
    private QLabel matchTimeLabel;
    private MatchSimulator.State currentState;

    public MatchDialog(Match match, QWidget parent) {
        super(parent);
        this.match = match;
        matchSimulator = new MatchSimulator(match);
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

        PlayerInfoWidget homePlayerInfoWidget = new PlayerInfoWidget(matchSimulator.getPitch().getPlayer(Side.HOME), this);
        pitchPanelLayout.addWidget(homePlayerInfoWidget, 0, 0, Qt.AlignmentFlag.AlignHCenter);

        pitchWidget = new PitchWidget(matchSimulator.getPitch(), this);
        pitchPanelLayout.addWidget(pitchWidget, 1, 0);

        PlayerInfoWidget awayPlayerInfoWidget = new PlayerInfoWidget(matchSimulator.getPitch().getPlayer(Side.AWAY), this);
        pitchPanelLayout.addWidget(awayPlayerInfoWidget, 2, 0, Qt.AlignmentFlag.AlignHCenter);

        QFrame infoWidget = new QFrame(this);
        infoWidget.setMinimumWidth(150);
        infoWidget.setMaximumWidth(250);
        infoWidget.setFrameShape(QFrame.Shape.Box);
        mainLayout.addWidget(infoWidget);

        QLayout infoPanelLayout = new QVBoxLayout();
        infoWidget.setLayout(infoPanelLayout);

        QLabel playersLabel = new QLabel();
        infoPanelLayout.addWidget(playersLabel);
        playersLabel.setAlignment(Qt.AlignmentFlag.AlignHCenter);
        playersLabel.setText(match.getFirstPlayer().getFullName() + " - " + match.getSecondPlayer().getFullName());

        matchTimeLabel = new QLabel();
        setMatchTimeLabel(0);
        infoPanelLayout.addWidget(matchTimeLabel);

        QLabel resultLabel = new QLabel();
        infoPanelLayout.addWidget(resultLabel);

        QLabel timeLabel = new QLabel();
        infoPanelLayout.addWidget(timeLabel);

        infoPanelLayout.addItem(new QSpacerItem(10, 10, QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Expanding));

        startButton = new QPushButton(this);
        startButton.setText("Start");
        startButton.clicked.connect(this, "onStartButtonClicked()");
        infoPanelLayout.addWidget(startButton);

        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Close");
        closeButton.clicked.connect(this, "onCloseButtonClicked()");
        infoPanelLayout.addWidget(closeButton);
    }

    private void onCloseButtonClicked() {
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
            setMatchTimeLabel(matchSimulator.getMatchTime());
        } else {
            activeTimer.stop();
            String info = String.format("Гейм разыгран, победитель - %s<br>Счёт в игре: %s",
                    matchSimulator.getLastGameWinner().getFullName(), matchSimulator.getCurrentScore());
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
            startButton.setDisabled(true);
            activeTimer = null;
        }
    }

    private void setMatchTimeLabel(long matchDurationInMillis) {
        Duration matchDuration = Duration.ofMillis(matchDurationInMillis);
        matchTimeLabel.setText(String.format("%02d:%02d", matchDuration.toMinutes(), matchDuration.getSeconds()));
    }
}

package tm.ui.qt;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPlainTextEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.MatchEvent;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.GameWorld;
import tm.lib.engine.MatchSimulator;

public class GameWorldDialog extends QDialog {

    private QPlainTextEdit gameWorldLogTextEdit;

    private final GameWorld gameWorld;

    public GameWorldDialog(GameWorld gameWorld, QWidget parent) {
        super(parent);
        this.gameWorld = gameWorld;
        setupUi();
        updateLogText();
    }

    private void setupUi() {
        setWindowTitle("Game World");

        QLayout mainLayout = new QVBoxLayout(this);

        gameWorldLogTextEdit = new QPlainTextEdit(this);
        gameWorldLogTextEdit.setFont(new QFont("Courier New"));
        gameWorldLogTextEdit.setMinimumSize(200, 30);
        mainLayout.addWidget(gameWorldLogTextEdit);

        QLayout bottomButtonsLayout = new QHBoxLayout();
        mainLayout.addItem(bottomButtonsLayout);

        QPushButton nextButton = new QPushButton(this);
        nextButton.setText("Next Match");
        nextButton.clicked.connect(this, "onNextMatchButtonClicked()");
        bottomButtonsLayout.addWidget(nextButton);

        QPushButton nextFastButton = new QPushButton(this);
        nextFastButton.setText("Next Match Fast");
        nextFastButton.clicked.connect(this, "onNextMatchFastButtonClicked()");
        bottomButtonsLayout.addWidget(nextFastButton);

        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Close");
        closeButton.clicked.connect(this, "onCloseButtonClicked()");
        bottomButtonsLayout.addWidget(closeButton);

        resize(1000, 600);
        move(200, 50);
    }

    private void onCloseButtonClicked() {
        close();
    }

    private void onNextMatchButtonClicked() {
        MatchEvent match = gameWorld.getCurrentSeason().getNextMatch();
        MatchDialog matchDialog = new MatchDialog(match, this);
        matchDialog.exec();
        MatchScore score = matchDialog.getFinalScore();
        if (score != null) {
            gameWorld.getCurrentSeason().processMatch(match, score);
            updateLogText();
        }
    }

    private void onNextMatchFastButtonClicked() {
        MatchEvent match = gameWorld.getCurrentSeason().getNextMatch();
        MatchSimulator matchSimulator = new MatchSimulator(match.createMatchSpec());
        MatchSimulator.State state;
        do {
            state = matchSimulator.proceed();
        } while (state != MatchSimulator.State.MATCH_ENDED);
        MatchScore score = matchSimulator.getCurrentScore();
        if (score != null) {
            gameWorld.getCurrentSeason().processMatch(match, score);
            updateLogText();
        }
    }

    private void updateLogText() {
        Competition competitionToPrint = gameWorld.getCurrentSeason().getSeasonCompetition();
        gameWorldLogTextEdit.setPlainText(competitionToPrint.printToString());
    }
}

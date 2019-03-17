package tm.ui.qt;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPlainTextEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.GameWorld;
import tm.lib.domain.world.Season;
import tm.lib.engine.MatchSimulator;

public class GameWorldDialog extends QDialog {

    private final GameWorld gameWorld;

    private QPlainTextEdit gameWorldLogTextEdit;
    private QComboBox seasonComboBox;
    private QComboBox tournamentComboBox;
    private QComboBox stageComboBox;
    private QComboBox subStageComboBox;
    private QLabel nextMatchLabel;

    public GameWorldDialog(GameWorld gameWorld, QWidget parent) {
        super(parent);
        this.gameWorld = gameWorld;
        setupUi();
        populateSeasonComboBox();
        updateLogText();
        updateNextMatchLabel();
    }

    private void setupUi() {
        setWindowTitle("Game World");

        QLabel seasonComboBoxLabel = new QLabel(this);
        seasonComboBoxLabel.setText("Сезон:");
        seasonComboBox = new QComboBox(this);
        seasonComboBox.currentIndexChanged.connect(this, "onSeasonComboBoxIndexChanged()");

        QLabel tournamentComboBoxLabel = new QLabel(this);
        tournamentComboBoxLabel.setText("Соревнование:");
        tournamentComboBox = new QComboBox(this);
        tournamentComboBox.currentIndexChanged.connect(this, "onTournamentComboBoxIndexChanged()");

        QLabel stageComboBoxLabel = new QLabel(this);
        stageComboBoxLabel.setText("Стадия:");
        stageComboBox = new QComboBox(this);
        stageComboBox.currentIndexChanged.connect(this, "onStageComboBoxIndexChanged()");

        QLabel subStageComboBoxLabel = new QLabel(this);
        subStageComboBoxLabel.setText("Группа/раунд:");
        subStageComboBox = new QComboBox(this);
        subStageComboBox.setEnabled(false);
        subStageComboBox.currentIndexChanged.connect(this, "onSubStageComboBoxIndexChanged()");

        QHBoxLayout competitionSelectorLayout = new QHBoxLayout();
        competitionSelectorLayout.setSpacing(10);
        competitionSelectorLayout.addWidget(seasonComboBoxLabel);
        competitionSelectorLayout.addWidget(seasonComboBox);
        competitionSelectorLayout.addWidget(tournamentComboBoxLabel);
        competitionSelectorLayout.addWidget(tournamentComboBox);
        competitionSelectorLayout.addWidget(stageComboBoxLabel);
        competitionSelectorLayout.addWidget(stageComboBox);
        competitionSelectorLayout.addWidget(subStageComboBoxLabel);
        competitionSelectorLayout.addWidget(subStageComboBox);

        gameWorldLogTextEdit = new QPlainTextEdit(this);
        gameWorldLogTextEdit.setFont(new QFont("Courier New"));
        gameWorldLogTextEdit.setMinimumSize(200, 30);

        QPushButton nextButton = new QPushButton(this);
        nextButton.setText("Смотреть матч");
        nextButton.clicked.connect(this, "onNextMatchButtonClicked()");

        QPushButton nextFastButton = new QPushButton(this);
        nextFastButton.setText("Симулировать матч");
        nextFastButton.clicked.connect(this, "onNextMatchFastButtonClicked()");

        nextMatchLabel = new QLabel(this);
        nextMatchLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);

        QLayout matchSimulationButtonLayout = new QHBoxLayout();
        matchSimulationButtonLayout.addWidget(nextButton);
        matchSimulationButtonLayout.addWidget(nextFastButton);

        QVBoxLayout leftLayout = new QVBoxLayout();
        leftLayout.setSpacing(5);
        leftLayout.addItem(competitionSelectorLayout);
        leftLayout.addWidget(gameWorldLogTextEdit);

        QVBoxLayout rightLayout = new QVBoxLayout();
        rightLayout.addWidget(nextMatchLabel);
        rightLayout.addItem(matchSimulationButtonLayout);

        QLayout mainLayout = new QHBoxLayout(this);
        mainLayout.addItem(leftLayout);
        mainLayout.addItem(rightLayout);

        resize(1200, 600);
        move(200, 50);
    }

    private void populateSeasonComboBox() {
        for (Season season : gameWorld.getSeasons()) {
            seasonComboBox.addItem(season.getSeasonCompetition().getName(), season);
        }
        seasonComboBox.setCurrentIndex(seasonComboBox.findData(gameWorld.getCurrentSeason()));
    }

    private void onSeasonComboBoxIndexChanged() {
        Season selectedSeason = (Season) seasonComboBox.itemData(seasonComboBox.currentIndex());
        tournamentComboBox.clear();
        tournamentComboBox.addItem("Все", null);
        for (Competition competition : selectedSeason.getSeasonCompetition().getStages()) {
            tournamentComboBox.addItem(competition.getName(), competition);
        }
        updateLogText();
    }

    private void onTournamentComboBoxIndexChanged() {
        Competition selectedTournament = (Competition) tournamentComboBox.itemData(tournamentComboBox.currentIndex());
        if (!(selectedTournament instanceof MultiStageCompetition)) {
            stageComboBox.clear();
            stageComboBox.setEnabled(false);
            updateLogText();
        } else {
            MultiStageCompetition multiStageCompetition = (MultiStageCompetition) selectedTournament;
            stageComboBox.clear();
            stageComboBox.addItem("Все", null);
            for (Competition competition : multiStageCompetition.getStages()) {
                stageComboBox.addItem(competition.getName(), competition);
            }
            stageComboBox.setEnabled(true);
        }
    }

    private void onStageComboBoxIndexChanged() {
        Competition selectedStage = (Competition) stageComboBox.itemData(stageComboBox.currentIndex());
        if (!(selectedStage instanceof MultiStageCompetition)) {
            subStageComboBox.clear();
            subStageComboBox.setEnabled(false);
            updateLogText();
        } else {
            MultiStageCompetition multiStageCompetition = (MultiStageCompetition) selectedStage;
            subStageComboBox.clear();
            subStageComboBox.addItem("Все", null);
            for (Competition competition : multiStageCompetition.getStages()) {
                subStageComboBox.addItem(competition.getName(), competition);
            }
            subStageComboBox.setEnabled(true);
        }
    }

    private void onSubStageComboBoxIndexChanged() {
        updateLogText();
    }

    private void onNextMatchButtonClicked() {
        MatchEvent match = gameWorld.getCurrentSeason().getNextMatch();
        MatchDialog matchDialog = new MatchDialog(match, this);
        matchDialog.exec();
        MatchScore score = matchDialog.getFinalScore();
        applyMatchResult(match, score);
    }

    private void onNextMatchFastButtonClicked() {
        MatchEvent match = gameWorld.getCurrentSeason().getNextMatch();
        MatchSimulator matchSimulator = new MatchSimulator(match.createMatchSpec());
        MatchSimulator.State state;
        do {
            state = matchSimulator.proceed();
        } while (state != MatchSimulator.State.MATCH_ENDED);
        MatchScore score = matchSimulator.getCurrentScore();
        applyMatchResult(match, score);
    }

    private void applyMatchResult(MatchEvent match, MatchScore score) {
        selectCompetition(match.getCompetition());
        gameWorld.getCurrentSeason().processMatch(match, score);
        updateLogText();
        updateNextMatchLabel();
    }

    private void updateLogText() {
        Competition selectedCompetition = getSelectedCompetition();
        gameWorldLogTextEdit.setPlainText(selectedCompetition.printToString());
    }

    private Competition getSelectedCompetition() {
        int subStageComboBoxIndex = subStageComboBox.currentIndex();
        if (subStageComboBoxIndex != -1) {
            Competition subStage = (Competition) subStageComboBox.itemData(subStageComboBoxIndex);
            if (subStage != null) {
                return subStage;
            }
        }

        int stageComboBoxIndex = stageComboBox.currentIndex();
        if (stageComboBoxIndex != -1) {
            Competition stage = (Competition) stageComboBox.itemData(stageComboBoxIndex);
            if (stage != null) {
                return stage;
            }
        }

        int tournamentComboBoxIndex = tournamentComboBox.currentIndex();
        if (tournamentComboBoxIndex != -1) {
            Competition tournament = (Competition) tournamentComboBox.itemData(tournamentComboBoxIndex);
            if (tournament != null) {
                return tournament;
            }
        }

        return ((Season) seasonComboBox.itemData(seasonComboBox.currentIndex())).getSeasonCompetition();
    }

    private void selectCompetition(Competition competition) {
        seasonComboBox.setCurrentIndex(seasonComboBox.findData(competition.getSeason()));

        Competition tournament = competition;
        int tournamentComboBoxIndex;
        while (true) {
            tournamentComboBoxIndex = tournamentComboBox.findData(tournament);
            if (tournamentComboBoxIndex != -1) {
                break;
            }
            tournament = tournament.getParent();
        }
        tournamentComboBox.setCurrentIndex(tournamentComboBoxIndex);
        if (tournament == competition) {
            stageComboBox.setCurrentIndex(stageComboBox.findData(null));
            return;
        }

        Competition stage = competition;
        int stageComboBoxIndex;
        while (true) {
            stageComboBoxIndex = stageComboBox.findData(stage);
            if (stageComboBoxIndex != -1) {
                break;
            }
            stage = stage.getParent();
        }
        stageComboBox.setCurrentIndex(stageComboBoxIndex);
        if (stage == competition) {
            subStageComboBox.setCurrentIndex(subStageComboBox.findData(null));
            return;
        }

        subStageComboBox.setCurrentIndex(subStageComboBox.findData(competition));
    }

    private void updateNextMatchLabel() {
        MatchEvent nextMatch = gameWorld.getCurrentSeason().getNextMatch();
        if (nextMatch == null) {
            return;
        }
        nextMatchLabel.setText("Следующий матч:<br>" + nextMatch.getCompetition().getFullName(false) +
                "<br>" + nextMatch.toString());
    }
}

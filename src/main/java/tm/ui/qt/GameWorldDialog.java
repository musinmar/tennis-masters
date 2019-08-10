package tm.ui.qt;

import com.trolltech.qt.core.QMargins;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPlainTextEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QSpacerItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.GameWorld;
import tm.lib.domain.world.Season;
import tm.lib.engine.StrategyProvider;
import tm.lib.engine.strategies.NeuralNetworkStrategy;
import tm.lib.engine.strategies.StandardStrategy;
import tm.ui.qt.simulation.SimulationHelper;

public class GameWorldDialog extends QDialog {

    private final GameWorld gameWorld;

    private QPlainTextEdit gameWorldLogTextEdit;
    private QComboBox seasonComboBox;
    private QComboBox tournamentComboBox;
    private QComboBox stageComboBox;
    private QComboBox subStageComboBox;
    private QLabel nextMatchLabel;
    private QLabel previousMatchLabel;

    private NeuralNetworkTeacher neuralNetworkTeacher;

    public GameWorldDialog(GameWorld gameWorld, QWidget parent) {
        super(parent);
        this.gameWorld = gameWorld;
        this.neuralNetworkTeacher = new NeuralNetworkTeacher(gameWorld);
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

        QPushButton teachNeuralNetworkButton = new QPushButton(this);
        teachNeuralNetworkButton.setText("Обучить нейронную сеть");
        teachNeuralNetworkButton.clicked.connect(this, "onTeachNeuralNetworkButtonClicked()");

        QPushButton saveNeuralNetworkButton = new QPushButton(this);
        saveNeuralNetworkButton.setText("Сохранить нейронную сеть");
        saveNeuralNetworkButton.clicked.connect(this, "onSaveNeuralNetworkButtonClicked()");

        QPushButton showEloRatingButton = new QPushButton(this);
        showEloRatingButton.setText("Рейтинг");
        showEloRatingButton.clicked.connect(this, "onShowEloRatingButtonClicked()");

        QHBoxLayout bottomButtonsLayout = new QHBoxLayout();
        bottomButtonsLayout.addWidget(teachNeuralNetworkButton);
        bottomButtonsLayout.addWidget(saveNeuralNetworkButton);
        bottomButtonsLayout.addWidget(showEloRatingButton);
        bottomButtonsLayout.addSpacerItem(new QSpacerItem(10, 10, QSizePolicy.Policy.Expanding));

        QPushButton nextButton = new QPushButton(this);
        nextButton.setText("Смотреть матч");
        nextButton.clicked.connect(this, "onNextMatchButtonClicked()");

        QPushButton nextFastButton = new QPushButton(this);
        nextFastButton.setText("Симулировать матч");
        nextFastButton.clicked.connect(this, "onNextMatchFastButtonClicked()");

        previousMatchLabel = new QLabel(this);
        previousMatchLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);

        nextMatchLabel = new QLabel(this);
        nextMatchLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);

        QLayout matchSimulationButtonLayout = new QHBoxLayout();
        matchSimulationButtonLayout.addWidget(nextButton);
        matchSimulationButtonLayout.addWidget(nextFastButton);

        QVBoxLayout leftLayout = new QVBoxLayout();
        leftLayout.setSpacing(5);
        leftLayout.addItem(competitionSelectorLayout);
        leftLayout.addWidget(gameWorldLogTextEdit);
        leftLayout.addItem(bottomButtonsLayout);

        QVBoxLayout rightLayout = new QVBoxLayout();
        rightLayout.addWidget(previousMatchLabel);
        rightLayout.addWidget(nextMatchLabel);
        rightLayout.addItem(matchSimulationButtonLayout);

        QLayout mainLayout = new QHBoxLayout();
        mainLayout.setContentsMargins(new QMargins(2, 2, 2, 2));
        mainLayout.addItem(leftLayout);
        mainLayout.addItem(rightLayout);

        QMenu toolsMenu = new QMenu("Инструменты");
        QAction showMatchConfigurationDialogAction = toolsMenu.addAction("Настроить матч");
        showMatchConfigurationDialogAction.triggered.connect(this, "onShowMatchConfigurationDialogActionTriggered()");

        QMenuBar menuBar = new QMenuBar(this);
        menuBar.addMenu(toolsMenu);

        QVBoxLayout menuBarLayout = new QVBoxLayout(this);
        menuBarLayout.setSpacing(3);
        menuBarLayout.setContentsMargins(0, 0, 0, 0);
        menuBarLayout.addWidget(menuBar);
        menuBarLayout.addItem(mainLayout);

        QAction simulateNextMatchAction = new QAction(this);
        simulateNextMatchAction.setShortcut("Space");
        simulateNextMatchAction.triggered.connect(nextFastButton.clicked);
        addAction(simulateNextMatchAction);

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
        //StrategyProvider strategyProvider = new StrategyProvider(new NeuralNetworkStrategy(neuralNetworkTeacher.getBestFoundPerceptron()), new StandardStrategy());
        MatchScore score = SimulationHelper.showMatch(match.createMatchSpec(), StrategyProvider.standard(), this);
        applyMatchResult(match, score);
    }

    private void onNextMatchFastButtonClicked() {
        MatchEvent match = gameWorld.getCurrentSeason().getNextMatch();
        //StrategyProvider strategyProvider = new StrategyProvider(new NeuralNetworkStrategy(neuralNetworkTeacher.getBestFoundPerceptron()), new StandardStrategy());
        MatchScore score = SimulationHelper.simulateMatch(match.createMatchSpec(), StrategyProvider.standard());
        applyMatchResult(match, score);
    }

    private void applyMatchResult(MatchEvent match, MatchScore score) {
        selectCompetition(match.getCompetition());
        gameWorld.getCurrentSeason().processMatch(match, score);
        updateLogText();
        updatePreviousMatchLabel(match);
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

    private void updatePreviousMatchLabel(MatchEvent match) {
        previousMatchLabel.setText("Последний матч:" + createMatchDescription(match));
    }

    private void updateNextMatchLabel() {
        MatchEvent nextMatch = gameWorld.getCurrentSeason().getNextMatch();
        if (nextMatch == null) {
            return;
        }
        nextMatchLabel.setText("Следующий матч:" + createMatchDescription(nextMatch));
    }

    private String createMatchDescription(MatchEvent match) {
        return String.format("<br>%s<br>%s",
                match.getCompetition().getFullName(false),
                match.toString());
    }

    private void onShowEloRatingButtonClicked() {
        EloRatingDialog eloRatingDialog = new EloRatingDialog(this, gameWorld.getEloRating());
        eloRatingDialog.exec();
    }

    private void onTeachNeuralNetworkButtonClicked() {
        neuralNetworkTeacher.teachWithCustomEvolution();
    }

    private void onSaveNeuralNetworkButtonClicked() {
        String saveFileName = QFileDialog.getSaveFileName(this, "Файл ANN");
        neuralNetworkTeacher.getBestFoundPerceptron().save(saveFileName);
    }

    private void onShowMatchConfigurationDialogActionTriggered() {
        MatchConfigurationDialog matchConfigurationDialog = new MatchConfigurationDialog(gameWorld, this);
        matchConfigurationDialog.exec();
    }
}

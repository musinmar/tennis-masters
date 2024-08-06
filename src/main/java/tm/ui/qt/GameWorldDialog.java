package tm.ui.qt;

import io.qt.core.QMargins;
import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.gui.QAction;
import io.qt.gui.QFont;
import io.qt.gui.QTextCursor;
import io.qt.widgets.QApplication;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QDialog;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLayout;
import io.qt.widgets.QMenu;
import io.qt.widgets.QMenuBar;
import io.qt.widgets.QMessageBox;
import io.qt.widgets.QMessageBox.StandardButtons;
import io.qt.widgets.QPlainTextEdit;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QSpacerItem;
import io.qt.widgets.QTabWidget;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;
import tm.lib.domain.competition.SeasonCompetition;
import tm.lib.domain.competition.base.Competition;
import tm.lib.domain.competition.base.MatchEvent;
import tm.lib.domain.competition.base.MultiStageCompetition;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.PersistenceManager;
import tm.lib.domain.world.World;
import tm.lib.domain.world.WorldLogger;
import tm.lib.engine.StrategyProvider;
import tm.ui.qt.simulation.SimulationHelper;

import java.util.function.BiFunction;

import static io.qt.widgets.QMessageBox.StandardButton.Cancel;
import static io.qt.widgets.QMessageBox.StandardButton.No;
import static io.qt.widgets.QMessageBox.StandardButton.Yes;

public class GameWorldDialog extends QDialog {

    public static final String WORLD_INITIALIZATION_FAILURE_ERROR_TITLE = "World initialization failure";
    private World world;

    private QPlainTextEdit competitionBrowserTextEdit;
    private QComboBox seasonComboBox;
    private QComboBox tournamentComboBox;
    private QComboBox stageComboBox;
    private QComboBox subStageComboBox;
    private QLabel nextMatchLabel;
    private QLabel previousMatchLabel;
    private QPlainTextEdit seasonLogTextEdit;
    private NationRatingWidget nationRatingWidget;
    private EloRatingWidget eloRatingWidget;

    public GameWorldDialog(QWidget parent) {
        super(parent);
        setupUi();
        QTimer.singleShot(0, this::initializeWorld);
    }

    private void setupUi() {
        setWindowTitle("Игровой мир");

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

        competitionBrowserTextEdit = new QPlainTextEdit(this);
        competitionBrowserTextEdit.setFont(new QFont("Courier New"));
        competitionBrowserTextEdit.setMinimumSize(200, 30);
        competitionBrowserTextEdit.setReadOnly(true);

        QVBoxLayout seasonBrowserLayout = new QVBoxLayout();
        seasonBrowserLayout.setSpacing(5);
        seasonBrowserLayout.addItem(competitionSelectorLayout);
        seasonBrowserLayout.addWidget(competitionBrowserTextEdit);

        QWidget seasonBrowserWidget = new QWidget(this);
        seasonBrowserWidget.setLayout(seasonBrowserLayout);

        seasonLogTextEdit = new QPlainTextEdit(this);
        seasonLogTextEdit.setFont(new QFont("Courier New"));
        seasonLogTextEdit.setReadOnly(true);

        QVBoxLayout logWidgetLayout = new QVBoxLayout();
        logWidgetLayout.addWidget(seasonLogTextEdit);

        QWidget logWidget = new QWidget(this);
        logWidget.setLayout(logWidgetLayout);

        nationRatingWidget = new NationRatingWidget(this);
        eloRatingWidget = new EloRatingWidget(this);

        QTabWidget tabWidget = new QTabWidget(this);
        tabWidget.addTab(seasonLogTextEdit, "Лог");
        tabWidget.addTab(seasonBrowserWidget, "Турниры");
        tabWidget.addTab(eloRatingWidget, "Рейтинг игроков");
        tabWidget.addTab(nationRatingWidget, "Рейтинг наций");

        QPushButton nextButton = new QPushButton(this);
        nextButton.setText("Смотреть матч");
        nextButton.clicked.connect(this, "onNextMatchButtonClicked()");

        QPushButton nextFastButton = new QPushButton(this);
        nextFastButton.setText("Симулировать матч");
        nextFastButton.clicked.connect(this, "onNextMatchFastButtonClicked()");

        QHBoxLayout bottomButtonsLayout = new QHBoxLayout();
        bottomButtonsLayout.addSpacerItem(new QSpacerItem(10, 10, QSizePolicy.Policy.Expanding));
        bottomButtonsLayout.addWidget(nextButton);
        bottomButtonsLayout.addWidget(nextFastButton);

        previousMatchLabel = new QLabel(this);
        previousMatchLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);

        nextMatchLabel = new QLabel(this);
        nextMatchLabel.setAlignment(Qt.AlignmentFlag.AlignCenter);

        QHBoxLayout matchLabelsLayout = new QHBoxLayout();
        matchLabelsLayout.addWidget(previousMatchLabel);
        matchLabelsLayout.addWidget(nextMatchLabel);

        QVBoxLayout leftLayout = new QVBoxLayout();
        leftLayout.setSpacing(5);
        leftLayout.addWidget(tabWidget);
        leftLayout.addItem(matchLabelsLayout);
        leftLayout.addItem(bottomButtonsLayout);

        QLayout mainLayout = new QHBoxLayout();
        mainLayout.setContentsMargins(new QMargins(2, 2, 2, 2));
        mainLayout.addItem(leftLayout);

        QMenu toolsMenu = new QMenu("Инструменты");
        QAction showMatchConfigurationDialogAction = toolsMenu.addAction("Сыграть матч");
        showMatchConfigurationDialogAction.triggered.connect(this, "onShowMatchConfigurationDialogActionTriggered()");
        QAction teachAnnDialogAction = toolsMenu.addAction("Обучить сеть");
        teachAnnDialogAction.triggered.connect(this, "onTeachAnnDialogActionTriggered()");

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

    private void initializeWorld() {
        World world;
        if (PersistenceManager.canLoadWorld()) {
            world = loadExistingWorld();
        } else {
            world = createNewWorld();
        }
        setWorld(world);
    }

    private World loadExistingWorld() {
        World world;
        try {
            world = PersistenceManager.loadWorld();
        } catch (Exception e) {
            StandardButtons buttons = QMessageBox.StandardButton.flags(Yes, No);
            var result = QMessageBox.question(
                    this,
                    WORLD_INITIALIZATION_FAILURE_ERROR_TITLE,
                    "Failed to load existing world, do you want to create a new one?",
                    buttons);
            if (result == Yes) {
                world = createNewWorld();
            } else {
                QApplication.quit();
                throw e;
            }
        }
        return world;
    }

    private World createNewWorld() {
        World world;
        try {
            world = World.createNewWorld();
        } catch (Exception e) {
            QMessageBox.critical(this, WORLD_INITIALIZATION_FAILURE_ERROR_TITLE, "Failed to initialize new world");
            QApplication.quit();
            throw e;
        }
        return world;
    }

    private void setWorld(World world) {
        this.world = world;
        setWindowTitle("Игровой мир - Сезон " + (world.getYear() + 1));
        nationRatingWidget.setNationRating(world.getNationRating());
        eloRatingWidget.setEloRating(world.getEloRating());
        populateSeasonComboBox();
        updateLogText();
        updateNextMatchLabel();
        configureGameWorldLogger();
    }

    private void populateSeasonComboBox() {
        seasonComboBox.clear();
        for (SeasonCompetition season : world.getSeasons()) {
            seasonComboBox.addItem(season.getName(), season);
        }
        seasonComboBox.setCurrentIndex(seasonComboBox.findData(world.getCurrentSeason()));
    }

    private void onSeasonComboBoxIndexChanged() {
        SeasonCompetition selectedSeason = (SeasonCompetition) seasonComboBox.itemData(seasonComboBox.currentIndex());
        if (selectedSeason != null) {
            tournamentComboBox.clear();
            tournamentComboBox.addItem("Все", null);
            for (Competition competition : selectedSeason.getStages()) {
                tournamentComboBox.addItem(competition.getName(), competition);
            }
        } else {
            tournamentComboBox.clear();
            tournamentComboBox.setEnabled(false);
            updateLogText();
        }
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
        runWithSimulationRunner((match, strategyProvider) -> SimulationHelper.showMatch(match, strategyProvider, this));
    }

    private void onNextMatchFastButtonClicked() {
        runWithSimulationRunner(SimulationHelper::simulateMatch);
    }

    private void runWithSimulationRunner(BiFunction<Match, StrategyProvider, MatchScore> simulationRunner) {
        MatchEvent match = world.getCurrentSeason().getNextMatch();
        if (match != null) {
            //StrategyProvider strategyProvider = new StrategyProvider(new NeuralNetworkStrategy(neuralNetworkTeacher.getBestFoundPerceptron()), new StandardStrategy());
            MatchScore score = simulationRunner.apply(match.createMatchSpec(), StrategyProvider.standard());
            applyMatchResult(match, score);
        } else if (!world.isSeasonFinished()) {
            world.finishSeason();
            PersistenceManager.saveWorld(world);
        } else {
            QMessageBox.StandardButton reply = QMessageBox.question(this, "Новый сезон",
                    "Хотите начать новый сезон?", new StandardButtons(Yes, No, Cancel));
            if (reply == Yes) {
                startNewSeason();
            }
        }
    }

    private void applyMatchResult(MatchEvent match, MatchScore score) {
        selectCompetition(match.getCompetition());
        world.processMatch(match, score);
        updateLogText();
        updatePreviousMatchLabel(match);
        updateNextMatchLabel();
        eloRatingWidget.repopulateEloRatingList();
        nationRatingWidget.repopulateNationRatingWidget();
    }

    private void updateLogText() {
        Competition selectedCompetition = getSelectedCompetition();
        competitionBrowserTextEdit.setPlainText(selectedCompetition.printToString());
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

        return (SeasonCompetition) seasonComboBox.itemData(seasonComboBox.currentIndex());
    }

    private void selectCompetition(Competition competition) {
        Competition season = competition;
        int seasonComboBoxIndex;
        while (true) {
            seasonComboBoxIndex = seasonComboBox.findData(season);
            if (seasonComboBoxIndex != -1) {
                break;
            }
            season = season.getParent();
        }
        seasonComboBox.setCurrentIndex(seasonComboBoxIndex);
        if (season == competition) {
            tournamentComboBox.setCurrentIndex(tournamentComboBox.findData(null));
            return;
        }

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
        MatchEvent nextMatch = world.getCurrentSeason().getNextMatch();
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

    private void onShowMatchConfigurationDialogActionTriggered() {
        MatchConfigurationDialog matchConfigurationDialog = new MatchConfigurationDialog(world, this);
        matchConfigurationDialog.exec();
    }

    private void onTeachAnnDialogActionTriggered() {
        TeachNeuralNetworkDialog teachNeuralNetworkDialog = new TeachNeuralNetworkDialog(world, this);
        teachNeuralNetworkDialog.exec();
    }

    private void configureGameWorldLogger() {
        WorldLogger logger = new WorldLogger() {
            @Override
            public void print(String str) {
                seasonLogTextEdit.moveCursor(QTextCursor.MoveOperation.End);
                seasonLogTextEdit.insertPlainText(str);
            }

            @Override
            public void println() {
                seasonLogTextEdit.appendPlainText("");
            }

            @Override
            public void println(String str) {
                seasonLogTextEdit.appendPlainText(str);
            }

            @Override
            public void println(String formatString, Object... args) {
                seasonLogTextEdit.appendPlainText(String.format(formatString, args));
            }
        };
        world.setLogger(logger);
    }

    private void startNewSeason() {
        seasonLogTextEdit.clear();
        world.startNewSeason();
        populateSeasonComboBox();
        updateNextMatchLabel();
        nationRatingWidget.repopulateNationRatingWidget();
        eloRatingWidget.repopulateEloRatingList();
    }
}

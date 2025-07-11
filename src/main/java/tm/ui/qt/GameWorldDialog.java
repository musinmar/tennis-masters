package tm.ui.qt;

import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.gui.QAction;
import io.qt.gui.QFont;
import io.qt.gui.QTextCursor;
import io.qt.widgets.QApplication;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QDockWidget;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QMainWindow;
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
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.CompetitionPrinter;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.World;
import tm.lib.domain.world.WorldLogger;
import tm.lib.domain.world.event.SeasonConclusion;
import tm.lib.domain.world.event.SeasonMatchEvent;
import tm.lib.domain.world.event.SeasonStart;
import tm.lib.domain.world.event.WorldEvent;
import tm.lib.domain.world.event.YearProgression;
import tm.lib.engine.StrategyProvider;
import tm.lib.persistence.PersistenceManager;
import tm.ui.qt.simulation.SimulationHelper;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static io.qt.widgets.QMessageBox.StandardButton.Cancel;
import static io.qt.widgets.QMessageBox.StandardButton.No;
import static io.qt.widgets.QMessageBox.StandardButton.Yes;

public class GameWorldDialog extends QMainWindow {

    public static final String WORLD_INITIALIZATION_FAILURE_ERROR_TITLE = "World initialization failure";
    public static final String PLAYERS_PATH = "players";
    private World world;

    private QPlainTextEdit competitionBrowserTextEdit;
    private QComboBox seasonComboBox;
    private QComboBox tournamentComboBox;
    private QComboBox stageComboBox;
    private QComboBox subStageComboBox;
    private QLabel nextEventLabel;
    private QLabel previousEventLabel;
    private QPushButton nextEventButton;
    private QPushButton nextEventFastButton;
    private QPlainTextEdit seasonLogTextEdit;
    private NationRatingWidget nationRatingWidget;
    private EloRatingWidget eloRatingWidget;

    public GameWorldDialog(QWidget parent) {
        super(parent);
        setupUi();
        QTimer.singleShot(0, this::initializeWorld);
    }

    private void setupUi() {
        setWindowTitle("Game World");

        QLabel seasonComboBoxLabel = new QLabel(this);
        seasonComboBoxLabel.setText("Season:");
        seasonComboBox = new QComboBox(this);
        seasonComboBox.currentIndexChanged.connect(this::onSeasonComboBoxIndexChanged);

        QLabel tournamentComboBoxLabel = new QLabel(this);
        tournamentComboBoxLabel.setText("Competition:");
        tournamentComboBox = new QComboBox(this);
        tournamentComboBox.currentIndexChanged.connect(this::onTournamentComboBoxIndexChanged);

        QLabel stageComboBoxLabel = new QLabel(this);
        stageComboBoxLabel.setText("Stage:");
        stageComboBox = new QComboBox(this);
        stageComboBox.currentIndexChanged.connect(this::onStageComboBoxIndexChanged);

        QLabel subStageComboBoxLabel = new QLabel(this);
        subStageComboBoxLabel.setText("Group/round:");
        subStageComboBox = new QComboBox(this);
        subStageComboBox.setEnabled(false);
        subStageComboBox.currentIndexChanged.connect(this::onSubStageComboBoxIndexChanged);

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
        QDockWidget eloRatingDockWidget = new QDockWidget(this);
        eloRatingDockWidget.setWindowTitle("Player Rating");
        eloRatingDockWidget.setWidget(eloRatingWidget);
        addDockWidget(Qt.DockWidgetArea.RightDockWidgetArea, eloRatingDockWidget);

        QTabWidget tabWidget = new QTabWidget(this);
        tabWidget.addTab(seasonLogTextEdit, "Log");
        tabWidget.addTab(seasonBrowserWidget, "Tournaments");
        tabWidget.addTab(nationRatingWidget, "Nation Rating");

        nextEventButton = new QPushButton(this);
        nextEventButton.setText("Watch Match");
        nextEventButton.clicked.connect(this::onNextMatchButtonClicked);

        nextEventFastButton = new QPushButton(this);
        nextEventFastButton.clicked.connect(this::onNextEventFastButtonClicked);

        QHBoxLayout bottomButtonsLayout = new QHBoxLayout();
        bottomButtonsLayout.addSpacerItem(new QSpacerItem(10, 10, QSizePolicy.Policy.Expanding));
        bottomButtonsLayout.addWidget(nextEventButton);
        bottomButtonsLayout.addWidget(nextEventFastButton);

        previousEventLabel = createMatchLabel();
        nextEventLabel = createMatchLabel();

        QHBoxLayout matchLabelsLayout = new QHBoxLayout();
        matchLabelsLayout.addWidget(previousEventLabel);
        matchLabelsLayout.addWidget(nextEventLabel);

        QVBoxLayout leftLayout = new QVBoxLayout();
        leftLayout.setSpacing(5);
        leftLayout.addWidget(tabWidget);
        leftLayout.addItem(matchLabelsLayout);
        leftLayout.addItem(bottomButtonsLayout);

        QWidget centralWidget = new QWidget(this);
        centralWidget.setLayout(leftLayout);
        setCentralWidget(centralWidget);

        QMenu fileMenu = new QMenu("File");
        QAction restartGame = fileMenu.addAction("Restart");
        restartGame.triggered.connect(this::onRestartGameActionTriggered);

        QAction saveGameAction = fileMenu.addAction("Save");
        saveGameAction.triggered.connect(this::onSaveGameActionTriggered);

        QMenu toolsMenu = new QMenu("Tools");
        QAction showMatchConfigurationDialogAction = toolsMenu.addAction("Play Match");
        showMatchConfigurationDialogAction.triggered.connect(this::onShowMatchConfigurationDialogActionTriggered);
        QAction teachAnnDialogAction = toolsMenu.addAction("Train Network");
        teachAnnDialogAction.triggered.connect(this::onTeachAnnDialogActionTriggered);

        QMenuBar menuBar = new QMenuBar(this);
        menuBar.addMenu(fileMenu);
        menuBar.addMenu(toolsMenu);
        setMenuBar(menuBar);

        QAction simulateNextMatchAction = new QAction(this);
        simulateNextMatchAction.setShortcut("Space");
        simulateNextMatchAction.triggered.connect(nextEventFastButton.clicked);
        addAction(simulateNextMatchAction);

        resize(1200, 600);
        move(200, 50);
    }

    private QLabel createMatchLabel() {
        var nextMatchLabel1 = new QLabel(this);
        nextMatchLabel1.setAlignment(Qt.AlignmentFlag.AlignCenter);
        nextMatchLabel1.linkActivated.connect(this::onMatchLinkActivated);
        return nextMatchLabel1;
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
            StandardMessageBoxes.showError(e, this);
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
        configureGameWorldLogger();
        nationRatingWidget.setNationRating(world.getNationRating());
        eloRatingWidget.setEloRating(world.getEloRating());

        updateWidgetsOnNewSeason();
        updateWidgetsOnWorldEvent();
    }

    private void populateSeasonComboBox() {
        seasonComboBox.clear();
        for (Competition season : List.of(world.getCurrentSeason())) {
            seasonComboBox.addItem(season.getName(), season);
        }
        seasonComboBox.setCurrentIndex(seasonComboBox.findData(world.getCurrentSeason()));
    }

    private void onSeasonComboBoxIndexChanged() {
        Competition selectedSeason = (Competition) seasonComboBox.itemData(seasonComboBox.currentIndex());
        if (selectedSeason != null) {
            tournamentComboBox.clear();
            tournamentComboBox.addItem("All", null);
            for (Competition competition : selectedSeason.getChildren()) {
                tournamentComboBox.addItem(competition.getName(), competition);
            }
            tournamentComboBox.setEnabled(true);
        } else {
            tournamentComboBox.clear();
            tournamentComboBox.setEnabled(false);
            updateLogText();
        }
    }

    private void onTournamentComboBoxIndexChanged() {
        Competition selectedTournament = (Competition) tournamentComboBox.itemData(tournamentComboBox.currentIndex());
        if (selectedTournament == null || !selectedTournament.hasChildren()) {
            stageComboBox.clear();
            stageComboBox.setEnabled(false);
            updateLogText();
        } else {
            stageComboBox.clear();
            stageComboBox.addItem("All", null);
            for (Competition competition : selectedTournament.getChildren()) {
                stageComboBox.addItem(competition.getName(), competition);
            }
            stageComboBox.setEnabled(true);
        }
    }

    private void onStageComboBoxIndexChanged() {
        Competition selectedStage = (Competition) stageComboBox.itemData(stageComboBox.currentIndex());
        if (selectedStage == null || !selectedStage.hasChildren()) {
            subStageComboBox.clear();
            subStageComboBox.setEnabled(false);
            updateLogText();
        } else {
            subStageComboBox.clear();
            subStageComboBox.addItem("All", null);
            for (Competition competition : selectedStage.getChildren()) {
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

    private void onNextEventFastButtonClicked() {
        runWithSimulationRunner(SimulationHelper::simulateMatch);
    }

    private void runWithSimulationRunner(BiFunction<Match, StrategyProvider, MatchScore> simulationRunner) {
        var event = world.getNextEvent();
        switch (event) {
            case SeasonStart _, SeasonConclusion _ -> world.progressState();
            case YearProgression _ -> {
                QMessageBox.StandardButton reply = QMessageBox.question(this, "New Season",
                        "Do you want to start a new season?", new StandardButtons(Yes, No, Cancel));
                if (reply == Yes) {
                    world.progressState();
                    updateWidgetsOnNewSeason();
                    PersistenceManager.saveWorld(world);
                } else {
                    return;
                }
            }
            case SeasonMatchEvent seasonMatchEvent -> {
                var match = seasonMatchEvent.getMatchEvent();
                MatchScore score = simulationRunner.apply(match.createMatchSpec(), StrategyProvider.standard());
                selectCompetition(match.getCompetition());
                world.processMatch(match, score);
            }
            case null, default -> throw new IllegalArgumentException("Unknown world event: " + event);
        }

        updatePreviousEventLabel(event);
        updateWidgetsOnWorldEvent();
    }

    private void updateWidgetsOnNewSeason() {
        setWindowTitle("Game World - Season " + (world.getYear() + 1));
        seasonLogTextEdit.clear();
        populateSeasonComboBox();
        updateLogText();
    }

    private void updateWidgetsOnWorldEvent() {
        updateLogText();
        updateNextMatchLabel();
        eloRatingWidget.repopulateEloRatingList();
        nationRatingWidget.repopulateNationRatingWidget();
    }

    private void updateLogText() {
        Competition selectedCompetition = getSelectedCompetition();
        String text = selectedCompetition == null ? "" : CompetitionPrinter.printToString(selectedCompetition);
        competitionBrowserTextEdit.setPlainText(text);
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

        return (Competition) seasonComboBox.itemData(seasonComboBox.currentIndex());
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

    private void updatePreviousEventLabel(WorldEvent event) {
        previousEventLabel.setText(createEventDescription("Last Event:", event));
    }

    private void updateNextMatchLabel() {
        var event = world.getNextEvent();
        nextEventLabel.setText(createEventDescription("Next Event:", event));
        if (event instanceof SeasonMatchEvent) {
            nextEventButton.setEnabled(true);
            nextEventFastButton.setText("Simulate Match");
        } else {
            nextEventButton.setEnabled(false);
            nextEventFastButton.setText("Next");
        }
    }

    private String createEventDescription(String description, WorldEvent event) {
        var detailedDescription = switch (event) {
            case SeasonStart _ -> "Season start";
            case SeasonConclusion _ -> "Season conclusion";
            case YearProgression _ -> "Year progression";
            case SeasonMatchEvent seasonMatchEvent -> {
                var match = seasonMatchEvent.getMatchEvent();
                var resultString = (match.getResult() == null) ? "" : " " + match.getResult();
                yield String.format("%s<br/>%s - %s%s",
                        match.getCompetition().getFullName(false),
                        buildPlayerEndpoint(match.getHomePlayer().getPlayer()),
                        buildPlayerEndpoint(match.getAwayPlayer().getPlayer()),
                        resultString
                );
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        };

        return String.format("<html>%s<br/>%s</html>", description, detailedDescription);
    }

    private String buildPlayerEndpoint(Optional<Knight> maybeKnight) {
        return maybeKnight.map(knight ->
                String.format("<a href=\"/%s/%d\">%s</a>", PLAYERS_PATH, knight.getId(), knight.getFullName())
        ).orElse("TBD");
    }

    private void onMatchLinkActivated(String reference) {
        URI uri = URI.create(reference);
        String path = uri.getPath();
        String[] pathElements = path.split("/");
        System.out.println(path);
        System.out.println(Arrays.toString(pathElements));
        if (PLAYERS_PATH.equals(pathElements[1])) {
            var playerId = Integer.valueOf(pathElements[2]);
            var player = world.getKnightRepository().getById(playerId);
            PlayerDialog playerDialog = new PlayerDialog(this, player);
            playerDialog.show();
        }
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

    private void onRestartGameActionTriggered() {
        setWorld(createNewWorld());
    }

    private void onSaveGameActionTriggered() {
        PersistenceManager.saveWorld(world);
    }
}

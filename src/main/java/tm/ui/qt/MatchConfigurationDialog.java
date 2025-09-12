package tm.ui.qt;

import io.qt.core.QEventLoop;
import io.qt.gui.QTextCursor;
import io.qt.widgets.QApplication;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QDialog;
import io.qt.widgets.QDialogButtonBox;
import io.qt.widgets.QFileDialog;
import io.qt.widgets.QGridLayout;
import io.qt.widgets.QGroupBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLineEdit;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSpacerItem;
import io.qt.widgets.QSpinBox;
import io.qt.widgets.QTextEdit;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Stadium;
import tm.lib.domain.world.World;
import tm.lib.engine.Side;
import tm.lib.engine.Strategy;
import tm.lib.engine.StrategyProvider;
import tm.lib.engine.strategies.NeuralNetworkStrategy;
import tm.lib.engine.strategies.StandardStrategy;
import tm.ui.qt.simulation.SimulationHelper;

import java.util.ArrayList;
import java.util.List;

import static io.qt.widgets.QDialogButtonBox.ButtonRole.RejectRole;
import static io.qt.widgets.QSizePolicy.Policy.Expanding;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static tm.ui.qt.simulation.WidgetsHelper.fillPlayerComboBox;

public class MatchConfigurationDialog extends QDialog {

    public static final String HARD_CODED_STRATEGY_NAME = "Hard-coded";
    public static final String ANN_STRATEGY_NAME = "ANN";

    private final World world;
    private PlayerSelector homePlayerSelector;
    private PlayerSelector awayPlayerSelector;
    private QTextEdit resultTextEdit;
    private QSpinBox simulationCountSpinBox;
    private QLabel simulationStatusLabel;
    private QSpinBox setCountSpinBox;

    private static class PlayerSelector {
        QGroupBox rootGroupBox;
        QComboBox playerComboBox;
        QComboBox strategyComboBox;
        QLabel annLabel;
        QLineEdit annPathEdit;
        QPushButton selectNetworkButton;
    }

    public MatchConfigurationDialog(World world, QWidget parent) {
        super(parent);
        this.world = world;
        setupUi();
    }

    private void setupUi() {
        homePlayerSelector = createPlayerSelector(Side.HOME);
        awayPlayerSelector = createPlayerSelector(Side.AWAY);

        QHBoxLayout playerSelectorsLayout = new QHBoxLayout();
        playerSelectorsLayout.addWidget(homePlayerSelector.rootGroupBox);
        playerSelectorsLayout.addWidget(awayPlayerSelector.rootGroupBox);

        QGroupBox simulationGroupBox = createSimulationGroupBox();

        QDialogButtonBox buttonBox = new QDialogButtonBox(this);
        buttonBox.addButton("Close", RejectRole);
        buttonBox.rejected.connect(this::onCloseButtonClicked);

        QVBoxLayout mainLayout = new QVBoxLayout(this);
        mainLayout.addLayout(playerSelectorsLayout);
        mainLayout.addWidget(simulationGroupBox);
        mainLayout.addWidget(buttonBox);

        setWindowTitle("Configure Match");
        resize(650, 300);
    }

    private PlayerSelector createPlayerSelector(Side side) {
        PlayerSelector playerSelector = new PlayerSelector();

        QLabel playerLabel = new QLabel("Player:", this);
        playerSelector.playerComboBox = new QComboBox(this);
        fillPlayerComboBox(playerSelector.playerComboBox, world);
        QLabel strategyLabel = new QLabel("Strategy:", this);
        playerSelector.strategyComboBox = new QComboBox(this);
        playerSelector.strategyComboBox.addItem(HARD_CODED_STRATEGY_NAME);
        playerSelector.strategyComboBox.addItem(ANN_STRATEGY_NAME);

        playerSelector.annLabel = new QLabel("ANN:", this);
        playerSelector.annPathEdit = new QLineEdit(this);
        playerSelector.selectNetworkButton = new QPushButton(this);
        playerSelector.selectNetworkButton.setText("Select");
        playerSelector.selectNetworkButton.clicked.connect(this::onSelectAnnPathButtonClicked);

        playerSelector.strategyComboBox.currentTextChanged.connect(text ->
                onStrategyComboBoxTextChanged(text, playerSelector));
        playerSelector.strategyComboBox.setCurrentText(HARD_CODED_STRATEGY_NAME);
        onStrategyComboBoxTextChanged(HARD_CODED_STRATEGY_NAME, playerSelector);

        playerSelector.rootGroupBox = new QGroupBox();
        String title = side == Side.HOME ? "Home Player" : "Away Player";
        playerSelector.rootGroupBox.setTitle(title);

        QGridLayout groupBoxLayout = new QGridLayout();
        playerSelector.rootGroupBox.setLayout(groupBoxLayout);
        groupBoxLayout.addWidget(playerLabel, 0, 0);
        groupBoxLayout.addWidget(playerSelector.playerComboBox, 0, 1, 1, 2);
        groupBoxLayout.addWidget(strategyLabel, 1, 0);
        groupBoxLayout.addWidget(playerSelector.strategyComboBox, 1, 1, 1, 2);
        groupBoxLayout.addWidget(playerSelector.annLabel, 2, 0);
        groupBoxLayout.addWidget(playerSelector.annPathEdit, 2, 1);
        groupBoxLayout.addWidget(playerSelector.selectNetworkButton, 2, 2);

        return playerSelector;
    }

    private void onStrategyComboBoxTextChanged(String text, PlayerSelector playerSelector) {
        boolean enabled = text.equals(ANN_STRATEGY_NAME);
        playerSelector.annPathEdit.setEnabled(enabled);
        playerSelector.selectNetworkButton.setEnabled(enabled);
    }

    private QGroupBox createSimulationGroupBox() {
        QLabel setCountLabel = new QLabel("Number of sets:", this);
        setCountSpinBox = new QSpinBox(this);
        setCountSpinBox.setMinimum(2);
        setCountSpinBox.setMaximum(4);
        setCountSpinBox.setSingleStep(2);
        setCountSpinBox.setValue(2);

        QLabel simulationCountLabel = new QLabel("Number of simulations:", this);
        simulationCountSpinBox = new QSpinBox(this);
        simulationCountSpinBox.setMinimum(1);
        simulationCountSpinBox.setMaximum(Integer.MAX_VALUE);
        simulationCountSpinBox.setValue(1);

        QGridLayout gridLayout = new QGridLayout();
        gridLayout.setSpacing(10);
        gridLayout.addWidget(setCountLabel, 0, 0);
        gridLayout.addWidget(setCountSpinBox, 0, 1);
        gridLayout.addItem(new QSpacerItem(1, 1, Expanding), 0, 2);
        gridLayout.addWidget(simulationCountLabel, 1, 0);
        gridLayout.addWidget(simulationCountSpinBox, 1, 1);

        QPushButton showSimulationPushButton = new QPushButton("Show Match", this);
        showSimulationPushButton.clicked.connect(this::onShowSimulationPushButtonClicked);

        QPushButton fastSimulatePushButton = new QPushButton("Fast Simulation", this);
        fastSimulatePushButton.clicked.connect(this::onFastSimulatePushButtonClicked);

        simulationStatusLabel = new QLabel(this);

        QHBoxLayout buttonsLayout = new QHBoxLayout();
        buttonsLayout.addWidget(showSimulationPushButton);
        buttonsLayout.addWidget(fastSimulatePushButton);
        buttonsLayout.addWidget(simulationStatusLabel);
        buttonsLayout.addSpacerItem(new QSpacerItem(1, 1, Expanding));

        resultTextEdit = new QTextEdit(this);
        resultTextEdit.setReadOnly(true);

        QVBoxLayout groupBoxLayout = new QVBoxLayout();
        groupBoxLayout.addLayout(gridLayout);
        groupBoxLayout.addLayout(buttonsLayout);
        groupBoxLayout.addWidget(resultTextEdit);

        QGroupBox groupBox = new QGroupBox(this);
        groupBox.setTitle("Simulation");
        groupBox.setLayout(groupBoxLayout);

        return groupBox;
    }

    private void onCloseButtonClicked() {
        close();
    }

    private void onSelectAnnPathButtonClicked() {
        QLineEdit annPathEdit = sender() == homePlayerSelector.selectNetworkButton
                ? homePlayerSelector.annPathEdit
                : awayPlayerSelector.annPathEdit;
        QFileDialog.Result<String> selectedFileName = QFileDialog.getOpenFileName(
                this,
                "Select neural network",
                null,
                "ANN files (*.ann)"
        );
        if (selectedFileName != null && selectedFileName.result != null) {
            annPathEdit.setText(selectedFileName.result);
        }
    }

    private Match configureMatch() {
        Match match = new Match();
        match.setHomePlayer(((Knight) homePlayerSelector.playerComboBox.itemData(homePlayerSelector.playerComboBox.currentIndex())));
        match.setAwayPlayer(((Knight) awayPlayerSelector.playerComboBox.itemData(awayPlayerSelector.playerComboBox.currentIndex())));
        match.setSets(setCountSpinBox.value());
        match.setPlayOff(false);
        match.setVenue(Stadium.standard());
        return match;
    }

    private Strategy createStrategy(PlayerSelector selector) {
        String selectedStrategy = selector.strategyComboBox.currentText();
        if (selectedStrategy.equals(ANN_STRATEGY_NAME)) {
            String annPath = selector.annPathEdit.text();
            NeuralNetwork<?> perceptron = MultiLayerPerceptron.createFromFile(annPath);
            return new NeuralNetworkStrategy(perceptron);
        } else {
            return new StandardStrategy();
        }
    }

    private StrategyProvider configureStrategyProvider() {
        return new StrategyProvider(
                createStrategy(homePlayerSelector),
                createStrategy(awayPlayerSelector));
    }

    private void displayResult(Match match, MatchScore score) {
        resultTextEdit.setHtml(String.format("%s - %s<br>%s",
                match.getHomePlayer().getFullName(), match.getAwayPlayer().getFullName(), score));
    }

    private String formatBasicScore(BasicScore score) {
        double sum = score.v1 + score.v2;
        return String.format("%s (%.3f:%.3f)", score, score.v1 / sum, score.v2 / sum);
    }

    private void displayResults(Match match, List<MatchScore> scores) {
        BasicScore totalScoreByMatches = scores.stream()
                .map(MatchScore::getScoreBySets)
                .map(BasicScore::normalized)
                .reduce(BasicScore.of(0, 0), BasicScore::summedWith);
        BasicScore totalScoreBySets = scores.stream()
                .map(MatchScore::getScoreBySets)
                .reduce(BasicScore.of(0, 0), BasicScore::summedWith);
        BasicScore totalScoreByGames = scores.stream()
                .map(MatchScore::getScoreByGames)
                .reduce(BasicScore.of(0, 0), BasicScore::summedWith);

        resultTextEdit.setHtml(String.format("%s - %s", match.getHomePlayer().getFullName(), match.getAwayPlayer().getFullName()));
        resultTextEdit.append("Score by matches: " + formatBasicScore(totalScoreByMatches));
        resultTextEdit.append("Score by sets: " + formatBasicScore(totalScoreBySets));
        resultTextEdit.append("Score by games: " + formatBasicScore(totalScoreByGames));
        resultTextEdit.append("");
        scores.forEach(score -> resultTextEdit.append(score.toString()));
        resultTextEdit.moveCursor(QTextCursor.MoveOperation.Start);
    }

    private void onShowSimulationPushButtonClicked() {
        Match match = configureMatch();
        MatchScore score = SimulationHelper.showMatch(match, configureStrategyProvider(), this);
        displayResult(match, score);
    }

    private void onFastSimulatePushButtonClicked() {
        int count = simulationCountSpinBox.value();
        Match match = configureMatch();
        List<MatchScore> scores = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MatchScore score = SimulationHelper.simulateMatch(match, configureStrategyProvider());
            scores.add(score);
            simulationStatusLabel.setText("Matches played: " + (i + 1));
            QApplication.processEvents(QEventLoop.ProcessEventsFlag.ExcludeUserInputEvents);
        }
        displayResults(match, scores);
    }
}

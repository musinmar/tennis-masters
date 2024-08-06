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

    private World world;
    private PlayerSelector homePlayerSelector;
    private PlayerSelector awayPlayerSelector;
    private QTextEdit resultTextEdit;
    private QSpinBox simulationCountSpinBox;
    private QLabel simulationStatusLabel;
    private QSpinBox setCountSpinBox;

    private static class PlayerSelector {
        QGroupBox rootGroupBox;
        QComboBox playerComboBox;
        QLineEdit annPathEdit;
        QPushButton selectNetworkButton;
    }

    public MatchConfigurationDialog(World world, QWidget parent) {
        super(parent);
        this.world = world;
        setupUi();
    }

    private void setupUi() {
        homePlayerSelector = createPlayerSelector("Домашний игрок");
        awayPlayerSelector = createPlayerSelector("Выездной игрок");

        QHBoxLayout playerSelectorsLayout = new QHBoxLayout();
        playerSelectorsLayout.addWidget(homePlayerSelector.rootGroupBox);
        playerSelectorsLayout.addWidget(awayPlayerSelector.rootGroupBox);

        QGroupBox simulationGroupBox = createSimulationGroupBox();

        QDialogButtonBox buttonBox = new QDialogButtonBox(this);
        buttonBox.addButton("Закрыть", RejectRole);
        buttonBox.rejected.connect(this, "onCloseButtonClicked()");

        QVBoxLayout mainLayout = new QVBoxLayout(this);
        mainLayout.addLayout(playerSelectorsLayout);
        mainLayout.addWidget(simulationGroupBox);
        mainLayout.addWidget(buttonBox);

        setWindowTitle("Настроить матч");
        resize(600, 300);
    }

    private PlayerSelector createPlayerSelector(String title) {
        PlayerSelector playerSelector = new PlayerSelector();

        QLabel playerLabel = new QLabel("Игрок:", this);
        playerSelector.playerComboBox = new QComboBox(this);
        fillPlayerComboBox(playerSelector.playerComboBox, world);
        QLabel networkLabel = new QLabel("Сеть:", this);
        playerSelector.annPathEdit = new QLineEdit(this);
        playerSelector.selectNetworkButton = new QPushButton(this);
        playerSelector.selectNetworkButton.setText("Выбрать");
        playerSelector.selectNetworkButton.clicked.connect(this, "onSelectAnnPathButtonClicked()");

        playerSelector.rootGroupBox = new QGroupBox();
        playerSelector.rootGroupBox.setTitle(title);

        QGridLayout homePlayerGroupBoxLayout = new QGridLayout();
        playerSelector.rootGroupBox.setLayout(homePlayerGroupBoxLayout);
        homePlayerGroupBoxLayout.addWidget(playerLabel, 0, 0);
        homePlayerGroupBoxLayout.addWidget(playerSelector.playerComboBox, 0, 1, 1, 2);
        homePlayerGroupBoxLayout.addWidget(networkLabel, 1, 0);
        homePlayerGroupBoxLayout.addWidget(playerSelector.annPathEdit, 1, 1);
        homePlayerGroupBoxLayout.addWidget(playerSelector.selectNetworkButton, 1, 2);

        return playerSelector;
    }

    private QGroupBox createSimulationGroupBox() {
        QLabel setCountLabel = new QLabel("Количество сетов:", this);
        setCountSpinBox = new QSpinBox(this);
        setCountSpinBox.setMinimum(2);
        setCountSpinBox.setMaximum(4);
        setCountSpinBox.setSingleStep(2);
        setCountSpinBox.setValue(2);

        QLabel simulationCountLabel = new QLabel("Количество симуляций:", this);
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

        QPushButton showSimulationPushButton = new QPushButton("Показать матч", this);
        showSimulationPushButton.clicked.connect(this, "onShowSimulationPushButtonClicked()");

        QPushButton fastSimulatePushButton = new QPushButton("Быстрая симуляция", this);
        fastSimulatePushButton.clicked.connect(this, "onFastSimulatePushButtonClicked()");

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
        groupBox.setTitle("Симуляция");
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
                "Выберите нейронную сеть",
                null,
                "Файлы ANN (*.ann)"
        );
        if (selectedFileName.result != null) {
            annPathEdit.setText(selectedFileName.result);
        }
    }

    private Match configureMatch() {
        Match match = new Match();
        match.setHomePlayer(((Knight) homePlayerSelector.playerComboBox.itemData(homePlayerSelector.playerComboBox.currentIndex())));
        match.setAwayPlayer(((Knight) awayPlayerSelector.playerComboBox.itemData(awayPlayerSelector.playerComboBox.currentIndex())));
        match.setSets(setCountSpinBox.value());
        match.setPlayoff(false);
        match.setVenue(Stadium.standard());
        return match;
    }

    private Strategy createStrategy(QLineEdit annPathEdit) {
        String annPath = annPathEdit.text();
        if (isNotBlank(annPath)) {
            NeuralNetwork<?> perceptron = MultiLayerPerceptron.createFromFile(annPath);
            return new NeuralNetworkStrategy(perceptron);
        } else {
            return new StandardStrategy();
        }
    }

    private StrategyProvider configureStrategyProvider() {
        return new StrategyProvider(
                createStrategy(homePlayerSelector.annPathEdit),
                createStrategy(awayPlayerSelector.annPathEdit));
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
        resultTextEdit.append("Счёт по матчам: " + formatBasicScore(totalScoreByMatches));
        resultTextEdit.append("Счёт по сетам: " + formatBasicScore(totalScoreBySets));
        resultTextEdit.append("Счёт по геймам: " + formatBasicScore(totalScoreByGames));
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
            simulationStatusLabel.setText("Сыграно матчей: " + (i + 1));
            QApplication.processEvents(QEventLoop.ProcessEventsFlag.ExcludeUserInputEvents);
        }
        displayResults(match, scores);
    }
}

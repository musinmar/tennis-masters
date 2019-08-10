package tm.ui.qt;

import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpacerItem;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QTextCursor;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import tm.lib.domain.core.BasicScore;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.core.Stadium;
import tm.lib.domain.world.GameWorld;
import tm.lib.engine.Strategy;
import tm.lib.engine.StrategyProvider;
import tm.lib.engine.strategies.NeuralNetworkStrategy;
import tm.lib.engine.strategies.StandardStrategy;
import tm.ui.qt.simulation.SimulationHelper;

import java.util.ArrayList;
import java.util.List;

import static com.trolltech.qt.gui.QDialogButtonBox.ButtonRole.RejectRole;
import static com.trolltech.qt.gui.QSizePolicy.Policy.Expanding;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static tm.ui.qt.simulation.WidgetsHelper.fillPlayerComboBox;

public class MatchConfigurationDialog extends QDialog {

    private GameWorld gameWorld;
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

    public MatchConfigurationDialog(GameWorld gameWorld, QWidget parent) {
        super(parent);
        this.gameWorld = gameWorld;
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
        fillPlayerComboBox(playerSelector.playerComboBox, gameWorld);
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
        QLineEdit annPathEdit = signalSender() == homePlayerSelector.selectNetworkButton
                ? homePlayerSelector.annPathEdit
                : awayPlayerSelector.annPathEdit;
        String selectedFileName = QFileDialog.getOpenFileName(this, "Выберите нейронную сеть", null,
                new QFileDialog.Filter("Файлы ANN (*.ann)"));
        if (selectedFileName != null) {
            annPathEdit.setText(selectedFileName);
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

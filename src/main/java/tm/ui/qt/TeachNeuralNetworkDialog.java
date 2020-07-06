package tm.ui.qt;

import com.trolltech.qt.core.QDir;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpacerItem;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import org.neuroph.core.Layer;
import org.neuroph.nnet.MultiLayerPerceptron;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.GameWorld;
import tm.ui.qt.simulation.NeuralNetworkTeacher;

import java.util.List;

import static com.trolltech.qt.gui.QDialogButtonBox.ButtonRole.RejectRole;
import static com.trolltech.qt.gui.QSizePolicy.Policy.Expanding;
import static java.util.stream.Collectors.joining;
import static tm.ui.qt.simulation.WidgetsHelper.fillPlayerComboBox;

public class TeachNeuralNetworkDialog extends QDialog {

    private GameWorld gameWorld;
    private QComboBox playerComboBox;
    private QSpinBox iterationCountSpinBox;
    private QLineEdit outputDirectoryEdit;
    private QLabel teachingStatusLabel;

    public TeachNeuralNetworkDialog(GameWorld gameWorld, QWidget parent) {
        super(parent);
        this.gameWorld = gameWorld;
        setupUi();
    }

    private void setupUi() {
        QLabel playerLabel = new QLabel("Игрок:", this);
        playerComboBox = new QComboBox(this);
        fillPlayerComboBox(playerComboBox, gameWorld);

        QLabel iterationCountLabel = new QLabel("Итераций:", this);
        iterationCountSpinBox = new QSpinBox(this);
        iterationCountSpinBox.setMinimum(1);
        iterationCountSpinBox.setMaximum(Integer.MAX_VALUE);
        iterationCountSpinBox.setSingleStep(1000);
        iterationCountSpinBox.setValue(1000);

        QLabel outputDirectoryLabel = new QLabel("Целевой каталог:", this);
        outputDirectoryEdit = new QLineEdit(this);
        outputDirectoryEdit.setText(System.getProperty("user.dir"));

        QPushButton launchButton = new QPushButton("Запустить", this);
        launchButton.clicked.connect(this, "onLaunchButtonClicked()");

        teachingStatusLabel = new QLabel(this);

        QGridLayout inputBoxLayout = new QGridLayout();
        inputBoxLayout.addWidget(playerLabel, 0, 0);
        inputBoxLayout.addWidget(playerComboBox, 0, 1);
        inputBoxLayout.addWidget(iterationCountLabel, 1, 0);
        inputBoxLayout.addWidget(iterationCountSpinBox, 1, 1);
        inputBoxLayout.addWidget(outputDirectoryLabel, 2, 0);
        inputBoxLayout.addWidget(outputDirectoryEdit, 2, 1);

        QHBoxLayout buttonsLayout = new QHBoxLayout();
        buttonsLayout.addWidget(launchButton);
        buttonsLayout.addWidget(teachingStatusLabel);
        buttonsLayout.addSpacerItem(new QSpacerItem(1, 1, Expanding));

        QVBoxLayout groupBoxLayout = new QVBoxLayout();
        groupBoxLayout.addLayout(inputBoxLayout);
        groupBoxLayout.addLayout(buttonsLayout);
        groupBoxLayout.addSpacerItem(new QSpacerItem(1, 1, Expanding, Expanding));

        QGroupBox mainGroupBox = new QGroupBox(this);
        mainGroupBox.setTitle("Настройки");
        mainGroupBox.setLayout(groupBoxLayout);

        QDialogButtonBox buttonBox = new QDialogButtonBox(this);
        buttonBox.addButton("Закрыть", RejectRole);
        buttonBox.rejected.connect(this, "onCloseButtonClicked()");

        QVBoxLayout mainLayout = new QVBoxLayout(this);
        mainLayout.addWidget(mainGroupBox);
        mainLayout.addWidget(buttonBox);

        setWindowTitle("Обучить нейронную сеть");
        resize(600, 300);
    }

    private void onCloseButtonClicked() {
        close();
    }

    private String createDefaultFileName(String directory, Knight knight, int iterations, MultiLayerPerceptron perceptron) {
        List<Layer> layers = perceptron.getLayers();
        String layerSpec = layers.stream()
                .map(l -> Integer.toString(l.getNeuronsCount()))
                .collect(joining("-"));
        return directory + QDir.separator() + String.format("%s%s_%s_%d.ann", knight.getName(), knight.getSurname(), layerSpec, iterations);
    }

    private void onLaunchButtonClicked() {
        Knight knight = (Knight) playerComboBox.itemData(playerComboBox.currentIndex());
        int iterations = iterationCountSpinBox.value();
        NeuralNetworkTeacher teacher = new NeuralNetworkTeacher(knight, iterations);
        MultiLayerPerceptron perceptron = teacher.teachWithCustomEvolution();
        teachingStatusLabel.setText("Обучение завершено");
        String outputDirectory = outputDirectoryEdit.text();
        String fileName = createDefaultFileName(outputDirectory, knight, iterations, perceptron);
        perceptron.save(fileName);
    }
}
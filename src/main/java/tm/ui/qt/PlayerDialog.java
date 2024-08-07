package tm.ui.qt;

import io.qt.Nullable;
import io.qt.widgets.*;
import tm.lib.domain.core.Knight;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class PlayerDialog extends QDialog {

    private final Knight player;
    private QLineEdit nameEdit;
    private QLineEdit surnameEdit;
    private QLineEdit nationEdit;
    private QLineEdit countryEdit;

    private QProgressBar speedBar;
    private QProgressBar accelerationBar;
    private QProgressBar hitPowerBar;
    private QProgressBar shotRangeBar;
    private QProgressBar accuracyBar;
    private QProgressBar cunningBar;
    private QProgressBar intelligenceBar;
    private QProgressBar riskBar;
    private QProgressBar enduranceBar;
    private QProgressBar dexterityBar;
    private QTreeWidget trophyTreeWidget;

    public PlayerDialog(@Nullable QWidget parent, Knight player) {
        super(parent);
        this.player = player;
        initUi();
        updatePlayerInfo();
    }

    private void initUi() {
        QLabel nameLabel = createLabel("Имя");
        nameEdit = createEdit();
        QLabel surnameLabel = createLabel("Фамилия");
        surnameEdit = createEdit();

        QLabel nationLabel = createLabel("Нация");
        nationEdit = createEdit();
        QLabel countryLabel = createLabel("Страна");
        countryEdit = createEdit();

        QGridLayout mainInfoLayout = new QGridLayout(this);
        mainInfoLayout.addWidget(nameLabel, 0, 0);
        mainInfoLayout.addWidget(nameEdit, 0, 1);
        mainInfoLayout.addWidget(surnameLabel, 0, 2);
        mainInfoLayout.addWidget(surnameEdit, 0, 3);
        mainInfoLayout.addWidget(nationLabel, 1, 0);
        mainInfoLayout.addWidget(nationEdit, 1, 1);
        mainInfoLayout.addWidget(countryLabel, 1, 2);
        mainInfoLayout.addWidget(countryEdit, 1, 3);

        QGroupBox mainInfoGroupBox = new QGroupBox(this);
        mainInfoGroupBox.setTitle("Личные данные");
        mainInfoGroupBox.setLayout(mainInfoLayout);

        QLabel speedLabel = createLabel("Скорость");
        speedBar = createProgressBar();
        QLabel accelerationLabel = createLabel("Ускорение");
        accelerationBar = createProgressBar();
        QLabel hitPowerLabel = createLabel("Сила удара");
        hitPowerBar = createProgressBar();
        QLabel shotRangeLabel = createLabel("Дальность удара");
        shotRangeBar = createProgressBar();
        QLabel accuracyLabel = createLabel("Точность");
        accuracyBar = createProgressBar();
        QLabel cunningLabel = createLabel("Хитрость");
        cunningBar = createProgressBar();
        QLabel intelligenceLabel = createLabel("Интеллект");
        intelligenceBar = createProgressBar();
        QLabel riskLabel = createLabel("Риск");
        riskBar = createProgressBar();
        QLabel enduranceLabel = createLabel("Выносливость");
        enduranceBar = createProgressBar();
        QLabel dexterityLabel = createLabel("Ловкость");
        dexterityBar = createProgressBar();

        QGridLayout skillsLayout = new QGridLayout(this);
        skillsLayout.addWidget(speedLabel, 0, 0);
        skillsLayout.addWidget(speedBar, 0, 1);
        skillsLayout.addWidget(accelerationLabel, 0, 2);
        skillsLayout.addWidget(accelerationBar, 0, 3);
        skillsLayout.addWidget(hitPowerLabel, 1, 0);
        skillsLayout.addWidget(hitPowerBar, 1, 1);
        skillsLayout.addWidget(shotRangeLabel, 1, 2);
        skillsLayout.addWidget(shotRangeBar, 1, 3);
        skillsLayout.addWidget(accuracyLabel, 2, 0);
        skillsLayout.addWidget(accuracyBar, 2, 1);
        skillsLayout.addWidget(cunningLabel, 2, 2);
        skillsLayout.addWidget(cunningBar, 2, 3);
        skillsLayout.addWidget(intelligenceLabel, 3, 0);
        skillsLayout.addWidget(intelligenceBar, 3, 1);
        skillsLayout.addWidget(riskLabel, 3, 2);
        skillsLayout.addWidget(riskBar, 3, 3);
        skillsLayout.addWidget(enduranceLabel, 4, 0);
        skillsLayout.addWidget(enduranceBar, 4, 1);
        skillsLayout.addWidget(dexterityLabel, 4, 2);
        skillsLayout.addWidget(dexterityBar, 4, 3);

        QGroupBox skillsGroupBox = new QGroupBox(this);
        skillsGroupBox.setTitle("Характеристики");
        skillsGroupBox.setLayout(skillsLayout);

        trophyTreeWidget = new QTreeWidget(this);
        trophyTreeWidget.setColumnCount(2);
        trophyTreeWidget.setHeaderLabels(List.of("Турнир", "Количество"));
        trophyTreeWidget.setColumnWidth(0, 150);

        QVBoxLayout trophyLayout = new QVBoxLayout();
        trophyLayout.addWidget(trophyTreeWidget);

        QGroupBox trophyGroupBox = new QGroupBox(this);
        trophyGroupBox.setTitle("Трофеи");
        trophyGroupBox.setLayout(trophyLayout);

        QDialogButtonBox buttonBox = new QDialogButtonBox(this);
        buttonBox.setStandardButtons(QDialogButtonBox.StandardButton.Close.asFlags());
        buttonBox.clicked.connect(this::accept);

        QVBoxLayout mainLayout = new QVBoxLayout(this);
        mainLayout.addWidget(mainInfoGroupBox);
        mainLayout.addWidget(skillsGroupBox);
        mainLayout.addWidget(trophyGroupBox);
        mainLayout.addWidget(buttonBox);
        setLayout(mainLayout);

        setWindowTitle("Игрок");
    }

    private QLabel createLabel(String text) {
        QLabel label = new QLabel(this);
        label.setText(text);
        return label;
    }

    private QLineEdit createEdit() {
        var lineEdit = new QLineEdit(this);
        lineEdit.setReadOnly(true);
        return lineEdit;
    }

    private QProgressBar createProgressBar() {
        QProgressBar progressBar = new QProgressBar(this);
        progressBar.setRange(0, 100);
        String styleSheet = "QProgressBar {"
                + "border: 2px solid grey;"
                + "border-radius: 5px;"
                + "text-align: center;"
                + "background: #FFFFFF;"
                + "}"
                + "QProgressBar::chunk {"
                + "background-color: #3add36;"
                + "width: 20px;"
                + "}";
        progressBar.setStyleSheet(styleSheet);
        return progressBar;
    }

    private void updatePlayerInfo() {
        nameEdit.setText(player.getName());
        surnameEdit.setText(player.getSurname());
        nationEdit.setText(player.getNation().getName());
        countryEdit.setText(player.getCountry().toString());

        speedBar.setValue((int) player.getSkills().getSpeed());
        accelerationBar.setValue((int) player.getSkills().getAcceleration());
        hitPowerBar.setValue((int) player.getSkills().getHitPower());
        shotRangeBar.setValue((int) player.getSkills().getShotRange());
        accuracyBar.setValue((int) player.getSkills().getAccuracy());
        cunningBar.setValue((int) player.getSkills().getCunning());
        intelligenceBar.setValue((int) player.getSkills().getIntelligence());
        riskBar.setValue((int) player.getSkills().getRisk());
        enduranceBar.setValue((int) player.getSkills().getEndurance());
        dexterityBar.setValue((int) player.getSkills().getDexterity());

        var groupedTrophies = player.getTrophies().stream()
                .collect(groupingBy(Knight.Trophy::getCompetitionName, counting()));
        var sortedEntries = groupedTrophies.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .toList();
        sortedEntries.forEach(entry -> {
            QTreeWidgetItem item = new QTreeWidgetItem();
            item.setText(0, entry.getKey());
            item.setText(1, entry.getValue().toString());
            trophyTreeWidget.addTopLevelItem(item);
        });


    }
}

package tm.ui.qt;

import io.qt.core.Qt;
import io.qt.widgets.QFrame;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLayout;
import io.qt.widgets.QProgressBar;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QWidget;
import tm.lib.engine.Player;

public class PlayerInfoWidget extends QFrame {

    private final Player player;
    private QProgressBar energyBar;

    public PlayerInfoWidget(Player player, QWidget parent) {
        super(parent);
        this.player = player;
        setupUi();
    }

    private void setupUi() {
        setFrameShape(Shape.Panel);
        setSizePolicy(QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Minimum);
        setMaximumWidth(300);

        QLayout layout = new QHBoxLayout();
        setLayout(layout);

        QLabel label = new QLabel(this);
        layout.addWidget(label);
        label.setAlignment(Qt.AlignmentFlag.AlignCenter);
        label.setText(player.getKnight().getFullName());

        energyBar = new QProgressBar(this);
        layout.addWidget(energyBar);
        energyBar.setRange(0, 100);
        energyBar.setValue(100);
        energyBar.setMaximumWidth(150);
    }

    public void updateEnergy() {
        energyBar.setValue((int) player.getEnergy());
    }
}

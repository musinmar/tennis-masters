package tm.ui.qt;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QProgressBar;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QWidget;
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

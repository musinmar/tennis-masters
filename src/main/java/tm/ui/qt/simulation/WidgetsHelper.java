package tm.ui.qt.simulation;

import com.trolltech.qt.gui.QComboBox;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.World;

public class WidgetsHelper {

    public static void fillPlayerComboBox(QComboBox comboBox, World world) {
        for (Knight player : world.getPlayers()) {
            comboBox.addItem(player.getFullName(), player);
        }
        comboBox.setCurrentIndex(5);
    }
}

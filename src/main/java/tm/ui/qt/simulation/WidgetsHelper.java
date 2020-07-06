package tm.ui.qt.simulation;

import com.trolltech.qt.gui.QComboBox;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.GameWorld;

public class WidgetsHelper {

    public static void fillPlayerComboBox(QComboBox comboBox, GameWorld gameWorld) {
        for (Knight player : gameWorld.getPlayers()) {
            comboBox.addItem(player.getFullName(), player);
        }
        comboBox.setCurrentIndex(5);
    }
}

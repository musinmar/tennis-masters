package tm.ui.qt;

import com.trolltech.qt.gui.QApplication;
import tm.lib.domain.world.World;

public class TenisMastersMain {

    public static void main(String[] args) {
        QApplication.initialize(args);
        //MainWindow mainWindow = new MainWindow();
        //mainWindow.show();
        GameWorldDialog gameWorldDialog = new GameWorldDialog(World.createNewWorld(), null);
        gameWorldDialog.show();
        QApplication.execStatic();
        QApplication.shutdown();
    }
}

package tm.ui.qt;

import com.trolltech.qt.gui.QApplication;
import tm.lib.domain.world.GameWorld;

public class TenisMastersMain {

    public static void main(String[] args) {
        QApplication.initialize(args);
        //MainWindow mainWindow = new MainWindow();
        //mainWindow.show();
        GameWorldDialog gameWorldDialog = new GameWorldDialog(new GameWorld(), null);
        gameWorldDialog.show();
        QApplication.execStatic();
        QApplication.shutdown();
    }
}

package tm.ui.qt;

import com.trolltech.qt.gui.QApplication;
import tm.lib.domain.world.PersistenceManager;
import tm.lib.domain.world.World;

public class TenisMastersMain {

    public static void main(String[] args) {
        QApplication.initialize(args);
        //MainWindow mainWindow = new MainWindow();
        //mainWindow.show();

        World world;
        if (PersistenceManager.canLoadWorld()) {
            world = PersistenceManager.loadWorld();
        } else {
            world = World.createNewWorld();
        }
        GameWorldDialog gameWorldDialog = new GameWorldDialog(world, null);
        gameWorldDialog.show();
        QApplication.execStatic();
        QApplication.shutdown();
    }
}

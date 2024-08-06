package tm.ui.qt;

import io.qt.widgets.QApplication;
import tm.lib.domain.world.PersistenceManager;
import tm.lib.domain.world.World;

public class TennisMastersMain {

    public static void main(String[] args) {
        QApplication.initialize(args);

        World world;
        if (PersistenceManager.canLoadWorld()) {
            world = PersistenceManager.loadWorld();
        } else {
            world = World.createNewWorld();
        }
        GameWorldDialog gameWorldDialog = new GameWorldDialog(world, null);
        gameWorldDialog.show();
        QApplication.exec();
        QApplication.shutdown();
    }
}
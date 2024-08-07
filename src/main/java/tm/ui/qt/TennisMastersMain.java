package tm.ui.qt;

import io.qt.widgets.QApplication;

public class TennisMastersMain {

    public static void main(String[] args) {
        QApplication.initialize(args);
        GameWorldDialog gameWorldDialog = new GameWorldDialog(null);
        gameWorldDialog.showMaximized();
        QApplication.exec();
        QApplication.shutdown();
    }
}

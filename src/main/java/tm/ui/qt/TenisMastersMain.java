package tm.ui.qt;

import com.trolltech.qt.gui.QApplication;

public class TenisMastersMain {

    public static void main(String[] args) {
        QApplication.initialize(args);
        MainWindow mainWindow = new MainWindow();
        mainWindow.show();
        QApplication.execStatic();
        QApplication.shutdown();
    }
}

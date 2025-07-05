package tm.ui.qt;

import io.qt.widgets.QMessageBox;
import io.qt.widgets.QWidget;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class StandardMessageBoxes {

    public static void showError(Throwable throwable, QWidget parent) {
        QMessageBox messageBox = new QMessageBox(parent);
        messageBox.setWindowTitle("General Error");
        messageBox.setText("Unexpected error has occurred");
        String stacktrace = ExceptionUtils.getStackTrace(throwable);
        messageBox.setDetailedText(throwable.getMessage() + "\n" + stacktrace);
        messageBox.setStandardButtons(QMessageBox.StandardButton.Close);
        messageBox.exec();
    }

    public static class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            try {
                showError(e, null);
                e.printStackTrace();
            } catch (Exception dialogException) {
                System.err.println("Failed to show error dialog for uncaught exception:");
                e.printStackTrace();
                System.err.println("Dialog creation failed with:");
                dialogException.printStackTrace();
            }
        }
    }
}

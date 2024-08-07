package tm.ui.qt;

import io.qt.widgets.QLayout;
import io.qt.widgets.QMainWindow;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

public class MainWindow extends QMainWindow {

    private static final int MIN_BUTTON_HEIGHT = 30;

    public MainWindow() {
        setupUi();
    }

    private void setupUi() {
        setWindowTitle("Tennis Masters");
        QWidget centralWidget = new QWidget();
        setCentralWidget(centralWidget);
        QLayout layout = new QVBoxLayout(centralWidget);
        QPushButton newButton = new QPushButton(this);
        newButton.setText("New");
        newButton.setMinimumHeight(MIN_BUTTON_HEIGHT);
        newButton.clicked.connect(this, "onNewButtonClicked()");
        layout.addWidget(newButton);
        QPushButton exitButton = new QPushButton(this);
        exitButton.setText("Exit");
        exitButton.setMinimumHeight(MIN_BUTTON_HEIGHT);
        exitButton.clicked.connect(this, "onExitButtonClicked()");
        layout.addWidget(exitButton);
    }

    private void onNewButtonClicked() {
        GameWorldDialog gameWorldDialog = new GameWorldDialog(this);
        gameWorldDialog.show();
    }

    private void onExitButtonClicked() {
        close();
    }
}

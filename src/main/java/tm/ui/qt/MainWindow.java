package tm.ui.qt;

import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.world.World;

public class MainWindow extends QMainWindow {

    private static final int MIN_BUTTON_HEIGHT = 30;

    public MainWindow() {
        setupUi();
    }

    private void setupUi() {
        setWindowTitle("Tenis Masters");
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
        GameWorldDialog gameWorldDialog = new GameWorldDialog(World.createNewWorld(), this);
        gameWorldDialog.open();
    }

    private void onExitButtonClicked() {
        close();
    }
}

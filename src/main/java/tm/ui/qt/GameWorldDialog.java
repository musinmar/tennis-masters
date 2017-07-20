package tm.ui.qt;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPlainTextEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.world.GameWorld;

public class GameWorldDialog extends QDialog {
    
    private GameWorld gameWorld;
    
    public GameWorldDialog(GameWorld gameWorld, QWidget parent) {
        super(parent);
        this.gameWorld = gameWorld;
        setupUi();
    }
    
    private void setupUi() {
        setWindowTitle("Game World");
        
        QLayout mainLayout = new QVBoxLayout(this);
        
        QPlainTextEdit gameWorldLogTextEdit = new QPlainTextEdit(this);
        gameWorldLogTextEdit.setFont(new QFont("Courier New"));
        gameWorldLogTextEdit.setMinimumSize(200, 30);
        Competition competitionToPrint = gameWorld.getCurrentSeason().getSeasonCompetition();
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        competitionToPrint.print(printStream);
        gameWorldLogTextEdit.setPlainText(outputStream.toString());
        mainLayout.addWidget(gameWorldLogTextEdit);
        
        QLayout bottomButtonsLayout = new QHBoxLayout();
        mainLayout.addItem(bottomButtonsLayout);
        
        QPushButton nextButton = new QPushButton(this);
        nextButton.setText("Next Match");
        nextButton.clicked.connect(this, "onNextMatchButtonClicked()");
        bottomButtonsLayout.addWidget(nextButton);
        
        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Close");
        closeButton.clicked.connect(this, "onCloseButtonClicked()");
        bottomButtonsLayout.addWidget(closeButton);
        
        resize(1000, 600);
        move(200, 50);
    }
    
    private void onCloseButtonClicked() {
        close();
    }
    
    private void onNextMatchButtonClicked() {
    }
}

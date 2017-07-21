package tm.ui.qt;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QSpacerItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.competition.Match;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.Side;

public class MatchDialog extends QDialog {

    private final Match match;
    private final MatchSimulator matchSimulator;

    public MatchDialog(Match match, QWidget parent) {
        super(parent);
        this.match = match;
        matchSimulator = new MatchSimulator(match);
        setupUi();
    }

    private void setupUi() {
        setWindowTitle("Match");
        setWindowState(Qt.WindowState.WindowMaximized);

        QFont font = new QFont(font());
        font.setPointSize(12);
        setFont(font);

        QLayout mainLayout = new QHBoxLayout(this);

        QGridLayout pitchPanelLayout = new QGridLayout();
        mainLayout.addItem(pitchPanelLayout);

        PlayerInfoWidget homePlayerInfoWidget = new PlayerInfoWidget(matchSimulator.getPitch().getPlayer(Side.HOME), this);
        pitchPanelLayout.addWidget(homePlayerInfoWidget, 0, 0, Qt.AlignmentFlag.AlignHCenter);

        PitchWidget pitchWidget = new PitchWidget(this);
        pitchPanelLayout.addWidget(pitchWidget, 1, 0);

        PlayerInfoWidget awayPlayerInfoWidget = new PlayerInfoWidget(matchSimulator.getPitch().getPlayer(Side.AWAY), this);
        pitchPanelLayout.addWidget(awayPlayerInfoWidget, 2, 0, Qt.AlignmentFlag.AlignHCenter);

        QFrame infoWidget = new QFrame(this);
        infoWidget.setMinimumWidth(150);
        infoWidget.setMaximumWidth(250);
        infoWidget.setFrameShape(QFrame.Shape.Box);
        mainLayout.addWidget(infoWidget);

        QLayout infoPanelLayout = new QVBoxLayout();
        infoWidget.setLayout(infoPanelLayout);

        QLabel playersLabel = new QLabel();
        infoPanelLayout.addWidget(playersLabel);
        playersLabel.setAlignment(Qt.AlignmentFlag.AlignHCenter);
        playersLabel.setText(match.getFirstPlayer().getFullName() + " - " + match.getSecondPlayer().getFullName());

        QLabel resultLabel = new QLabel();
        infoPanelLayout.addWidget(resultLabel);

        QLabel timeLabel = new QLabel();
        infoPanelLayout.addWidget(timeLabel);

        infoPanelLayout.addItem(new QSpacerItem(10, 10, QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Expanding));

        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Close");
        closeButton.clicked.connect(this, "onCloseButtonClicked()");
        infoPanelLayout.addWidget(closeButton);
    }

    private void onCloseButtonClicked() {
        close();
    }
}

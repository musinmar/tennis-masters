package tm.ui.qt;

import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QWidget;

public class PitchWidget extends QFrame {

    public PitchWidget(QWidget parent) {
        super(parent);
        setupUi();
    }

    private void setupUi() {
        setFrameShape(QFrame.Shape.Box);
        setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    }
}

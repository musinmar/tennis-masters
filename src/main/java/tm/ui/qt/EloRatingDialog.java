package tm.ui.qt;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpacerItem;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.EloRating;

import java.util.Arrays;

import static com.trolltech.qt.core.Qt.ItemDataRole.DisplayRole;
import static com.trolltech.qt.gui.QSizePolicy.Policy.Expanding;

public class EloRatingDialog extends QDialog {

    private EloRating eloRating;
    private QTreeWidget eloRatingTreeWidget;

    public EloRatingDialog(QWidget parent, EloRating eloRating) {
        super(parent);
        this.eloRating = eloRating;
        initUi();
        populateEloRatingList();
    }

    private void initUi() {
        eloRatingTreeWidget = new QTreeWidget(this);
        eloRatingTreeWidget.setColumnCount(4);
        eloRatingTreeWidget.setHeaderLabels(Arrays.asList("", "Игрок", "Рейтинг", "Изменение за год"));
        eloRatingTreeWidget.setColumnWidth(0, 25);
        eloRatingTreeWidget.setColumnWidth(1, 120);
        eloRatingTreeWidget.setColumnWidth(2, 100);
        eloRatingTreeWidget.setColumnWidth(3, 100);
        eloRatingTreeWidget.setIndentation(0);
        eloRatingTreeWidget.setItemDelegate(new CustomItemDelegate());

        QPushButton closeButton = new QPushButton(this);
        closeButton.setText("Закрыть");
        closeButton.clicked.connect(this, "onCloseButtonClicked()");

        QHBoxLayout bottomButtonsLayout = new QHBoxLayout();
        bottomButtonsLayout.addSpacerItem(new QSpacerItem(1, 1, Expanding, Expanding));
        bottomButtonsLayout.addWidget(closeButton);

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addWidget(eloRatingTreeWidget, 100);
        mainLayout.addItem(bottomButtonsLayout);
        setLayout(mainLayout);

        setWindowTitle("Рейтинг игроков");
        resize(400, 600);
    }

    private void onCloseButtonClicked() {
        close();
    }

    private void populateEloRatingList() {
        int i = 1;
        for (Knight knight : eloRating.getPersonsByRating()) {
            QTreeWidgetItem item = new QTreeWidgetItem();
            item.setData(0, DisplayRole, i++);
            item.setText(1, knight.getFullName());
            item.setData(2, DisplayRole, eloRating.getRating(knight));
            item.setTextAlignment(2, Qt.AlignmentFlag.AlignRight.value());
            item.setData(3, DisplayRole, eloRating.getRatingChange(knight));
            item.setTextAlignment(3, Qt.AlignmentFlag.AlignRight.value());
            eloRatingTreeWidget.addTopLevelItem(item);
        }
        eloRatingTreeWidget.sortByColumn(2, Qt.SortOrder.DescendingOrder);
        eloRatingTreeWidget.setSortingEnabled(true);
    }

}

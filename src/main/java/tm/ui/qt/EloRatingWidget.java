package tm.ui.qt;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.EloRating;

import java.util.Arrays;

import static com.trolltech.qt.core.Qt.ItemDataRole.DisplayRole;

public class EloRatingWidget extends QWidget {

    private EloRating eloRating;
    private QTreeWidget eloRatingTreeWidget;

    public EloRatingWidget(QWidget parent, EloRating eloRating) {
        super(parent);
        this.eloRating = eloRating;
        initUi();
        repopulateEloRatingList();
    }

    private void initUi() {
        eloRatingTreeWidget = new QTreeWidget(this);
        eloRatingTreeWidget.setColumnCount(5);
        eloRatingTreeWidget.setHeaderLabels(Arrays.asList("", "Игрок", "Рейтинг", "Изменение за год", ""));
        eloRatingTreeWidget.setColumnWidth(0, 25);
        eloRatingTreeWidget.setColumnWidth(1, 120);
        eloRatingTreeWidget.setColumnWidth(2, 100);
        eloRatingTreeWidget.setColumnWidth(3, 150);
        eloRatingTreeWidget.setIndentation(0);
        eloRatingTreeWidget.setItemDelegate(new CustomItemDelegate());

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addWidget(eloRatingTreeWidget, 100);
        setLayout(mainLayout);
    }

    public void repopulateEloRatingList() {
        eloRatingTreeWidget.clear();
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

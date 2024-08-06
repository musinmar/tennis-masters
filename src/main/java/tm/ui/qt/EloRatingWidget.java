package tm.ui.qt;

import io.qt.core.Qt;
import io.qt.core.Qt.AlignmentFlag;
import io.qt.widgets.QTreeWidget;
import io.qt.widgets.QTreeWidgetItem;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;
import tm.lib.domain.core.Knight;
import tm.lib.domain.world.EloRating;

import java.util.Arrays;

import static io.qt.core.Qt.ItemDataRole.DisplayRole;

public class EloRatingWidget extends QWidget {

    private EloRating eloRating;
    private QTreeWidget eloRatingTreeWidget;

    public EloRatingWidget(QWidget parent) {
        super(parent);
        initUi();
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

    public void setEloRating(EloRating eloRating) {
        this.eloRating = eloRating;
        repopulateEloRatingList();
    }

    public void repopulateEloRatingList() {
        eloRatingTreeWidget.clear();
        int i = 1;
        for (Knight knight : eloRating.getPersonsByRating()) {
            QTreeWidgetItem item = new QTreeWidgetItem();
            item.setData(0, DisplayRole, i++);
            item.setText(1, knight.getFullName());
            item.setData(2, DisplayRole, eloRating.getRating(knight));
            item.setTextAlignment(2, AlignmentFlag.AlignRight.asFlags());
            item.setData(3, DisplayRole, eloRating.getRatingChange(knight));
            item.setTextAlignment(3, AlignmentFlag.AlignRight.asFlags());
            eloRatingTreeWidget.addTopLevelItem(item);
        }
        eloRatingTreeWidget.sortByColumn(2, Qt.SortOrder.DescendingOrder);
        eloRatingTreeWidget.setSortingEnabled(true);
    }

}

package tm.ui.qt;

import io.qt.NonNull;
import io.qt.core.QSize;
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
    public static final int RANK_COLUMN_WIDTH = 15;
    public static final int NAME_COLUMN_WIDTH = 130;
    public static final int RATING_COLUMN_WIDTH = 50;
    public static final int DELTA_COLUMN_WIDTH = 50;

    private EloRating eloRating;
    private QTreeWidget eloRatingTreeWidget;

    public EloRatingWidget(QWidget parent) {
        super(parent);
        initUi();
    }

    private void initUi() {
        eloRatingTreeWidget = new QTreeWidget(this);
        eloRatingTreeWidget.setColumnCount(4);
        eloRatingTreeWidget.setHeaderLabels(Arrays.asList("", "Player", "Rating", "Delta"));
        eloRatingTreeWidget.setColumnWidth(0, RANK_COLUMN_WIDTH);
        eloRatingTreeWidget.setColumnWidth(1, NAME_COLUMN_WIDTH);
        eloRatingTreeWidget.setColumnWidth(2, RATING_COLUMN_WIDTH);
        eloRatingTreeWidget.setColumnWidth(3, DELTA_COLUMN_WIDTH);
        eloRatingTreeWidget.setIndentation(0);
        eloRatingTreeWidget.setItemDelegate(new CustomItemDelegate());

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addWidget(eloRatingTreeWidget);
        mainLayout.setContentsMargins(2, 2, 4, 2);
        setLayout(mainLayout);
    }

    public void setEloRating(EloRating eloRating) {
        this.eloRating = eloRating;
        repopulateEloRatingList();
    }

    public void repopulateEloRatingList() {
        eloRatingTreeWidget.clear();
        int i = 1;
        for (Knight knight : eloRating.getKnightsByRating()) {
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

    @Override
    public @NonNull QSize sizeHint() {
        var tableSizeHint = eloRatingTreeWidget.sizeHint();
        return tableSizeHint.add(new QSize(25, 0));
    }
}

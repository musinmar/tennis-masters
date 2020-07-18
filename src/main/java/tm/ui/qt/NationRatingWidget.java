package tm.ui.qt;

import com.trolltech.qt.core.Qt.AlignmentFlag;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import tm.lib.domain.core.Nation;
import tm.lib.domain.world.NationRating;

import static com.trolltech.qt.core.Qt.AlignmentFlag.AlignRight;
import static com.trolltech.qt.core.Qt.AlignmentFlag.AlignVCenter;
import static com.trolltech.qt.core.Qt.ItemDataRole.DisplayRole;
import static com.trolltech.qt.core.Qt.SortOrder.AscendingOrder;
import static java.util.Arrays.asList;

public class NationRatingWidget extends QWidget {

    private final NationRating nationRating;

    private QTreeWidget nationRatingTreeWidget;

    public NationRatingWidget(QWidget parent, NationRating nationRating) {
        super(parent);
        this.nationRating = nationRating;
        initUi();
        repopulateNationRatingWidget();
    }

    private void initUi() {
        nationRatingTreeWidget = new QTreeWidget(this);

        QVBoxLayout nationRatingWidgetLayout = new QVBoxLayout();
        nationRatingWidgetLayout.addWidget(nationRatingTreeWidget);

        setLayout(nationRatingWidgetLayout);
    }

    public void repopulateNationRatingWidget() {
        nationRatingTreeWidget.clear();
        nationRatingTreeWidget.setIndentation(0);
        nationRatingTreeWidget.setColumnCount(8);
        nationRatingTreeWidget.setHeaderLabels(asList("#", "Нация", "Текущий сезон", "0", "-1", "-2", "-3", "-4", "-5", "Сумма"));
        nationRatingTreeWidget.header().setStretchLastSection(false);
        nationRatingTreeWidget.setColumnWidth(0, 20);
        nationRatingTreeWidget.setColumnWidth(1, 100);
        nationRatingTreeWidget.setColumnWidth(2, 100);
        nationRatingTreeWidget.setColumnWidth(3, 50);
        nationRatingTreeWidget.setColumnWidth(4, 50);
        nationRatingTreeWidget.setColumnWidth(5, 50);
        nationRatingTreeWidget.setColumnWidth(6, 50);
        nationRatingTreeWidget.setColumnWidth(7, 50);
        nationRatingTreeWidget.setColumnWidth(8, 50);
        nationRatingTreeWidget.setColumnWidth(9, 50);
        nationRatingTreeWidget.setItemDelegate(new CustomItemDelegate());

        for (Nation nation : Nation.values()) {
            QTreeWidgetItem item = new QTreeWidgetItem();
            item.setData(0, DisplayRole, nationRating.getNationRanking(nation) + 1);
            item.setText(1, nation.getName());
            setDoubleData(item, 2, nationRating.getCurrentSeasonPoints(nation));
            setDoubleData(item, 3, nationRating.getCurrentSeasonValue(nation));
            NationRating.PointHistoryItem pointHistory = nationRating.getPointHistory(nation);
            for (int i = 0; i < pointHistory.seasons.length; i++) {
                setDoubleData(item, 4 + i, pointHistory.seasons[i]);
            }
            setDoubleData(item, 9, nationRating.getTotalPoints(nation));
            nationRatingTreeWidget.addTopLevelItem(item);
        }

        nationRatingTreeWidget.sortByColumn(0, AscendingOrder);
    }

    private void setDoubleData(QTreeWidgetItem item, int column, double value) {
        item.setData(column, DisplayRole, value);
        item.setTextAlignment(column, AlignmentFlag.createQFlags(AlignRight, AlignVCenter).value());
    }
}

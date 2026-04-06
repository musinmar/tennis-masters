package tm.ui.qt;

import io.qt.core.QLocale;
import io.qt.widgets.QStyledItemDelegate;

class CustomItemDelegate extends QStyledItemDelegate {
    @Override
    public String displayText(Object value, QLocale locale) {
        if (value instanceof Double) {
            return String.format("%.2f", value);
        }
        return super.displayText(value, locale);
    }
}

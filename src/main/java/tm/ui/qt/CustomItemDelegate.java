package tm.ui.qt;

import com.trolltech.qt.core.QLocale;
import com.trolltech.qt.gui.QStyledItemDelegate;

class CustomItemDelegate extends QStyledItemDelegate {
    @Override
    public String displayText(Object value, QLocale locale) {
        if (value instanceof Double) {
            return String.format("%.2f", value);
        }
        return super.displayText(value, locale);
    }
}

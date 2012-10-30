package mollusc.linguasubtitle;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author mollusc
 */
public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

    CheckBoxRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        int indexRow = table.convertRowIndexToModel(row);
        boolean isKnown = (Boolean) table.getModel().getValueAt(indexRow, 0);
        if (isKnown) {
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
        } else {
            setBackground(Color.white);
            setForeground(Color.BLACK);
        }
        setSelected((value != null && ((Boolean) value).booleanValue()));
        return this;
    }
}

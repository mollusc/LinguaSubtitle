package mollusc.linguasubtitle;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author mollusc <MolluscLab@gmail.com>
 */
public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
    
    CheckBoxRenderer() {
	setHorizontalAlignment(JLabel.CENTER);
	setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	int indexRow = table.convertRowIndexToModel(row);
	boolean isKnown = (Boolean) table.getModel().getValueAt(indexRow, 2);
	boolean isFamilar = (Boolean) table.getModel().getValueAt(indexRow, 1);
	int meeting = (Integer) table.getModel().getValueAt(indexRow, 6);
	CellRender.paintCell(this, row, meeting, isKnown, isFamilar, false);
	setSelected((value != null && ((Boolean) value).booleanValue()));
	return this;
    }
}

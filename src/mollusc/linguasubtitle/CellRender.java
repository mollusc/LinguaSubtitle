package mollusc.linguasubtitle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import sun.org.mozilla.javascript.internal.ast.Comment;

/**
 * @author mollusc
 */
public class CellRender extends DefaultTableCellRenderer{

    public CellRender() {
	setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	int indexRow = table.convertRowIndexToModel(row);
	boolean isKnown = (Boolean) table.getModel().getValueAt(indexRow, 2);
	boolean isFamilar = (Boolean) table.getModel().getValueAt(indexRow, 1);
	int meeting = (Integer) table.getModel().getValueAt(indexRow, 6);
	
	Component c= super.getTableCellRendererComponent(table, value, isSelected, hasFocus, indexRow, column);	
	paintCell(c, row, meeting, isKnown, isFamilar);
	
	return c;
    }
    

    static public void paintCell(Component component, int row, int meeting, boolean isKnown, boolean  isFamilar) {
	if (isKnown) {
	    if (meeting == 0) {
		component.setFont(component.getFont().deriveFont(Font.BOLD));
	    }
	    component.setBackground(Color.LIGHT_GRAY);
	    component.setForeground(Color.BLACK);
	} else {
	    if (meeting == 0) {
		component.setFont(component.getFont().deriveFont(Font.BOLD));
	    }
	    if (row % 2 == 0) {
		component.setBackground(Color.white);
	    } else {
		component.setBackground(Color.decode("#eeeeee"));
	    }
	    if(isFamilar) component.setForeground(Color.decode("#007000"));
	    else component.setForeground(Color.BLACK);
	}
    }
}
package mollusc.linguasubtitle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRender extends DefaultTableCellRenderer {
	public CellRender () {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		int indexRow = table.convertRowIndexToModel(row);
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, indexRow, column);

		boolean isKnown = (Boolean) table.getModel().getValueAt(indexRow, 0);
		int meeting = (Integer) table.getModel().getValueAt(indexRow, 4);
		if (isKnown) {
			if (meeting == 0)
				c.setFont(c.getFont().deriveFont(Font.BOLD));

			c.setBackground(Color.LIGHT_GRAY);
			c.setForeground(Color.BLACK);
		} else {
			if (meeting == 0)
				c.setFont(c.getFont().deriveFont(Font.BOLD));

			c.setBackground(Color.white);
			c.setForeground(Color.BLACK);
		}
		return c;
	}
}
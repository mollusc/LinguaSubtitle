/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mollusc.linguasubtitle;

import javax.swing.*;
import java.awt.*;

/**
 * @author mollusc <MolluscLab@gmail.com>
 */
class CellEditor extends DefaultCellEditor {

	public CellEditor() {
		super(new JTextField());
		setClickCountToStart(1);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table,
												 Object value, boolean isSelected, int row, int column) {
		JTextField ec = (JTextField) editorComponent;
		if (column == 3) {
			ec.setEditable(false);

		} else {
			ec.setEditable(true);
		}
		ec.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2),
				BorderFactory.createEmptyBorder(0, 0, 1, 0)));
		ec.setText(value.toString());
		return editorComponent;
	}
}

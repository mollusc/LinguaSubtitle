/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mollusc.linguasubtitle.table;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author mollusc <MolluscLab@gmail.com>
 */
public class CellEditor extends DefaultCellEditor {

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

		installContextMenu(ec);
		return editorComponent;
	}

	private void installContextMenu(Container c) {
			if (c instanceof JTextComponent) {
				c.addMouseListener(new MouseAdapter() {
					public void mouseReleased(final MouseEvent e) {
						if (e.isPopupTrigger()) {
							final JTextComponent component = (JTextComponent)e.getComponent();
							final JPopupMenu menu = new JPopupMenu();
							JMenuItem item;
							item = new JMenuItem(new DefaultEditorKit.CopyAction());
							item.setText("Copy");
							item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
							menu.add(item);
							item = new JMenuItem(new DefaultEditorKit.CutAction());
							item.setText("Cut");
							item.setEnabled(component.isEditable() && component.getSelectionStart() != component.getSelectionEnd());
							menu.add(item);
							item = new JMenuItem(new DefaultEditorKit.PasteAction());
							item.setText("Paste");
							item.setEnabled(component.isEditable());
							menu.add(item);
							menu.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				});
			} else if (c instanceof Container)
				installContextMenu(c);
	}
}

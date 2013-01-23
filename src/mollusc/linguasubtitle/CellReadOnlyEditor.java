/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mollusc.linguasubtitle;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.*;
import mollusc.linguasubtitle.subtitle.parser.Stem;

/**
 *
 * @author vofedoseenko
 */
public class CellReadOnlyEditor extends DefaultCellEditor{
    
  public CellReadOnlyEditor() {
    super(new JTextField());
    setClickCountToStart(1);
  }
     
@Override
    public Component getTableCellEditorComponent(JTable table,
	    Object value, boolean isSelected, int row, int column) {

	JTextField ec = (JTextField) editorComponent;
	if (value instanceof Stem) {
	    ec.setEditable(false);
	    
	} else {
	    ec.setEditable(true);
	}
	ec.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	ec.setText(value.toString());
	return editorComponent;
    }
}

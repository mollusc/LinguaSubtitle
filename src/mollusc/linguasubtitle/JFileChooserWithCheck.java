package mollusc.linguasubtitle;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Check before saving if the same file already exist then ask user for confirmation does she really want to override
 * @author mollusc <MolluscLab@gmail.com>
 */
public class JFileChooserWithCheck extends JFileChooser {

    @Override
    public void approveSelection() {
	File file = getSelectedFile();
	if (file.exists() && getDialogType() == SAVE_DIALOG) {
	    int result = JOptionPane.showConfirmDialog(this,
		    "Такой файл уже существует. Заменить?",
		    "Уведомление",
		    JOptionPane.YES_NO_CANCEL_OPTION);
	    switch (result) {
		case JOptionPane.YES_OPTION:
		    super.approveSelection();
		    return;
		case JOptionPane.NO_OPTION:
		    return;
		case JOptionPane.CLOSED_OPTION:
		    return;
		case JOptionPane.CANCEL_OPTION:
		    cancelSelection();
		    return;
	    }
	}
	super.approveSelection();
    }
}

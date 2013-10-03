package mollusc.linguasubtitle.filechooser;

import javax.swing.*;
import java.io.File;

/**
 * @author mollusc <MolluscLab@gmail.com>
 *         Check before saving if the same file already exist
 *         then ask user for confirmation does she really want to override
 */
public class JFileChooserWithCheck extends JFileChooser {
	//<editor-fold desc="Private Fields">
	/**
	 * Is file subtitle?
	 */
	private final boolean isSubtitle;
	//</editor-fold>

	//<editor-fold desc="Constructors">

	/**
	 * Constructor of the class JFileChooserWithCheck
	 *
	 * @param isSubtitle Is file subtitle?
	 */
	public JFileChooserWithCheck(boolean isSubtitle) {
		this.isSubtitle = isSubtitle;
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public void approveSelection() {
		String pathSubtitle = getSelectedFile().getAbsolutePath();
		if (isSubtitle) {
			String ext = ((ExtensionFileFilter) getFileFilter()).extensions[0];
			if (!(pathSubtitle.length() > ext.length() && pathSubtitle.substring(pathSubtitle.length() - ext.length()).toLowerCase().equals(ext)))
				setSelectedFile(new File(pathSubtitle + "." + ext));
		}

		File file = getSelectedFile();
		if (file.exists() && getDialogType() == SAVE_DIALOG) {
			int result = JOptionPane.showConfirmDialog(this,
					"File already exists. Overwrite?",
					"Message",
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
	//</editor-fold>
}

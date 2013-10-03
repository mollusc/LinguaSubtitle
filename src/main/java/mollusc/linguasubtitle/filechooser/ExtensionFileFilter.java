package mollusc.linguasubtitle.filechooser;

import java.io.File;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 20.09.13
 */
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {
	//<editor-fold desc="Private Fields">
	/**
	 * Description of files
	 */
	private final String description;
	/**
	 * Extensions of files
	 */
	public final String[] extensions;
	//</editor-fold>

	//<editor-fold desc="Constructors">

	/**
	 * Constructor of the class ExtensionFileFilter
	 * @param description description of a file
	 * @param extension   extensions of a file
	 */
	public ExtensionFileFilter(String description, String extension) {
		this(description, new String[]{extension});
	}

	/**
	 * Constructor of the class ExtensionFileFilter
	 *
	 * @param description description of files
	 * @param extensions array of extensions
	 */
	public ExtensionFileFilter(String description, String extensions[]) {
		if (description == null) {
			this.description = extensions[0];
		} else {
			this.description = description;
		}
		this.extensions = extensions.clone();
		toLower(this.extensions);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	public String getDescription() {
		return description;
	}

	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			String path = file.getAbsolutePath().toLowerCase();
			for (String extension : extensions) {
				if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * All item of array to lower case
	 *
	 * @param array array of strings
	 */
	private void toLower(String array[]) {
		for (int i = 0, n = array.length; i < n; i++) {
			array[i] = array[i].toLowerCase();
		}
	}
	//</editor-fold>
}

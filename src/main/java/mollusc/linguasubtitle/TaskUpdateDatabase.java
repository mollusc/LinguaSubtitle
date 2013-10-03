package mollusc.linguasubtitle;

import mollusc.linguasubtitle.db.Vocabulary;
import mollusc.linguasubtitle.stemming.Stemmator;

import javax.swing.*;

/**
 * @author mollusc <MolluscLab@gmail.com>
 *         <p/>
 *         Update data from mainTable to the Database
 */
class TaskUpdateDatabase extends SwingWorker<Void, Void> {
	//<editor-fold desc="Private Fields">
	/**
	 * Form that call this class
	 */
	private final MainWindow outer;
	/**
	 * Updated database
	 */
	private final Vocabulary db;
	//</editor-fold>

	//<editor-fold desc="Constructors">

	/**
	 * Constructor of the class ExtensionFileFilter
	 *
	 * @param outer form that call this class
	 */
	public TaskUpdateDatabase(final MainWindow outer) {
		this.outer = outer;
		db = new Vocabulary();
		db.createConnection();
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public Void doInBackground() throws ClassNotFoundException {
		try {
			float progress = 0f;
			setProgress((int) progress);
			for (int i = 0; i < outer.mainTable.getRowCount(); i++) {
				if (!outer.progressMonitor.isCanceled()) {
					boolean isStudy = (Boolean) outer.mainTable.getModel().getValueAt(i, 1);
					boolean isKnown = (Boolean) outer.mainTable.getModel().getValueAt(i, 2);
					String word = outer.mainTable.getModel().getValueAt(i, 3).toString();
					Stemmator stemmator = new Stemmator(word, outer.language);
					String translation = outer.mainTable.getModel().getValueAt(i, 4).toString();
					String language = outer.language;
					db.updateValues(stemmator.getStem(), word, translation, language, isKnown, isStudy);
					progress += 100f / outer.mainTable.getRowCount();
					setProgress((int) progress);
				}
			}
		} catch (Exception ignore) {
		}
		return null;
	}

	@Override
	public void done() {
		db.closeConnection();
		outer.progressMonitor.close();
		outer.exportToSubtitleButton.setEnabled(true);
	}
	//</editor-fold>
}

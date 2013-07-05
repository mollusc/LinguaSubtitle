package mollusc.linguasubtitle;

import mollusc.linguasubtitle.db.Vocabulary;
import mollusc.linguasubtitle.subtitle.parser.Stem;

import javax.swing.*;

/**
 * Update data from mainTable to the Database
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
class TaskUpdateDatabase extends SwingWorker<Void, Void> {
	private final boolean updateMeeting;
	private final MainWindow outer;
	private final Vocabulary db;

	public TaskUpdateDatabase(final MainWindow outer) {
		this.outer = outer;
		this.updateMeeting = true;
		db = new Vocabulary();
		db.createConnection();
	}

	/*
	 * Main task. Executed in background thread.
	 */
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
					Stem stem = new Stem(word, outer.language);
					String translation = outer.mainTable.getModel().getValueAt(i, 4).toString();
					String language = outer.language;
					db.updateValues(stem.getStem(), word, translation, language, isKnown, isStudy, updateMeeting);
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

}

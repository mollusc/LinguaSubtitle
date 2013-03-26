package mollusc.linguasubtitle;

import mollusc.linguasubtitle.db.Vocabulary;
import mollusc.linguasubtitle.subtitle.parser.Stem;

import javax.swing.*;

/**
 * Update data from tableMain to the Database
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
class TaskUpdateDatabase extends SwingWorker<Void, Void> {
    private boolean updateMeeting;
    private final MainWindow outer;
    private Vocabulary db;

    public TaskUpdateDatabase(boolean updateMeeting, final MainWindow outer) {
        this.outer = outer;
        this.updateMeeting = updateMeeting;
        db = new Vocabulary("Vocabulary");
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
			for (int i = 0; i < outer.tableMain.getRowCount(); i++) {
				boolean isStudy = (Boolean) outer.tableMain.getModel().getValueAt(i, 1);
				boolean isKnown = (Boolean) outer.tableMain.getModel().getValueAt(i, 2);
				String word = outer.tableMain.getModel().getValueAt(i, 3).toString();
				Stem stem = new Stem(word, outer.language);
				String translation = outer.tableMain.getModel().getValueAt(i, 4).toString();
				String language = outer.language;
				db.updateValues(stem.getStem(), word, translation, language, isKnown, isStudy, updateMeeting);
				progress += 100f / outer.tableMain.getRowCount();
				setProgress((int) progress);
				System.out.println("\t" + word);
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

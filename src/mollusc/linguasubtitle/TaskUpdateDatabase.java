package mollusc.linguasubtitle;

import javax.swing.SwingWorker;
import mollusc.linguasubtitle.db.Vocabulary;
import mollusc.linguasubtitle.subtitle.parser.Stem;

/**
 * Update data from tableMain to the Database
 * @author mollusc <MolluscLab@gmail.com>
 */
class TaskUpdateDatabase extends SwingWorker<Void, Void> {
    private boolean updateMeeting;
    private final MainFrame outer;

    public TaskUpdateDatabase(boolean updateMeeting, final MainFrame outer) {
	this.outer = outer;
	this.updateMeeting = updateMeeting;
    }

    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() throws ClassNotFoundException {
	float progress = 0f;
	setProgress((int) progress);
	Vocabulary db = new Vocabulary("Vocabulary");
	db.createConnection();
	for (int i = 0; i < outer.tableMain.getRowCount(); i++) {
	    boolean isStudy = (Boolean) outer.tableMain.getModel().getValueAt(i, 1);
	    boolean isKnown = (Boolean) outer.tableMain.getModel().getValueAt(i, 2);
	    String word = outer.tableMain.getModel().getValueAt(i, 3).toString();
	    Stem stem = new Stem(word);
	    String translation = outer.tableMain.getModel().getValueAt(i, 4).toString();
	    db.updateValues(stem.getStem(), word, translation, isKnown, isStudy, updateMeeting);
	    progress += 100f / outer.tableMain.getRowCount();
	    setProgress((int) progress);
	}
	db.closeConnection();
	return null;
    }

    @Override
    public void done() {
	outer.progressMonitor.close();
	outer.exportToSubtitleButton.setEnabled(true);
    }
    
}

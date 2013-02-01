package mollusc.linguasubtitle;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * Class change progress bar in real-time
 * @author mollusc <MolluscLab@gmail.com>
 */
class Worker extends SwingWorker<Object, Object> {

    JProgressBar pBar;

    public Worker(JProgressBar pb) {
        pBar = pb;
    }

    @Override
    protected Object doInBackground() throws Exception {
        pBar.setVisible(true);
        return null;
    }

    @Override
    protected void done() {
        pBar.setVisible(false);
    }
}

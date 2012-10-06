package mollusc.linguasubtitle;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

//import com.sun.java.swing.plaf.windows.WindowsBorders.ProgressBarBorder;

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

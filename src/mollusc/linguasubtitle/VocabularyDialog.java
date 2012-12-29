package mollusc.linguasubtitle;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import mollusc.linguasubtitle.db.Vocabulary;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.parser.Stem;
import mollusc.linguasubtitle.subtitle.srt.SrtSubtitle;
import sun.awt.windows.ThemeReader;

/**
 * Create main window
 *
 * @author mollusc
 */
public class VocabularyDialog implements PropertyChangeListener {

    private JPanel rightPanel;
    private JPanel sittingsPanel;
    private JTable table;
    private JTextPane textSubtitle;
    private JScrollPane scroller;
    private JProgressBar progressBar;
    private JButton openFileButton;
    private JButton generateSubtitle;
    private DefaultTableModel model;
    private JButton knownButton;
    private JButton translatButton;
    private JButton studiedButton;
    private JButton familiarButton;
    private JButton saveTable;
    private JCheckBox hideDialog;
    private JLabel numberOfRepeat;
    private JLabel totalWords;
    private JLabel numberUnknownWords;
    private Color colorKnownWords;
    private Color colorTranslateWords;
    private Color colorFamiliarWords;
    private Color colorStudiedWords;
    private Subtitle subtitle;

    public VocabularyDialog() {
        super();
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.lightGray);
        rightPanel.setPreferredSize(new Dimension(400, 0));
        createTextPanel();
        createTable();
        createStatistic();
        createSittings();
        createProgressBar();
        rightPanel.setVisible(true);
    }

    private void createTextPanel() {
        textSubtitle = new JTextPane();
        textSubtitle.setEditable(false);

        scroller = new JScrollPane(textSubtitle,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.getVerticalScrollBar().setValue(0);
    }

    private void createTable() {
        openFileButton = new JButton("Загрузка субтитров");
        openFileButton
                .addActionListener((ActionListener) new OpenFileListener());
        rightPanel.add(openFileButton);

        Object[] columnNames = {"Изв.", "Слова", "Перевод", "Кол.", "Встреч."};
        Object[][] data = null;

        model = new DefaultTableModel(data, columnNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return Stem.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Integer.class;
                    case 4:
                        return Integer.class;
                    default:
                        return Boolean.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                updateStatistic();
                if (column == 1 || column == 3 || column == 4) {
                    return false;
                } else {
                    return true;
                }
            }
        };

        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(390, 250));
        table.getColumnModel().getColumn(0).setPreferredWidth(36);
        table.getColumnModel().getColumn(1).setPreferredWidth(134);
        table.getColumnModel().getColumn(2).setPreferredWidth(134);
        table.getColumnModel().getColumn(3).setPreferredWidth(43);
        table.getColumnModel().getColumn(4).setPreferredWidth(43);

        CellListener listener = new CellListener();
        table.getColumnModel().getSelectionModel()
                .addListSelectionListener(listener);
        table.getSelectionModel().addListSelectionListener(listener);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new CellRender());
        table.setDefaultRenderer(Integer.class, new CellRender());
        table.setDefaultRenderer(Boolean.class, new CheckBoxRenderer());
        rightPanel.add(new JScrollPane(table));
    }

    private void createStatistic() {
        JPanel statisticPanel = new JPanel();
        statisticPanel.setBackground(Color.lightGray);
        statisticPanel.setPreferredSize(new Dimension(190, 220));
        statisticPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        statisticPanel.setLayout(null);
        Insets insets = statisticPanel.getInsets();
        Graphics graphics = statisticPanel.getGraphics();

        // Header
        JLabel label = new JLabel("<html><b>Статистика</b></html>");
        statisticPanel.add(label);
        Dimension size = label.getPreferredSize();
        label.setBounds(50 + insets.right, 5 + insets.bottom, size.width, size.height);
        
        
        
        JLabel label1 = new JLabel(
                "<html>Количество диалгов<br/>с двумя и более<br>неизвестными словами</html>");
        statisticPanel.add(label1);

        size = label1.getPreferredSize();
        label1.setBounds(5 + insets.right, 25 + insets.bottom, size.width,
                size.height);

        numberOfRepeat = new JLabel(" ");
        statisticPanel.add(numberOfRepeat);
        size = numberOfRepeat.getPreferredSize();
        numberOfRepeat.setBounds(145 + insets.right, 30 + insets.bottom, 30,
                size.height);

        JLabel label2 = new JLabel("<html>Общее<br>количество слов</html>");
        statisticPanel.add(label2);
        size = label2.getPreferredSize();
        label2.setBounds(5 + insets.right, 80 + insets.bottom, size.width,
                size.height);

        totalWords = new JLabel(" ");
        statisticPanel.add(totalWords);
        size = totalWords.getPreferredSize();
        totalWords.setBounds(145 + insets.right, 85 + insets.bottom, 50,
                size.height);

        JLabel label3 = new JLabel(
                "<html>Количесво<br>неизвестных<br>слов</html>");
        statisticPanel.add(label3);
        size = label3.getPreferredSize();
        label3.setBounds(5 + insets.right, 120 + insets.bottom, size.width,
                size.height);

        numberUnknownWords = new JLabel(" ");
        statisticPanel.add(numberUnknownWords);
        size = numberUnknownWords.getPreferredSize();
        numberUnknownWords.setBounds(145 + insets.right, 125 + insets.bottom,
                50, 30);

        rightPanel.add(statisticPanel);
    }

    private void updateStatistic() {
        numberOfRepeat.setText(Integer.toString(subtitle.numberDialogWithStems(
                getUnknownStems(), 2)));
        int total = getTotalQuantityWords();
        String strTotal = Integer.toString(total);
        totalWords.setText("<html>" + strTotal + "</html>");
        int count = getQuantityUnknownWords();
        float percent = (100f * (float) count) / (float) total;
        String strPercent = String.format("%.1f", percent) + "%";
        numberUnknownWords.setText("<html>" + Integer.toString(count) + "<br>("
                + strPercent + ")</html>");
    }

    private void createSittings() {
        sittingsPanel = new JPanel();
        sittingsPanel.setBackground(Color.lightGray);
        sittingsPanel.setPreferredSize(new Dimension(190, 220));
        sittingsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        sittingsPanel.setLayout(null);

        Map<String, String> sittings = getSittings();

        JLabel label = new JLabel("<html><b>Экспорт</b></html>");
        sittingsPanel.add(label);
        Dimension size = label.getPreferredSize();
        Insets insets = sittingsPanel.getInsets();

        label.setBounds(60 + insets.right, 5 + insets.bottom, size.width,
                size.height);

        hideDialog = new JCheckBox("<html>Скрывать известные<br/>диалоги</html>");
        hideDialog.setBackground(Color.lightGray);        
        sittingsPanel.add(hideDialog);

        size = hideDialog.getPreferredSize();
        hideDialog.setBounds(5 + insets.right, 20 + insets.bottom, size.width,
                size.height);

        if (sittings != null && sittings.containsKey("hideKnownDialog")) {
            if (sittings.get("hideKnownDialog").equals("true")) {
                hideDialog.setSelected(true);
            } else {
                hideDialog.setSelected(false);
            }
        }
        else
            hideDialog.setSelected(true);


        if (sittings != null && sittings.containsKey("colorKnownWords")) {
            colorKnownWords = Color.decode("#"
                    + sittings.get("colorKnownWords"));
        } else {
            colorKnownWords = Color.lightGray;
        }

        if (sittings != null && sittings.containsKey("colorTranslateWords")) {
            colorTranslateWords = Color.decode("#"
                    + sittings.get("colorTranslateWords"));
        } else {
            colorTranslateWords = new Color(204, 255, 204);
        }

        if (sittings != null && sittings.containsKey("colorStudiedWords")) {
            colorStudiedWords = Color.decode("#"
                    + sittings.get("colorStudiedWords"));
        } else {
            colorStudiedWords = Color.white;
        }

        if (sittings != null && sittings.containsKey("colorFamiliarWords")) {
            colorFamiliarWords = Color.decode("#"
                    + sittings.get("colorFamiliarWords"));
        } else {
            colorFamiliarWords = new Color(255, 255, 204);
        }

        knownButton = new JButton();
        knownButton.setPreferredSize(new Dimension(10, 10));
        knownButton.setBackground(colorKnownWords);
        knownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color c = JColorChooser.showDialog(rightPanel,
                        "Выберите цвет...", colorKnownWords);
                if (c != null) {
                    colorKnownWords = c;
                    knownButton.setBackground(colorKnownWords);
                }
            }
        });
        sittingsPanel.add(knownButton);

        JLabel knownLabel = new JLabel("Известные слова");
        sittingsPanel.add(knownLabel);

        size = knownButton.getPreferredSize();
        knownButton.setBounds(10 + insets.right, 65 + insets.bottom,
                size.width, size.height);

        size = knownLabel.getPreferredSize();
        knownLabel.setBounds(25 + insets.right, 61 + insets.bottom, size.width,
                size.height);

        translatButton = new JButton();
        translatButton.setPreferredSize(new Dimension(10, 10));
        translatButton.setBackground(colorTranslateWords);
        translatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color c = JColorChooser.showDialog(rightPanel,
                        "Выберите цвет...", colorTranslateWords);
                if (c != null) {
                    colorTranslateWords = c;
                    translatButton.setBackground(colorTranslateWords);
                }
            }
        });
        sittingsPanel.add(translatButton);

        JLabel translateLabel = new JLabel("Перевод");
        sittingsPanel.add(translateLabel);

        size = translatButton.getPreferredSize();
        translatButton.setBounds(10 + insets.right, 85 + insets.bottom,
                size.width, size.height);

        size = translateLabel.getPreferredSize();
        translateLabel.setBounds(25 + insets.right, 81 + insets.bottom,
                size.width, size.height);

        studiedButton = new JButton();
        studiedButton.setPreferredSize(new Dimension(10, 10));
        studiedButton.setBackground(colorStudiedWords);
        studiedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color c = JColorChooser.showDialog(rightPanel,
                        "Выберите цвет...", colorStudiedWords);
                if (c != null) {
                    colorStudiedWords = c;
                    studiedButton.setBackground(colorStudiedWords);
                }
            }
        });
        sittingsPanel.add(studiedButton);

        JLabel targetLabel = new JLabel("Изучаемые слова");
        sittingsPanel.add(targetLabel);

        size = studiedButton.getPreferredSize();
        studiedButton.setBounds(10 + insets.right, 105 + insets.bottom,
                size.width, size.height);

        size = targetLabel.getPreferredSize();
        targetLabel.setBounds(25 + insets.right, 101 + insets.bottom,
                size.width, size.height);

        familiarButton = new JButton();
        familiarButton.setPreferredSize(new Dimension(10, 10));
        familiarButton.setBackground(colorFamiliarWords);
        familiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color c = JColorChooser.showDialog(rightPanel,
                        "Выберите цвет...", colorFamiliarWords);
                if (c != null) {
                    colorFamiliarWords = c;
                    familiarButton.setBackground(colorFamiliarWords);
                }
            }
        });
        sittingsPanel.add(familiarButton);

        JLabel familiarLabel = new JLabel("Знакомые слова");
        sittingsPanel.add(familiarLabel);

        size = familiarButton.getPreferredSize();
        familiarButton.setBounds(10 + insets.right, 125 + insets.bottom,
                size.width, size.height);

        size = familiarLabel.getPreferredSize();
        familiarLabel.setBounds(25 + insets.right, 121 + insets.bottom,
                size.width, size.height);

        generateSubtitle = new JButton("Генерация субтитров");
        generateSubtitle.addActionListener(new SaveFileListener());
        sittingsPanel.add(generateSubtitle);

        size = generateSubtitle.getPreferredSize();
        generateSubtitle.setBounds(20 + insets.right, 145 + insets.bottom,
                size.width, size.height);

        saveTable = new JButton("Сохранить данные");
        saveTable.addActionListener(new UpdateTableListener());
        sittingsPanel.add(saveTable);

        size = saveTable.getPreferredSize();
        saveTable.setBounds(28 + insets.right, 175 + insets.bottom, size.width,
                size.height);

        rightPanel.add(sittingsPanel);
    }

    private void createProgressBar() {
        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(Color.lightGray);
        progressPanel.setPreferredSize(new Dimension(400, 100));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressPanel.add(progressBar);
        progressPanel.setOpaque(true);
        rightPanel.add(progressPanel);
    }

    private static void createAndShowGUI() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JFrame frame = new JFrame("LinguaSubtitle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        VocabularyDialog newContentPane = new VocabularyDialog();
        Container contentPane = frame.getContentPane();

        contentPane.add(newContentPane.scroller);
        contentPane.add(newContentPane.rightPanel, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(800, 600);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private class OpenFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileopen = new JFileChooser();
            fileopen.setFileFilter(new SubtitleFilter());
            int ret = fileopen.showDialog(null, "Открыть");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileopen.getSelectedFile();
                String pathSubtitle = file.getAbsolutePath();
                if (pathSubtitle != null) {
                    subtitle = null;
                    Filename fn = new Filename(pathSubtitle, '/', '.');
                    String extension = fn.extension().toLowerCase();
                    
                    if (extension.toLowerCase().equals("srt")) {
                        subtitle = new SrtSubtitle(pathSubtitle);
                    }

                    if (subtitle != null) {                        
                        if (loadSubtitle()) {
                            try {
                                ((DefaultTableModel)table.getModel()).setRowCount(0);
                                loadTable();
                            } catch (ClassNotFoundException e1) {                                
                                e1.printStackTrace();
                            }
                        }
                    }                    
                }
            }
        }
    }

    private class UpdateTableListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                updateDB(false);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class SaveFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileopen = new JFileChooser() {
                @Override
                public void approveSelection() {
                    File f = getSelectedFile();
                    if (f.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this,
                                "Такой файл уже существует. Заменить?", "Уведомление",
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
            };
            fileopen.setFileFilter(new SubtitleFilter());
            fileopen.setCurrentDirectory(new File(subtitle.getPathToSubtitle()));
            int ret = fileopen.showSaveDialog(rightPanel);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileopen.getSelectedFile();
                String pathGeneratedSubtitle = file.getAbsolutePath();

                try {
                    subtitle.generateSubtitle(pathGeneratedSubtitle,
                            getTranslate(), getColorsStems(),
                            getColorsTranslate(), toHexString(colorKnownWords),
                            hideDialog.isSelected());

                    updateDB(true);
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Get list of unknown stems and translations
     *
     * @return
     */
    private Map<String, String> getTranslate() {
        Map<String, String> stems = new HashMap<String, String>();
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!((Boolean) table.getValueAt(i, 0))) {
                Stem stem = (Stem) table.getValueAt(i, 1);
                String translate = (String) table.getValueAt(i, 2);
                stems.put(stem.getStem(), translate);
            }
        }
        db.closeConnection();
        return stems;
    }

    /**
     * Get list of unknown stems and colors
     *
     * @return
     */
    private Map<String, String> getColorsStems() {
        Map<String, String> stems = new HashMap<String, String>();
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!((Boolean) table.getValueAt(i, 0))) {
                Stem stem = (Stem) table.getValueAt(i, 1);
                String translate = (String) table.getValueAt(i, 2);
                if (translate.isEmpty()) {
                    stems.put(stem.getStem(),
                            toHexString(colorFamiliarWords));
                    continue;
                }
                stems.put(stem.getStem(), toHexString(colorStudiedWords));
            }
        }
        db.closeConnection();
        return stems;
    }

    /**
     * Get list of unknown stems and translate colors
     *
     * @return
     */
    private Map<String, String> getColorsTranslate() {
        Map<String, String> stems = new HashMap<String, String>();
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!((Boolean) table.getValueAt(i, 0))) {
                Stem stem = (Stem) table.getValueAt(i, 1);
                String translate = (String) table.getValueAt(i, 2);
                if (!translate.isEmpty()) {
                    stems.put(stem.getStem(),
                            toHexString(colorTranslateWords));
                }
            }
        }
        db.closeConnection();
        return stems;
    }

    /**
     * Get list of unknown stems
     *
     * @return
     */
    private ArrayList<String> getUnknownStems() {
        ArrayList<String> stems = new ArrayList<String>();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!((Boolean) table.getValueAt(i, 0))) {
                Stem stem = (Stem) table.getValueAt(i, 1);
                String translate = (String) table.getValueAt(i, 2);
                if (!translate.isEmpty()) {
                    stems.add(stem.getStem());
                }
            }
        }
        return stems;
    }

    private int getQuantityUnknownWords() {
        int result = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!((Boolean) table.getValueAt(i, 0))) {
                String translate = (String) table.getValueAt(i, 2);
                int count = (Integer) table.getValueAt(i, 3);
                if (!translate.isEmpty()) {
                    result += count;
                }
            }
        }
        return result;
    }

    private int getTotalQuantityWords() {
        int result = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            int count = (Integer) table.getValueAt(i, 3);
            result += count;
        }
        return result;
    }

    private void updateDB(boolean updateMeeting) throws ClassNotFoundException {
        progressBar.setVisible(true);
        UpdateSittings();
        TaskUpdateDB task = new TaskUpdateDB(updateMeeting);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void UpdateSittings() {
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        db.updateSittings(hideDialog.isSelected(),
                toHexString(colorTranslateWords),
                toHexString(colorStudiedWords),
                toHexString(colorFamiliarWords), toHexString(colorKnownWords));
        db.closeConnection();
    }

    private Map<String, String> getSittings() {
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        Map<String, String> result = db.getSittings();
        db.closeConnection();
        return result;
    }

    public static String toHexString(Color c) {
        StringBuilder sb = new StringBuilder('#');

        if (c.getRed() < 16) {
            sb.append('0');
        }
        sb.append(Integer.toHexString(c.getRed()));

        if (c.getGreen() < 16) {
            sb.append('0');
        }
        sb.append(Integer.toHexString(c.getGreen()));

        if (c.getBlue() < 16) {
            sb.append('0');
        }
        sb.append(Integer.toHexString(c.getBlue()));

        return sb.toString();
    }

    public void loadTable() throws ClassNotFoundException {
        if (subtitle != null) {
            TaskLoadTable task = new TaskLoadTable();
            task.addPropertyChangeListener(this);
            task.execute();
        }

    }

    private void tableToDefaultSort() {
        DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
        ArrayList list = new ArrayList();
        list.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        list.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        list.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        list.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sorter.setSortKeys(list);
        sorter.sort();
    }

    private boolean loadSubtitle() {
        textSubtitle.setContentType("text/html");
        if (subtitle != null) {
            String formatedText = subtitle.hideHeader();
            textSubtitle.setText(formatedText);
            return true;
        }
        return false;
    }

    public void markWord() {
        textSubtitle.setContentType("text/html");
        if (table.getSelectedColumn() == 1) {
            int row = table.convertRowIndexToModel(table.getSelectedRow());
            if (row != -1 && subtitle != null) {
                Stem stem = (Stem) table.getModel().getValueAt(row, 1);
                String formatedText = subtitle.markWord(stem.getStem());
                textSubtitle.setText(formatedText);
                textSubtitle.setCaretPosition(subtitle.getPositionStem(stem
                        .getStem()));
            }
        }
    }

    private class CellListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            markWord();
        }
    }

    class TaskLoadTable extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() throws ClassNotFoundException {
            // Initialize progress property.
            //float progress = 0f;
            //setProgress((int) progress);
            Map<Stem, Integer> stems = subtitle.getListStems();

            // Clear table
            ((DefaultTableModel) table.getModel()).setRowCount(0);
            //progress += 50f;
            //setProgress((int) progress);

            Vocabulary db = new Vocabulary("Vocabulary");
            db.createConnection();
            for (Stem key : stems.keySet()) {
                String translate = db.getTranslate(key.getStem());
                if (translate == null) {
                    translate = "";
                }
                boolean remember = db.getRemember(key.getStem());
                int meeting = db.getMeeting(key.getStem());
                String word = db.getWord(key.getStem());

                if (word != null && word.length() < key.getWord().length()) {
                    if (Character.isUpperCase(word.charAt(0))
                            && Character.isLowerCase(key.getWord().charAt(0))) {
                        word = word.toLowerCase();
                    }
                    key.setWord(word);
                }

                if (word != null
                        && Character.isUpperCase(key.getWord().charAt(0))
                        && Character.isLowerCase(word.charAt(0))) {
                    key.setWord(key.getWord().toLowerCase());
                }

                model.addRow(new Object[]{remember, key, translate,
                            stems.get(key), meeting});
               // progress += 50f / stems.keySet().size();
               // setProgress((int) progress);
            }
            db.closeConnection();
            tableToDefaultSort();
            updateStatistic();
            return null;
        }

        /*@Override
        public void done() {
            setProgress(0);
            progressBar.setVisible(false);
        }*/
    }

    class TaskUpdateDB extends SwingWorker<Void, Void> {

        private boolean updateMeeting;

        public TaskUpdateDB(boolean updateMeeting) {
            this.updateMeeting = updateMeeting;
        }

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() throws ClassNotFoundException {
            // Initialize progress property.
            generateSubtitle.setEnabled(false);
            saveTable.setEnabled(false);
            float progress = 0f;
            setProgress((int) progress);
            Map<Stem, Integer> stems = subtitle.getListStems();

            Vocabulary db = new Vocabulary("Vocabulary");
            db.createConnection();
            for (int i = 0; i < table.getRowCount(); i++) {
                Stem stem = (Stem) table.getModel().getValueAt(i, 1);
                String translation = (String) table.getModel().getValueAt(i, 2);
                boolean isKnown = (Boolean) table.getModel().getValueAt(i, 0);
                String word = stem.getWord();
                db.updateValues(stem.getStem(), word, translation, isKnown,
                        updateMeeting);

                progress += 100f / table.getRowCount();
                setProgress((int) progress);
            }
            db.closeConnection();
            return null;
        }

        @Override
        public void done() {
            setProgress(0);
            generateSubtitle.setEnabled(true);
            saveTable.setEnabled(true);
            progressBar.setVisible(false);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }

    }
}

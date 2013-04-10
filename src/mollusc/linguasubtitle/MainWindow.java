package mollusc.linguasubtitle;

import mollusc.linguasubtitle.db.ItemVocabulary;
import mollusc.linguasubtitle.db.Vocabulary;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.parser.Stem;
import mollusc.linguasubtitle.subtitle.srt.SrtSubtitle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 */
public class MainWindow implements PropertyChangeListener {
    private JPanel panel1;
    public JTable tableMain;
    private JTable tableStatistic;
    private JButton loadSubtitle;
    private JCheckBox hideDialog;
    public JButton exportToSubtitleButton;
    private ColorSelectionButton colorButtonTranslateWords;
    private ColorSelectionButton colorButtonUnknownWords;
    private ColorSelectionButton colorButtonKnownWords;
    private ColorSelectionButton colorButtonStudiedWords;
    private ColorSelectionButton colorButtonNameWords;
    private ColorSelectionButton colorButtonHardWords;
    private JEditorPane textSubtitle;
    private JTabbedPane tabbedPane1;
    private JComboBox languageList;
    private JLabel helpLink;
	private JLabel siteLink;
	private JCheckBox unknownWordsCheckBox;
	private JCheckBox studyWordsCheckBox;
	private JCheckBox knownWordsCheckBox;
	private JCheckBox noBlankTranslationCheckBox;
	private JTextField mentionedMoreThan;
	private JButton exportFromDatabaseButton;
	private JComboBox languageExport;
	public String language;


    private Subtitle subtitle;
    private ArrayList<String> hardWords;
    Map<String, String> settings;
    private Map<String, String> languages;
    ProgressMonitor progressMonitor;
    private TaskUpdateDatabase task;
    private JFrame frameParent;

    public MainWindow(JFrame frameParent) {
        this.frameParent = frameParent;
        this.frameParent.setTitle("LinguaSubtitle 2");
		settings = getSettings();

        InitializeExportToSubtitle();
        InitializeTableMain();
        InitializeTableStatistic();
        InitializeLanguageList();
		InitializeExportFromDatabase();
        InitializeLinks();

        // initialize loadSubtitle
        loadSubtitle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loadSubtitleActionPerformed();
            }
        });

	}

	private void InitializeExportFromDatabase()
	{
		// Default value
		boolean isExportUnknownWords = true;
		boolean isExportStudyWords = false;
		boolean isExportKnownWords = false;
		boolean isNoBlankTranslation = false;
		String exportMoreThan = "10";
		String exportLanguage = "English";

		// Read value
		if (settings != null)
		{
			if(settings.containsKey("isExportUnknownWords"))
				isExportUnknownWords = settings.get("isExportUnknownWords").equals("1")?true:false;
			if(settings.containsKey("isExportStudyWords"))
				isExportStudyWords = settings.get("isExportStudyWords").equals("1")?true:false;
			if(settings.containsKey("isExportKnownWords"))
				isExportKnownWords = settings.get("isExportKnownWords").equals("1")?true:false;
			if(settings.containsKey("isNoBlankTranslation"))
				isNoBlankTranslation = settings.get("isNoBlankTranslation").equals("1")?true:false;
			if(settings.containsKey("exportMoreThan") && tryParseInt(settings.get("exportMoreThan")))
				exportMoreThan = settings.get("exportMoreThan");
			if(settings.containsKey("exportLanguage") && languages.containsKey(settings.get("exportLanguage")))
				exportLanguage = settings.get("exportLanguage");
		}

		//Set value
		unknownWordsCheckBox.setSelected(isExportUnknownWords);
		studyWordsCheckBox.setSelected(isExportStudyWords);
		knownWordsCheckBox.setSelected(isExportKnownWords);
		noBlankTranslationCheckBox.setSelected(isNoBlankTranslation);
		mentionedMoreThan.setText(exportMoreThan);
		languageExport.setSelectedItem(exportLanguage);
		exportFromDatabaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dumpDatabase();
			}
		});
	}

	private void InitializeLinks(){
		helpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://sourceforge.net/p/linguasubtitle/wiki/Home/"));
				} catch (Exception ex) {}
			}
		});

		siteLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		siteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://sourceforge.net/projects/linguasubtitle/"));
				} catch (Exception ex) {}
			}
		});
    }

    private void InitializeTableStatistic() {
        // initialize tableStatistic
        tableStatistic.setModel(new StatisticTableModel());
        tableStatistic.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableStatistic.getColumnModel().getColumn(1).setMaxWidth(150);
        tableStatistic.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableStatistic.getColumnModel().getColumn(2).setMaxWidth(150);
    }

    private void InitializeTableMain() {
        tableMain.setModel(new MainTableModel());
        tableMain.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableMain.getColumnModel().getColumn(0).setMaxWidth(40);
        tableMain.getColumnModel().getColumn(1).setPreferredWidth(40);
        tableMain.getColumnModel().getColumn(1).setMaxWidth(40);
        tableMain.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableMain.getColumnModel().getColumn(2).setMaxWidth(60);
        tableMain.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableMain.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableMain.getColumnModel().getColumn(5).setPreferredWidth(50);
        tableMain.getColumnModel().getColumn(5).setMaxWidth(80);
        tableMain.getColumnModel().getColumn(6).setPreferredWidth(50);
        tableMain.getColumnModel().getColumn(6).setMaxWidth(80);
        tableMain.setRowHeight(20);
        tableMain.setDefaultEditor(Object.class, new CellEditor());
        tableMain.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tableMainMouseClicked(evt);
            }
        });
    }

    private void InitializeLanguageList()
    {
        language = null;
        languages = new HashMap<String, String>();
        //languages.put("Dansk", "danish");
        //languages.put("Nederlands", "dutch");
        //languages.put("Svenska", "swedish");
        //languages.put("Suomi", "finnish");
        //languages.put("Magyar nyelv", "hungarian");
        //languages.put("Norsk", "norwegian");
        //languages.put("Limba română", "romanian");
        languages.put("English", "english");
        languages.put("Français", "french");
        languages.put("Deutsch", "german");
        languages.put("Italiano", "italian");
        languages.put("Português", "portuguese");
        languages.put("Русский", "russian");
        languages.put("Español", "spanish");
        languages.put("Türkçe", "turkish");

        for(String language : languages.keySet())
            languageList.addItem(language);

		for(String language : languages.keySet())
			languageExport.addItem(language);

        if(settings != null && settings.containsKey("language") && languages.containsValue(settings.get("language"))){
            for (String key : languages.keySet()){
                String value = languages.get(key);
                if (value.equals(settings.get("language")))
                    languageList.setSelectedItem(key);
            }
        }
        else
            languageList.setSelectedItem("English");
    }

    private void InitializeExportToSubtitle() {
        if (settings != null && settings.containsKey("colorKnownWords"))
            colorButtonKnownWords.setColor(Color.decode("#" + settings.get("colorKnownWords")));
        else
            colorButtonKnownWords.setColor(Color.decode("#999999"));

        if (settings != null && settings.containsKey("colorUnknownWords"))
            colorButtonUnknownWords.setColor(Color.decode("#" + settings.get("colorUnknownWords")));
        else
            colorButtonUnknownWords.setColor(Color.decode("#ffffff"));

        if (settings != null && settings.containsKey("colorTranslateWords"))
            colorButtonTranslateWords.setColor(Color.decode("#" + settings.get("colorTranslateWords")));
        else
            colorButtonTranslateWords.setColor(Color.decode("#ccffcc"));

        if (settings != null && settings.containsKey("colorHardWord"))
            colorButtonHardWords.setColor(Color.decode("#" + settings.get("colorHardWord")));
        else
            colorButtonHardWords.setColor(Color.decode("#ffcccc"));

        if (settings != null && settings.containsKey("colorNameWords"))
            colorButtonNameWords.setColor(Color.decode("#" + settings.get("colorNameWords")));
        else
            colorButtonNameWords.setColor(Color.decode("#ccccff"));

        if (settings != null && settings.containsKey("colorStudiedWords"))
            colorButtonStudiedWords.setColor(Color.decode("#" + settings.get("colorStudiedWords")));
        else
            colorButtonStudiedWords.setColor(Color.decode("#ffff33"));

        if (settings != null && settings.containsKey("hideKnownDialog") && settings.get("hideKnownDialog").equals("false"))
            hideDialog.setSelected(false);

        exportToSubtitleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exportToSubtitleButtonActionPerformed();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow(frame).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Invoked when the mouse button has been clicked on tableMain
     */
    private void tableMainMouseClicked(MouseEvent evt) {
        Point point = evt.getPoint();
        int columnIndex = tableMain.columnAtPoint(point);
        int rowIndex = tableMain.rowAtPoint(point);
        if (SwingUtilities.isRightMouseButton(evt) && columnIndex == 3)
            highlightWord(rowIndex);
        if (SwingUtilities.isLeftMouseButton(evt) && (columnIndex == 0 || columnIndex == 1 || columnIndex == 2))
            updateStatistic();
    }

    /**
     * Handle clicks on loadSubtitle button.
     */
    private void loadSubtitleActionPerformed() {
        language = languages.get(languageList.getSelectedItem());
        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setFileFilter(new SubtitleFilter());
        int returnValue = fileOpen.showDialog(null, "Open");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            frameParent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = fileOpen.getSelectedFile();
            String path = file.getAbsolutePath();
            if (path != null) {
                subtitle = null;
                Filename fileName = new Filename(path, '/', '.');
                String extension = fileName.extension().toLowerCase();
                if (extension.equals("srt"))
                    subtitle = new SrtSubtitle(path, language);
                if (subtitle != null && loadTextPane()) {
                    loadTable();
                    frameParent.setTitle("LinguaSubtitle 2 - " + path);
                }
            }
            frameParent.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Handle clicks on exportToSubtitle button.
     */
    private void exportToSubtitleButtonActionPerformed() {
        JFileChooser fileOpen = new JFileChooserWithCheck();
        fileOpen.setFileFilter(new SubtitleFilter());
        fileOpen.setCurrentDirectory(new File(subtitle.getPathToSubtitle()));
        int returnValue = fileOpen.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileOpen.getSelectedFile();
            String pathGeneratedSubtitle = file.getAbsolutePath();
            if (!(pathGeneratedSubtitle.length()>4 && pathGeneratedSubtitle.substring(pathGeneratedSubtitle.length()-4).toLowerCase().equals(".srt")))
                pathGeneratedSubtitle += ".srt";
            subtitle.generateSubtitle(pathGeneratedSubtitle,
                    getStemTranslatePairs(),
                    getStemColorPairs(),
                    getColorsTranslate(),
                    toHexString(colorButtonKnownWords.getColor()),
                    hideDialog.isSelected());
            updateDatabase(true);
        }
    }

    /**
     * Updating records in the Database
     *
     * @param updateMeeting Is it necessary to increment Meeting?
     */
    private void updateDatabase(boolean updateMeeting) {
        exportToSubtitleButton.setEnabled(false);
        progressMonitor = new ProgressMonitor(frameParent,
                "Updating records in the Database...",
                "", 0, 100);
        progressMonitor.setProgress(0);

        updateSettings();
        task = new TaskUpdateDatabase(updateMeeting, this);
        task.addPropertyChangeListener(this);
        task.execute();
    }

	private void dumpDatabase() {
		Vocabulary db = new Vocabulary("Vocabulary");
		db.createConnection();
		int meeting;
		try{
			meeting = Integer.parseInt(mentionedMoreThan.getText());
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this.frameParent, "Error input data");
			db.closeConnection();
			return;
		}


		JFileChooser fileOpen = new JFileChooserWithCheck();
		int returnValue = fileOpen.showSaveDialog(null);
		String pathGeneratedSubtitle = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileOpen.getSelectedFile();
			pathGeneratedSubtitle = file.getAbsolutePath();

		}
		ArrayList<ItemVocabulary> result = db.getDump(
				unknownWordsCheckBox.isSelected(),
				knownWordsCheckBox.isSelected(),
				studyWordsCheckBox.isSelected(),
				noBlankTranslationCheckBox.isSelected(),
				meeting,
				languages.get(languageExport.getSelectedItem().toString()));
		db.closeConnection();

		saveDump(pathGeneratedSubtitle, result);
		updateSettings();
	}

	protected static void saveDump(String path, ArrayList<ItemVocabulary> items) {
		if(path != null && items != null){
			try {
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, false), "UTF8");
				//writer.append("test");
				for (ItemVocabulary item: items)
					writer.write(item.word + "\t" + item.translate + "\n");
				writer.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
    /**
     * Save all settings in the Database
     */
    private void updateSettings() {
        Vocabulary db = new Vocabulary("Vocabulary");
		String isExportUnknownWords = unknownWordsCheckBox.isSelected()?"1":"0";
		String isExportStudyWords = studyWordsCheckBox.isSelected()?"1":"0";
		String isExportKnownWords = knownWordsCheckBox.isSelected()?"1":"0";
		String isNoBlankTranslation = noBlankTranslationCheckBox.isSelected()?"1":"0";

        db.createConnection();
        db.updateSettings(
				hideDialog.isSelected(),
                toHexString(colorButtonTranslateWords.getColor()),
                toHexString(colorButtonUnknownWords.getColor()),
                toHexString(colorButtonKnownWords.getColor()),
                toHexString(colorButtonStudiedWords.getColor()),
                toHexString(colorButtonNameWords.getColor()),
                toHexString(colorButtonHardWords.getColor()),
                language,
				isExportUnknownWords,
				isExportStudyWords,
				isExportKnownWords,
				isNoBlankTranslation,
				mentionedMoreThan.getText(),
				languageExport.getSelectedItem().toString());
        db.closeConnection();
    }

    /**
     * This method gets called when a bound property is changed
     */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message =
					String.format("Completed %d%%.\n", progress);
			progressMonitor.setNote(message);
			if (progressMonitor.isCanceled() || task.isDone()) {
				if (progressMonitor.isCanceled()) {
					task.cancel(true);
				}
				exportToSubtitleButton.setEnabled(true);
			}
		}
	}

    /**
     * Get pairs of an unknown stem and a translation
     */
    private Map<String, String> getStemTranslatePairs() {
        Map<String, String> stems = new HashMap<String, String>();
        for (int i = 0; i < tableMain.getRowCount(); i++) {
            boolean isName = (Boolean) tableMain.getModel().getValueAt(i, 0);
            boolean isStudy = (Boolean) tableMain.getModel().getValueAt(i, 1);
            boolean isKnown = (Boolean) tableMain.getModel().getValueAt(i, 2);
            if (!isName && !isStudy && !isKnown) {
                String word = tableMain.getModel().getValueAt(i, 3).toString();
                Stem stem = new Stem(word, language);
                String translate = (String) tableMain.getModel().getValueAt(i, 4);
                stems.put(stem.getStem(), translate);
            }
        }
        return stems;
    }

    /**
     * Get pairs of an stem and a color
     */
    private Map<String, String> getStemColorPairs() {
        Map<String, String> stems = new HashMap<String, String>();
        for (int i = 0; i < tableMain.getRowCount(); i++) {
            boolean isName = (Boolean) tableMain.getModel().getValueAt(i, 0);
            boolean isStudy = (Boolean) tableMain.getModel().getValueAt(i, 1);
            boolean isKnown = (Boolean) tableMain.getModel().getValueAt(i, 2);
            String word = tableMain.getModel().getValueAt(i, 3).toString();
            Stem stem = new Stem(word, language);

            if (isKnown)
                continue;

            if (isStudy) {
                stems.put(stem.getStem(), toHexString(colorButtonStudiedWords.getColor()));
                continue;
            }

            if (isName) {
                stems.put(stem.getStem(), toHexString(colorButtonNameWords.getColor()));
                continue;
            }

            if (hardWords != null && hardWords.contains(stem.getStem()))
                stems.put(stem.getStem(), toHexString(colorButtonHardWords.getColor()));
            else
                stems.put(stem.getStem(), toHexString(colorButtonUnknownWords.getColor()));
        }
        return stems;
    }

    /**
     * Get pairs of an unknown stems and a translate color
     */
    private Map<String, String> getColorsTranslate() {
        Map<String, String> stems = new HashMap<String, String>();
        for (int i = 0; i < tableMain.getRowCount(); i++) {
            boolean isName = (Boolean) tableMain.getModel().getValueAt(i, 0);
            boolean isStudy = (Boolean) tableMain.getModel().getValueAt(i, 1);
            boolean isKnown = (Boolean) tableMain.getModel().getValueAt(i, 2);
            if (!isName && !isStudy && !isKnown) {
                String word = tableMain.getModel().getValueAt(i, 3).toString();
                Stem stem = new Stem(word, language);
                stems.put(stem.getStem(), toHexString(colorButtonTranslateWords.getColor()));
            }
        }
        return stems;
    }

    /**
     * Convert a color to a hex string
     */
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

    /**
     * Get settings from the database
     *
     * @return pairs a parameter name and value
     */
    private Map<String, String> getSettings() {
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        Map<String, String> result = db.getSettings();
        db.closeConnection();
        return result;
    }

    /**
     * Highlight a selected word
     *
     * @param rowNumber selected row
     */
    public void highlightWord(int rowNumber) {
        int rowIndex = tableMain.convertRowIndexToModel(rowNumber);
        if (rowIndex != -1 && subtitle != null) {
            String word = tableMain.getModel().getValueAt(rowIndex, 3).toString();
            Stem stem = new Stem(word, language);
            textSubtitle.setText("");
            Document document = textSubtitle.getDocument();
            subtitle.markWord(stem.getStem(), document);
            textSubtitle.setCaretPosition(subtitle.getPositionStem(stem.getStem()));
        }
    }

    /**
     * Fill tableMain
     */
    public void loadTable() {
        Map<Stem, Integer> stems = subtitle.getListStems();
        DefaultTableModel tableModel = ((DefaultTableModel) tableMain.getModel());
        tableModel.setRowCount(0);
        Vocabulary db = new Vocabulary("Vocabulary");
        db.createConnection();
        hardWords = db.getHardWords();
        tableMain.setDefaultRenderer(Object.class, new CellRender(hardWords, language));
        tableMain.setDefaultRenderer(Integer.class, new CellRender(hardWords, language));
        tableMain.setDefaultRenderer(Boolean.class, new CheckBoxRenderer());
        for (Stem key : stems.keySet()) {
            ItemVocabulary itemDatabase = db.getItem(key.getStem(), language);
            boolean known = false;
            boolean study = false;
            int meeting = 0;
            String translate = "";
            String word = key.getWord();
            if (itemDatabase != null) {
                known = itemDatabase.known;
                study = itemDatabase.study;
                meeting = itemDatabase.meeting;
                translate = itemDatabase.translate;

                if (itemDatabase.word.length() < word.length())
                    word = itemDatabase.word;

                if (Character.isLowerCase(itemDatabase.word.charAt(0)) || Character.isLowerCase(key.getWord().charAt(0)))
                    word = word.toLowerCase();
            }
            tableModel.addRow(new Object[]{false, study, known, word, translate, stems.get(key), meeting});
        }
        db.closeConnection();
        tableDefaultSort();
        updateStatistic();
    }

    /**
     * Sort tableMain in the original condition
     */
    private void tableDefaultSort() {
        DefaultRowSorter sorter = ((DefaultRowSorter) tableMain.getRowSorter());
        ArrayList list = new ArrayList();
        list.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        list.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
        list.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
        list.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sorter.setSortKeys(list);
        sorter.sort();
    }

    /**
     * Update statistic
     */
    private void updateStatistic() {
        int totalWords = 0;
        int totalUnique = tableMain.getRowCount();

        int unknownWords = 0;
        int unknownUnique = 0;

        int knownWords = 0;
        int knownUnique = 0;

        int studyWords = 0;
        int studyUnique = 0;

        for (int i = 0; i < totalUnique; i++) {
            boolean isName = (Boolean) tableMain.getModel().getValueAt(i, 0);
            boolean isStudy = (Boolean) tableMain.getModel().getValueAt(i, 1);
            boolean isKnown = (Boolean) tableMain.getModel().getValueAt(i, 2);
            int count = (Integer) tableMain.getModel().getValueAt(i, 5);
            totalWords += count;

            if (isKnown) {
                knownUnique++;
                knownWords += count;
                continue;
            }

            if (isStudy) {
                studyUnique++;
                studyWords += count;
                continue;
            }

            if (!isName && !isStudy && !isKnown) {
                unknownUnique++;
                unknownWords += count;
                continue;
            }
        }

        tableStatistic.setValueAt(totalUnique, 0, 1);
        tableStatistic.setValueAt(totalWords, 0, 2);

        tableStatistic.setValueAt(String.valueOf(unknownUnique) + " (" + String.format("%.1f", 100f * (float) unknownUnique / (float) totalUnique) + "%)", 1, 1);
        tableStatistic.setValueAt(String.valueOf(unknownWords) + " (" + String.format("%.1f", 100f * (float) unknownWords / (float) totalWords) + "%)", 1, 2);

        tableStatistic.setValueAt(String.valueOf(knownUnique) + " (" + String.format("%.1f", 100f * (float) knownUnique / (float) totalUnique) + "%)", 2, 1);
        tableStatistic.setValueAt(String.valueOf(knownWords) + " (" + String.format("%.1f", 100f * (float) knownWords / (float) totalWords) + "%)", 2, 2);

        tableStatistic.setValueAt(String.valueOf(studyUnique) + " (" + String.format("%.1f", 100f * (float) studyUnique / (float) totalUnique) + "%)", 3, 1);
        tableStatistic.setValueAt(String.valueOf(studyWords) + " (" + String.format("%.1f", 100f * (float) studyWords / (float) totalWords) + "%)", 3, 2);
    }

    /**
     * Insert subtitle's text to textSubtitle
     *
     * @return true if it is success, otherwise false
     */
    private boolean loadTextPane() {
        if (subtitle != null) {
            textSubtitle.setText("");
            subtitle.hideHeader(textSubtitle.getDocument());

            return true;
        }
        return false;
    }

	boolean tryParseInt(String value)
	{
		try
		{
			Integer.parseInt(value);
			return true;
		} catch(NumberFormatException nfe)
		{
			return false;
		}
	}
}

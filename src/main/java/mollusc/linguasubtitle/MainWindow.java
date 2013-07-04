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
@SuppressWarnings("ALL")
public class MainWindow implements PropertyChangeListener {
    private JPanel panel1;
    public JTable tableMain;
    private JTable tableStatistic;
    public JButton exportToSubtitleButton;
    private JEditorPane textSubtitle;
    private JComboBox<String> languageList;
    private JLabel helpLink;
	private JLabel siteLink;
	private JButton exportFromDatabaseButton;
	private JButton preferenceToSubtitleButton;
	private JButton preferenceFromDatabaseButton;
	private JButton openSubtitle;
	public String language;
    private Subtitle subtitle;
    private ArrayList<String> hardWords;
    Map<String, String> settings;
    public Map<String, String> languages;
    ProgressMonitor progressMonitor;
    private TaskUpdateDatabase task;
    private JFrame frameParent;

    public MainWindow(JFrame frameParent) {
        this.frameParent = frameParent;
        this.frameParent.setTitle("LinguaSubtitle 2");
		language = null;
		languages = new HashMap<String, String>();
		languages.put("English", "english");
		languages.put("Français", "french");
		languages.put("Deutsch", "german");
		languages.put("Italiano", "italian");
		languages.put("Português", "portuguese");
		languages.put("Русский", "russian");
		languages.put("Español", "spanish");
		languages.put("Türkçe", "turkish");

		settings = getSettings();

        initializeExportToSubtitle();
        initializeTableMain();
        initializeTableStatistic();
        initializeLanguageList();
		initializeExportFromDatabase();
        initializeLinks();

		openSubtitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openSubtitleActionPerformed();
			}
		});
	}

	private void initializeExportFromDatabase()
	{
		exportFromDatabaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dumpDatabase();
			}
		});
		preferenceFromDatabaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openPreference(1);
			}
		});
	}

	private void initializeExportToSubtitle() {
		exportToSubtitleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {exportToSubtitleButtonActionPerformed();}
		});
		preferenceToSubtitleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openPreference(0);
			}
		});
	}

	private void initializeLinks(){
		helpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://sourceforge.net/p/linguasubtitle/wiki/Home/"));
				} catch (Exception ignored) {
				}
			}
		});

		siteLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		siteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://sourceforge.net/projects/linguasubtitle/"));
				} catch (Exception ignored) {}
			}
		});
    }

    private void initializeTableStatistic() {
        tableStatistic.setModel(new StatisticTableModel());
        tableStatistic.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableStatistic.getColumnModel().getColumn(1).setMaxWidth(150);
        tableStatistic.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableStatistic.getColumnModel().getColumn(2).setMaxWidth(150);
    }

    private void initializeTableMain() {
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

    private void initializeLanguageList() {
        for(String language : languages.keySet())
            languageList.addItem(language);

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

	private void openPreference(int index) {
		Preferences preferencesDialog = new Preferences(this.settings,this.languages);
		preferencesDialog.activateTab(index);
		preferencesDialog.setVisible(true);
		preferencesDialog.pack();
	}

	public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow(frame).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
     * Handle clicks on openSubtitle button.
     */
    private void openSubtitleActionPerformed() {
        language = languages.get(languageList.getSelectedItem());
        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setFileFilter(new SubtitleFilter());
		if(subtitle != null && new File(subtitle.getPathToSubtitle()).exists())
			fileOpen.setCurrentDirectory(new File(subtitle.getPathToSubtitle()));
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
			int millisecondsPerCharacter = 100;
			if(settings.containsKey("millisecondsPerCharacter") && tryParseInt(settings.get("millisecondsPerCharacter")))
				millisecondsPerCharacter = Integer.parseInt(settings.get("millisecondsPerCharacter"));
            subtitle.generateSubtitle(pathGeneratedSubtitle,
                    getStemTranslatePairs(),
                    getStemColorPairs(),
                    getColorsTranslate(),
                   	settings.get("colorKnownWords"),
					settings.get("hideKnownDialog").equals("1"),
					settings.get("automaticDurations").equals("1"),
					millisecondsPerCharacter);
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
		if(settings.containsKey("exportMoreThan") && tryParseInt(settings.get("exportMoreThan")))
			meeting = Integer.parseInt(settings.get("exportMoreThan"));
		else
			return;


		JFileChooser fileOpen = new JFileChooserWithCheck();
		int returnValue = fileOpen.showSaveDialog(null);
		String pathGeneratedSubtitle = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileOpen.getSelectedFile();
			pathGeneratedSubtitle = file.getAbsolutePath();

		}
		ArrayList<ItemVocabulary> result;
		result = db.getDump(
				settings.get("isExportUnknownWords").equals("1"),
				settings.get("isExportKnownWords").equals("1"),
				settings.get("isExportStudyWords").equals("1"),
				settings.get("isNoBlankTranslation").equals("1"),
				meeting,
				languages.get(settings.get("exportLanguage")));
		db.closeConnection();

		saveDump(pathGeneratedSubtitle, result);
		updateSettings();
	}

	protected static void saveDump(String path, ArrayList<ItemVocabulary> items) {
		if(path != null && items != null){
			try {
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, false), "UTF8");
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
		settings.put("language", language);
        db.createConnection();
        db.updateSettings(settings);
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

            if (isKnown) continue;

            if (isStudy) {
                stems.put(stem.getStem(), settings.get("colorStudiedWords"));
                continue;
            }

            if (isName) {
                stems.put(stem.getStem(), settings.get("colorNameWords"));
                continue;
            }

            if (hardWords != null && hardWords.contains(stem.getStem()))
                stems.put(stem.getStem(), settings.get("colorHardWord"));
            else
                stems.put(stem.getStem(), settings.get("colorUnknownWords"));
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
                stems.put(stem.getStem(), settings.get("colorTranslateWords"));
            }
        }
        return stems;
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
		// Set default value for settings if it isn't set
		Preferences defaultSettings = new Preferences(result,this.languages);
		defaultSettings.UpdateSettings();

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
        ArrayList<RowSorter.SortKey> list = new ArrayList<RowSorter.SortKey>();
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

		int newWords = 0;
		int newUnique = 0;

        for (int i = 0; i < totalUnique; i++) {
            boolean isName = (Boolean) tableMain.getModel().getValueAt(i, 0);
            boolean isStudy = (Boolean) tableMain.getModel().getValueAt(i, 1);
            boolean isKnown = (Boolean) tableMain.getModel().getValueAt(i, 2);
            int count = (Integer) tableMain.getModel().getValueAt(i, 5);
			int mentioned = (Integer) tableMain.getModel().getValueAt(i, 6);
            totalWords += count;

			if(mentioned == 0)
			{
				newUnique++;
				newWords += count;
			}

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

            if (!isName) {
                unknownUnique++;
                unknownWords += count;
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

		tableStatistic.setValueAt(String.valueOf(newUnique) + " (" + String.format("%.1f", 100f * (float) newUnique / (float) totalUnique) + "%)", 4, 1);
		tableStatistic.setValueAt(String.valueOf(newWords) + " (" + String.format("%.1f", 100f * (float) newWords / (float) totalWords) + "%)", 4, 2);
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

	static boolean tryParseInt(String value)
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

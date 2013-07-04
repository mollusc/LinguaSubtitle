package mollusc.linguasubtitle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class Preferences extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	public JTabbedPane tabbedPane1;
	public ColorSelectionButton colorButtonTranslateWords;
	public ColorSelectionButton colorButtonUnknownWords;
	public ColorSelectionButton colorButtonKnownWords;
	public ColorSelectionButton colorButtonStudiedWords;
	public ColorSelectionButton colorButtonNameWords;
	public ColorSelectionButton colorButtonHardWords;
	public JCheckBox hideDialog;
	public JCheckBox automaticDurationsCheckBox;
	public JTextField millisecondsPerCharacter;
	public JLabel millisecondsPerCharacterLabel;
	public JCheckBox unknownWordsCheckBox;
	public JCheckBox studyWordsCheckBox;
	public JCheckBox knownWordsCheckBox;
	public JCheckBox noBlankTranslationCheckBox;
	public JTextField mentionedMoreThan;
	public JComboBox languageExport;

	Map<String, String> settings;
	public Map<String, String> languages;

	public Preferences(Map<String, String> settings, Map<String, String> languages) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		this.settings = settings;
		this.languages = languages;

		initializeLanguageList();
		initializeExportToSubtitle();
		initializeExportFromDatabase();
		changeEnableMillisecondsPerCharacter();


		this.setTitle("Preferences");
		this.setSize(600, 400);
		this.setResizable(false);

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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

	private void initializeLanguageList() {
		for(String language : languages.keySet())
			languageExport.addItem(language);
	}

	private void onOK() {

		if(!MainWindow.tryParseInt(mentionedMoreThan.getText()) || !MainWindow.tryParseInt(millisecondsPerCharacter.getText()) )
		{
			JOptionPane.showMessageDialog(this,
					"Wrong value of parameter.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		settings.put("hideKnownDialog", hideDialog.isSelected()?"1":"0");
		settings.put("colorTranslateWords", toHexString(colorButtonTranslateWords.getColor()));
		settings.put("colorUnknownWords", toHexString(colorButtonUnknownWords.getColor()));
		settings.put("colorKnownWords", toHexString(colorButtonKnownWords.getColor()));
		settings.put("colorStudiedWords", toHexString(colorButtonStudiedWords.getColor()));
		settings.put("colorNameWords", toHexString(colorButtonNameWords.getColor()));
		settings.put("colorHardWord", toHexString(colorButtonHardWords.getColor()));
		settings.put("automaticDurations", automaticDurationsCheckBox.isSelected()?"1":"0");
		settings.put("millisecondsPerCharacter", millisecondsPerCharacter.getText());
		settings.put("isExportUnknownWords", unknownWordsCheckBox.isSelected()?"1":"0");
		settings.put("isExportStudyWords", studyWordsCheckBox.isSelected()?"1":"0");
		settings.put("isExportKnownWords", knownWordsCheckBox.isSelected()?"1":"0");
		settings.put("isNoBlankTranslation", noBlankTranslationCheckBox.isSelected()?"1":"0");
		settings.put("exportMoreThan", mentionedMoreThan.getText());
		settings.put("exportLanguage", languageExport.getSelectedItem().toString());

		dispose();
	}

	private void onCancel() {
// add your code here if necessary
		dispose();
	}

	public void initializeExportFromDatabase() {
		// Default values
		boolean isExportUnknownWords = true;
		boolean isExportStudyWords = false;
		boolean isExportKnownWords = false;
		boolean isNoBlankTranslation = false;
		String exportMoreThan = "10";
		String exportLanguage = "English";

		// Read values
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
			if(settings.containsKey("exportMoreThan") && MainWindow.tryParseInt(settings.get("exportMoreThan")))
				exportMoreThan = settings.get("exportMoreThan");
			if(settings.containsKey("exportLanguage") && languages.containsKey(settings.get("exportLanguage")))
				exportLanguage = settings.get("exportLanguage");
		}

		//Set values
		unknownWordsCheckBox.setSelected(isExportUnknownWords);
		studyWordsCheckBox.setSelected(isExportStudyWords);
		knownWordsCheckBox.setSelected(isExportKnownWords);
		noBlankTranslationCheckBox.setSelected(isNoBlankTranslation);
		mentionedMoreThan.setText(exportMoreThan);
		languageExport.setSelectedItem(exportLanguage);
	}

	public void changeEnableMillisecondsPerCharacter() {
		if(automaticDurationsCheckBox.isSelected()){
			millisecondsPerCharacter.setEnabled(true);
			millisecondsPerCharacterLabel.setEnabled(true);
		}
		else{
			millisecondsPerCharacter.setEnabled(false);
			millisecondsPerCharacterLabel.setEnabled(false);
		}
	}

	public void initializeExportToSubtitle() {

		//Default values
		Color colorKnownWords = Color.decode("#999999");
		Color colorUnknownWords = Color.decode("#ffffff");
		Color colorTranslateWords = Color.decode("#ccffcc");
		Color colorHardWord = Color.decode("#ffcccc");
		Color colorNameWords = Color.decode("#ccccff");
		Color colorStudiedWords = Color.decode("#ffff33");
		String msecondsPerCharacter = "100";
		boolean isAutomaticDurations = true;
		boolean hideKnownDialog = true;

		//Read values
		if(settings != null)
		{
			if (settings.containsKey("colorKnownWords"))
				colorKnownWords = Color.decode("#" + settings.get("colorKnownWords"));
			if (settings.containsKey("colorUnknownWords"))
				colorUnknownWords = Color.decode("#" + settings.get("colorUnknownWords"));
			if (settings.containsKey("colorTranslateWords"))
				colorTranslateWords = Color.decode("#" + settings.get("colorTranslateWords"));
			if (settings.containsKey("colorHardWord"))
				colorHardWord = Color.decode("#" + settings.get("colorHardWord"));
			if (settings.containsKey("colorNameWords"))
				colorNameWords= Color.decode("#" + settings.get("colorNameWords"));
			if (settings.containsKey("colorStudiedWords"))
				colorStudiedWords = Color.decode("#" + settings.get("colorStudiedWords"));
			if (settings.containsKey("millisecondsPerCharacter") && MainWindow.tryParseInt(settings.get("millisecondsPerCharacter")))
				msecondsPerCharacter = settings.get("millisecondsPerCharacter");
			if (settings.containsKey("automaticDurations"))
				isAutomaticDurations = settings.get("automaticDurations").equals("1")?true:false;
			if (settings.containsKey("hideKnownDialog"))
				hideKnownDialog = settings.get("hideKnownDialog").equals("1")?true:false;
		}

		// Set values
		colorButtonKnownWords.setColor(colorKnownWords);
		colorButtonUnknownWords.setColor(colorUnknownWords);
		colorButtonTranslateWords.setColor(colorTranslateWords);
		colorButtonHardWords.setColor(colorHardWord);
		colorButtonNameWords.setColor(colorNameWords);
		colorButtonStudiedWords.setColor(colorStudiedWords);
		millisecondsPerCharacter.setText(msecondsPerCharacter);
		automaticDurationsCheckBox.setSelected(isAutomaticDurations);
		hideDialog.setSelected(hideKnownDialog);
		automaticDurationsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeEnableMillisecondsPerCharacter();
			}
		});
	}

	public void activateTab(int index){
		tabbedPane1.setSelectedIndex(index);
	}
}

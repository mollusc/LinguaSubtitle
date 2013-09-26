package mollusc.linguasubtitle;

import mollusc.linguasubtitle.subtitle.utility.CommonUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

/**
 * Dialog Preferences
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class Preferences extends JDialog {
	private JPanel contentPanel;
	private JButton OKButton;
	private JButton cancelButton;
	public JTabbedPane contentTabbedPane;
	public ColorSelectionButton translateWordsColorButton;
	public ColorSelectionButton unknownWordsColorButton;
	public ColorSelectionButton knownWordsColorButton;
	public ColorSelectionButton studiedWordsColorButton;
	public ColorSelectionButton nameWordsColorButton;
	public ColorSelectionButton hardWordsColorButton;
	public JCheckBox hideDialogCheckBox;
	public JCheckBox automaticDurationsCheckBox;
	public JTextField msPerCharacterTextField;
	public JLabel msPerCharacterLabel;
	public JCheckBox unknownWordsCheckBox;
	public JCheckBox studyWordsCheckBox;
	public JCheckBox knownWordsCheckBox;
	public JCheckBox noBlankTranslationCheckBox;
	public JTextField exportMoreThanTextField;
	public JComboBox<String> languagesComboBox;

	private final Map<String, String> settings;
	private final Map<String, String> languages;

	public Preferences(Map<String, String> settings, Map<String, String> languages) {
		setContentPane(contentPanel);
		setModal(true);
		getRootPane().setDefaultButton(OKButton);
		this.settings = settings;
		this.languages = languages;

		initializeLanguagesComboBox();
		initializeExportToSubtitle();
		initializeExportFromDatabase();
		changeEnableMillisecondsPerCharacter();


		this.setTitle("Preferences");
		this.setSize(600, 400);
		this.setResizable(false);

		OKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
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
		contentPanel.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	/**
	 * Handle clicks on OK button.
	 */
	private void onOK() {

		if (!CommonUtility.tryParseInt(exportMoreThanTextField.getText()) || !CommonUtility.tryParseInt(msPerCharacterTextField.getText())) {
			JOptionPane.showMessageDialog(this,
					"Wrong value of parameter.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		updateSettings();

		dispose();
	}

	/**
	 * Handle clicks on Cancel button.
	 */
	private void onCancel() {
		dispose();
	}

	private void initializeLanguagesComboBox() {
		for (String language : languages.keySet())
			languagesComboBox.addItem(language);
	}

	private void initializeExportFromDatabase() {
		// Default values
		boolean exportUnknownWords = true;
		boolean exportStudyWords = false;
		boolean exportKnownWords = false;
		boolean noBlankTranslation = false;
		String exportMoreThan = "10";
		String exportLanguage = "English";

		// Read values
		if (settings != null) {
			if (settings.containsKey("exportUnknownWords"))
				exportUnknownWords = settings.get("exportUnknownWords").equals("1");
			if (settings.containsKey("exportStudyWords"))
				exportStudyWords = settings.get("exportStudyWords").equals("1");
			if (settings.containsKey("exportKnownWords"))
				exportKnownWords = settings.get("exportKnownWords").equals("1");
			if (settings.containsKey("noBlankTranslation"))
				noBlankTranslation = settings.get("noBlankTranslation").equals("1");
			if (settings.containsKey("exportMoreThan") && CommonUtility.tryParseInt(settings.get("exportMoreThan")))
				exportMoreThan = settings.get("exportMoreThan");
			if (settings.containsKey("exportLanguage") && languages.containsKey(settings.get("exportLanguage")))
				exportLanguage = settings.get("exportLanguage");
		}

		//Set values
		unknownWordsCheckBox.setSelected(exportUnknownWords);
		studyWordsCheckBox.setSelected(exportStudyWords);
		knownWordsCheckBox.setSelected(exportKnownWords);
		noBlankTranslationCheckBox.setSelected(noBlankTranslation);
		exportMoreThanTextField.setText(exportMoreThan);
		languagesComboBox.setSelectedItem(exportLanguage);
	}

	private void initializeExportToSubtitle() {

		//Default values
		Color colorKnownWords = Color.decode("#999999");
		Color colorUnknownWords = Color.decode("#ffffff");
		Color colorTranslateWords = Color.decode("#ccffcc");
		Color colorHardWord = Color.decode("#ffcccc");
		Color colorNameWords = Color.decode("#ccccff");
		Color colorStudiedWords = Color.decode("#ffff33");
		String msPerCharacter = "100";
		boolean automaticDurations = true;
		boolean hideKnownDialog = true;

		//Read values
		if (settings != null) {
			if (settings.containsKey("colorKnownWords"))
				colorKnownWords = Color.decode("#" + settings.get("colorKnownWords"));
			if (settings.containsKey("colorUnknownWords"))
				colorUnknownWords = Color.decode("#" + settings.get("colorUnknownWords"));
			if (settings.containsKey("colorTranslateWords"))
				colorTranslateWords = Color.decode("#" + settings.get("colorTranslateWords"));
			if (settings.containsKey("colorHardWord"))
				colorHardWord = Color.decode("#" + settings.get("colorHardWord"));
			if (settings.containsKey("colorNameWords"))
				colorNameWords = Color.decode("#" + settings.get("colorNameWords"));
			if (settings.containsKey("colorStudiedWords"))
				colorStudiedWords = Color.decode("#" + settings.get("colorStudiedWords"));
			if (settings.containsKey("millisecondsPerCharacter") && CommonUtility.tryParseInt(settings.get("millisecondsPerCharacter")))
				msPerCharacter = settings.get("millisecondsPerCharacter");
			if (settings.containsKey("automaticDurations"))
				automaticDurations = settings.get("automaticDurations").equals("1");
			if (settings.containsKey("hideKnownDialog"))
				hideKnownDialog = settings.get("hideKnownDialog").equals("1");
		}

		// Set values
		knownWordsColorButton.setColor(colorKnownWords);
		unknownWordsColorButton.setColor(colorUnknownWords);
		translateWordsColorButton.setColor(colorTranslateWords);
		hardWordsColorButton.setColor(colorHardWord);
		nameWordsColorButton.setColor(colorNameWords);
		studiedWordsColorButton.setColor(colorStudiedWords);
		msPerCharacterTextField.setText(msPerCharacter);
		automaticDurationsCheckBox.setSelected(automaticDurations);
		hideDialogCheckBox.setSelected(hideKnownDialog);
		automaticDurationsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeEnableMillisecondsPerCharacter();
			}
		});
	}

	public void updateSettings() {
		settings.put("hideKnownDialog", hideDialogCheckBox.isSelected() ? "1" : "0");
		settings.put("colorTranslateWords", toHexString(translateWordsColorButton.getColor()));
		settings.put("colorUnknownWords", toHexString(unknownWordsColorButton.getColor()));
		settings.put("colorKnownWords", toHexString(knownWordsColorButton.getColor()));
		settings.put("colorStudiedWords", toHexString(studiedWordsColorButton.getColor()));
		settings.put("colorNameWords", toHexString(nameWordsColorButton.getColor()));
		settings.put("colorHardWord", toHexString(hardWordsColorButton.getColor()));
		settings.put("automaticDurations", automaticDurationsCheckBox.isSelected() ? "1" : "0");
		settings.put("millisecondsPerCharacter", msPerCharacterTextField.getText());
		settings.put("exportUnknownWords", unknownWordsCheckBox.isSelected() ? "1" : "0");
		settings.put("exportStudyWords", studyWordsCheckBox.isSelected() ? "1" : "0");
		settings.put("exportKnownWords", knownWordsCheckBox.isSelected() ? "1" : "0");
		settings.put("noBlankTranslation", noBlankTranslationCheckBox.isSelected() ? "1" : "0");
		settings.put("exportMoreThan", exportMoreThanTextField.getText());
		settings.put("exportLanguage", languages.get(languagesComboBox.getSelectedItem().toString()));
	}

	private void changeEnableMillisecondsPerCharacter() {
		if (automaticDurationsCheckBox.isSelected()) {
			msPerCharacterTextField.setEnabled(true);
			msPerCharacterLabel.setEnabled(true);
		} else {
			msPerCharacterTextField.setEnabled(false);
			msPerCharacterLabel.setEnabled(false);
		}
	}

	public void activateTab(int index) {
		contentTabbedPane.setSelectedIndex(index);
	}

	/**
	 * Convert a color to a hex string
	 */
	public static String toHexString(Color c) {
		StringBuilder sb = new StringBuilder();

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
}

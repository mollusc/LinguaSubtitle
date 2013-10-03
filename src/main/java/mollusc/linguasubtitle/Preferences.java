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
	public JTextField millisecondsPerCharacterTextField;
	public JLabel msPerCharacterLabel;
	public JCheckBox exportUnknownWordsCheckBox;
	public JCheckBox exportStudyWordsCheckBox;
	public JCheckBox exportKnownWordsCheckBox;
	public JCheckBox noBlankTranslationCheckBox;
	public JTextField exportMoreThanTextField;
	public JComboBox<String> exportLanguageComboBox;
	private JComboBox FontNamesComboBox;
	private JTextField mainFontSizeTextField;
	private JSlider transparenceSlider;
	private JTextField translateFontSizeTextField;

	private final Settings settings;
	private final Map<String, String> languages;

	public Preferences(Settings settings, Map<String, String> languages) {
		setContentPane(contentPanel);
		setModal(true);
		getRootPane().setDefaultButton(OKButton);
		this.settings = settings;
		this.languages = languages;

		initializeLanguagesComboBox();
		initializeExportToSubtitle();
		initializeExportFromDatabase();
		changeEnableMillisecondsPerCharacter();
		initializeFontLis();
		initializeASSSettings();


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

	private void initializeFontLis() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = e.getAllFonts();
		for (Font f : fonts) {
			FontNamesComboBox.addItem(f.getFontName());
		}
	}

	/**
	 * Handle clicks on OK button.
	 */
	private void onOK() {

		if (!CommonUtility.tryParseInt(exportMoreThanTextField.getText()) || !CommonUtility.tryParseInt(millisecondsPerCharacterTextField.getText())) {
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

	private void initializeASSSettings() {
		FontNamesComboBox.setSelectedItem(settings.getFontName());
		mainFontSizeTextField.setText(settings.getMainFontSize().toString());
		translateFontSizeTextField.setText(settings.getTranslateFontSize().toString());
		long value = Long.parseLong(settings.getTransparencyKnownWords(), 16);
		transparenceSlider.setValue((int)(100*value/255));
	}

	private void initializeLanguagesComboBox() {
		for (String language : languages.keySet())
			exportLanguageComboBox.addItem(language);
	}

	private void initializeExportFromDatabase() {
		exportUnknownWordsCheckBox.setSelected(settings.getExportUnknownWords());
		exportStudyWordsCheckBox.setSelected(settings.getExportStudyWords());
		exportKnownWordsCheckBox.setSelected(settings.getExportKnownWords());
		noBlankTranslationCheckBox.setSelected(settings.getNoBlankTranslation());
		exportMoreThanTextField.setText(settings.getExportMoreThan().toString());
		if(languages.containsKey(settings.getExportLanguage()))
			exportLanguageComboBox.setSelectedItem(settings.getExportLanguage());
	}

	private void initializeExportToSubtitle() {
		knownWordsColorButton.setColor(Color.decode("#" + settings.getColorKnownWords()));
		unknownWordsColorButton.setColor(Color.decode("#" + settings.getColorUnknownWords()));
		translateWordsColorButton.setColor(Color.decode("#" + settings.getColorTranslateWords()));
		hardWordsColorButton.setColor(Color.decode("#" + settings.getColorHardWord()));
		nameWordsColorButton.setColor(Color.decode("#" + settings.getColorNameWords()));
		studiedWordsColorButton.setColor(Color.decode("#" + settings.getColorStudiedWords()));
		millisecondsPerCharacterTextField.setText(settings.getMillisecondsPerCharacter().toString());
		automaticDurationsCheckBox.setSelected(settings.getAutomaticDurations());
		hideDialogCheckBox.setSelected(settings.getHideKnownDialog());
		automaticDurationsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeEnableMillisecondsPerCharacter();
			}
		});
	}

	public void updateSettings() {
		settings.setColorTranslateWords(toHexString(translateWordsColorButton.getColor()));
		settings.setColorUnknownWords(toHexString(unknownWordsColorButton.getColor()));
		settings.setColorStudiedWords(toHexString(studiedWordsColorButton.getColor()));
		settings.setColorKnownWords(toHexString(knownWordsColorButton.getColor()));
		settings.setColorNameWords(toHexString(nameWordsColorButton.getColor()));
		settings.setColorHardWord(toHexString(hardWordsColorButton.getColor()));
		settings.setTransparencyKnownWords( Integer.toHexString(transparenceSlider.getValue() * 255 /100));
		settings.setNoBlankTranslation(noBlankTranslationCheckBox.isSelected());
		settings.setAutomaticDurations(automaticDurationsCheckBox.isSelected());
		settings.setExportUnknownWords(exportUnknownWordsCheckBox.isSelected());
		settings.setExportStudyWords(exportStudyWordsCheckBox.isSelected());
		settings.setExportKnownWords(exportKnownWordsCheckBox.isSelected());
		settings.setHideKnownDialog(hideDialogCheckBox.isSelected());
		settings.setMillisecondsPerCharacter(millisecondsPerCharacterTextField.getText());
		settings.setMainFontSize(mainFontSizeTextField.getText());
		settings.setTranslateFontSize(translateFontSizeTextField.getText());
		settings.setFontName(FontNamesComboBox.getSelectedItem().toString());
		settings.setExportMoreThan(exportMoreThanTextField.getText());
		settings.setExportLanguage(languages.get(exportLanguageComboBox.getSelectedItem().toString()));
	}

	private void changeEnableMillisecondsPerCharacter() {
		if (automaticDurationsCheckBox.isSelected()) {
			millisecondsPerCharacterTextField.setEnabled(true);
			msPerCharacterLabel.setEnabled(true);
		} else {
			millisecondsPerCharacterTextField.setEnabled(false);
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

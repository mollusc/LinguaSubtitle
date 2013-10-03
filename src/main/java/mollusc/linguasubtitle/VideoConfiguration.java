package mollusc.linguasubtitle;

import javax.swing.*;
import java.awt.event.*;

public class VideoConfiguration extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox resolutionsComboBox;
	private JTextField playResXTextField;
	private JTextField playResYTextField;
	private Settings settings;
	private boolean isOk;
	private JPanel mainPanel;
	private JPanel buttonPanel;

	public VideoConfiguration(Settings settings) {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		this.setTitle("Set video configuration");
		this.setSize(350, 120);
		this.setResizable(false);
		this.settings = settings;
		playResXTextField.setText(settings.getPlayResX().toString());
		playResYTextField.setText(settings.getPlayResY().toString());

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
		resolutionsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeVideoResolution();
			}
		});
	}

	private void changeVideoResolution() {
		switch (resolutionsComboBox.getSelectedIndex())
		{
			case 0:
				playResXTextField.setText("640");
				playResYTextField.setText("352");
				break;
			case 1:
				playResXTextField.setText("640");
				playResYTextField.setText("360");
				break;
			case 2:
				playResXTextField.setText("640");
				playResYTextField.setText("480");
				break;
			case 3:
				playResXTextField.setText("704");
				playResYTextField.setText("396");
				break;
			case 4:
				playResXTextField.setText("704");
				playResYTextField.setText("400");
				break;
			case 5:
				playResXTextField.setText("704");
				playResYTextField.setText("480");
				break;
			case 6:
				playResXTextField.setText("1024");
				playResYTextField.setText("576");
				break;
			case 7:
				playResXTextField.setText("1280");
				playResYTextField.setText("720");
				break;
			case 8:
				playResXTextField.setText("1920");
				playResYTextField.setText("1080");
				break;
		}
	}

	private void onOK() {
		settings.setPlayResX(playResXTextField.getText());
		settings.setPlayResY(playResYTextField.getText());
		dispose();
	}

	private void onCancel() {
// add your code here if necessary
		dispose();
	}
}

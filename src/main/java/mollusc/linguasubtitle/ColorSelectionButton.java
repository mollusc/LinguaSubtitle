package mollusc.linguasubtitle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Button select color
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class ColorSelectionButton extends JButton implements ActionListener {

	//<editor-fold desc="Private Field">
	/**
	 * Color of this button
	 */
	private Color color;
	//</editor-fold>

	//<editor-fold desc="Constructors">

	/**
	 * Constructor of the class ColorSelectionButton
	 */
	public ColorSelectionButton() {
		this(Color.white);
	}

	/**
	 * Constructor of the class ColorSelectionButton
	 *
	 * @param c color of this button
	 */
	public ColorSelectionButton(Color c) {
		super();
		color = c;
		addActionListener(this);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">

	/**
	 * Get current color of this button
	 *
	 * @return color of this button
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set color of this button
	 *
	 * @param color new color of this button
	 */
	public void setColor(Color color) {
		this.color = color;
		repaint();
		fireStateChanged();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Insets ins = new Insets(5, 5, 5, 5);

		g.setColor(color);
		g.fillRect(
				ins.left,
				ins.top,
				getWidth() - ins.left - ins.right,
				getHeight() - ins.top - ins.bottom);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Color c = JColorChooser.showDialog(
				this,
				"Choose a color...",
				color);
		if (c != null)
			setColor(c);
	}
	//</editor-fold>
}

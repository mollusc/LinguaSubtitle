package mollusc.linguasubtitle;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorSelectionButton extends JButton implements ActionListener {

    private Color color;

    public ColorSelectionButton() {
	this(Color.white);
    }

    public ColorSelectionButton(Color c) {
	super();
	color = c;
	addActionListener(this);
    }

    public Color getColor() {
	return color;
    }

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

    public void actionPerformed(ActionEvent e) {

	Color c = JColorChooser.showDialog(
		this,
		"Choose a color...",
		color);

	if (c != null) {
	    setColor(c);
	}
    }
}

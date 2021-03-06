package mollusc.linguasubtitle.table;


import javax.swing.table.DefaultTableModel;

/**
 * User: mollusc <MolluscLab@gmail.com>
 */
public class MainTableModel extends DefaultTableModel {
	private final Class[] types;
	private final boolean[] isEditable;

	public MainTableModel() {
		super(
				new Object[][]{},
				new String[]{"Name", "Study", "Known", "Word", "Translation", "Amount", "Mentioned"}
		);

		this.types = new Class[]{
				Boolean.class,
				Boolean.class,
				Boolean.class,
				String.class,
				String.class,
				Integer.class,
				Integer.class
		};
		isEditable = new boolean[]{true, true, true, true, true, false, false};

	}

	@Override
	public Class getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return isEditable[columnIndex];
	}
}

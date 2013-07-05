package mollusc.linguasubtitle;

import javax.swing.table.DefaultTableModel;

/**
 * TableModel for statistic
 * User: mollusc <MolluscLab@gmail.com>
 */
class StatisticTableModel extends DefaultTableModel {
	private final Class[] types;
	private final boolean[] isEditable;

	public StatisticTableModel() {
		super(
				new Object[][]{
						{"Number of words", null, null},
						{"Number of unknown words", null, null},
						{"Number of known words", null, null},
						{"Number of studied words", null, null},
						{"Number of new words", null, null}
				},
				new String[]{
						"Parameter", "Unique", "Total"
				}
		);

		this.types = new Class[]{
				String.class,
				String.class,
				String.class,
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

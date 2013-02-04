package mollusc.linguasubtitle;

import javax.swing.table.DefaultTableModel;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 04.02.13
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class StatisticTableModel extends DefaultTableModel {
    Class[] types;
    boolean[] isEditable;
    String[] columnNames;

    public StatisticTableModel() {
        super(
                new Object[][]{
                        {"Общее количество слов", null, null},
                        {"Количество неизвестных слов ", null, null},
                        {"Количество известных слов", null, null},
                        {"Количество изучаемых слов", null, null}
                },
                new String[]{
                        "Параметр", "Уникальных", "Всего"
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

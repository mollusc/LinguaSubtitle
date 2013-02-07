package mollusc.linguasubtitle;


import javax.swing.table.DefaultTableModel;

/**
 * User: mollusc <MolluscLab@gmail.com>
 */
public class MainTableModel extends DefaultTableModel {
    Class[] types;
    boolean[] isEditable;

    public MainTableModel() {
        super(
                new Object[][]{},
                new String[]{"Имя", "Учу", "Знаю", "Слово", "Перевод", "Кол.", "Встречалось ранее"}
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

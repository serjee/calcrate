import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Модель таблицы сделок
 */
public class DealTableModel implements TableModel  {

    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

    private List<DealModel> dealModel;

    public DealTableModel(List<DealModel> dealModel) {
        this.dealModel = dealModel;
    }

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "#";
            case 1:
                return "Депозит";
            case 2:
                return "Ставка";
            case 3:
                return "Профит";
        }
        return "";
    }

    @Override
    public int getRowCount() {
        return dealModel.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        DealModel deal = dealModel.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return String.valueOf(deal.getNum());
            case 1:
                return String.valueOf(deal.getDepo());
            case 2:
                return String.valueOf(deal.getRate());
            case 3:
                return String.valueOf(deal.getProfit());
        }
        return "";
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {

    }

}

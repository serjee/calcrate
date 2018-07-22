import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by SEr on 10.07.2016.
 */
public class DealTableRender extends DefaultTableCellRenderer
{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        setText(value.toString());

        if (Double.parseDouble((String) table.getValueAt(row, 3)) > 0)
        {
            setBackground(new Color(198,239,206));
            setForeground(new Color(0,97,0));
        }
        else
        {
            setBackground(new Color(255,199,206));
            setForeground(new Color(156,0,6));
        }

        return this;

    }
}
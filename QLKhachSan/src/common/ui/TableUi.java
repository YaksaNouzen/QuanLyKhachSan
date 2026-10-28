package common.ui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** Quy ước căn giữa thống nhất cho các bảng trong dashboard. */
public final class TableUi {
    private TableUi() {
    }

    public static void centerCellsAndHeader(JTable table) {
        DefaultTableCellRenderer cells = new DefaultTableCellRenderer();
        cells.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, cells);

        TableCellRenderer originalHeader = table.getTableHeader().getDefaultRenderer();
        table.getTableHeader().setDefaultRenderer((source, value, selected, focused, row, column) -> {
            Component component = originalHeader.getTableCellRendererComponent(
                    source, value, selected, focused, row, column);
            if (component instanceof JLabel) {
                ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
            }
            return component;
        });
    }
}

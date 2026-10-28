package Module_PhanHoiKhachHang.view;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

final class PhanHoiUI {
    static final Color BLUE = new Color(23,54,93);
    static final Color BACKGROUND = new Color(245,247,250);
    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);
    private PhanHoiUI() { }
    static JPanel card(String title) {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,225,232)),
                BorderFactory.createEmptyBorder(12,12,12,12)));
        JLabel label = new JLabel(title); label.setFont(new Font("Arial",Font.BOLD,15));
        label.setForeground(BLUE); panel.add(label,BorderLayout.NORTH); return panel;
    }
    static JButton button(String title, Color color) {
        JButton b = new JButton(title); b.setFont(new Font("Arial",Font.BOLD,12));
        b.setForeground(Color.WHITE); b.setBackground(color); b.setOpaque(true);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setMargin(new Insets(9,12,9,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    static void row(JPanel panel, String label, Component input, int row) {
        GridBagConstraints c = new GridBagConstraints(); c.gridx=0; c.gridy=row;
        c.insets=new Insets(5,6,5,6); c.anchor=GridBagConstraints.NORTHWEST;
        JLabel text=new JLabel(label); text.setFont(new Font("Arial",Font.PLAIN,12)); panel.add(text,c);
        c.gridx=1; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; panel.add(input,c);
    }
    static JTextArea area(int rows) {
        JTextArea a=new JTextArea(rows,35); a.setLineWrap(true); a.setWrapStyleWord(true);
        a.setFont(new Font("Arial",Font.PLAIN,13)); return a;
    }
    static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns,0) {
            @Override public boolean isCellEditable(int row,int column) { return false; }
        };
    }
    static JTable table(DefaultTableModel model) {
        JTable t=new JTable(model); t.setRowHeight(29); t.setFont(new Font("Arial",Font.PLAIN,13));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setFont(new Font("Arial",Font.BOLD,12));
        t.getTableHeader().setForeground(BLUE); t.getTableHeader().setBackground(new Color(225,233,243));
        t.getTableHeader().setReorderingAllowed(false); return t;
    }
    static void error(Component parent, Throwable error) {
        String message = error instanceof IllegalArgumentException || error instanceof IllegalStateException
                ? error.getMessage() : "Không thể hoàn tất thao tác. Vui lòng thử tải lại.";
        JOptionPane.showMessageDialog(parent,message,"Không thể thực hiện",JOptionPane.WARNING_MESSAGE);
    }
    static <T> void background(Component parent, Callable<T> task, Consumer<T> success, Runnable finish) {
        new SwingWorker<T,Void>() {
            @Override protected T doInBackground() throws Exception { return task.call(); }
            @Override protected void done() {
                try { success.accept(get()); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); error(parent,e); }
                catch (ExecutionException e) { error(parent,e.getCause()); }
                finally { finish.run(); }
            }
        }.execute();
    }
    static void configureDialog(JDialog dialog, int width) {
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack(); dialog.setSize(Math.max(width,dialog.getWidth()),dialog.getHeight());
        dialog.setLocationRelativeTo(dialog.getOwner());
    }
}

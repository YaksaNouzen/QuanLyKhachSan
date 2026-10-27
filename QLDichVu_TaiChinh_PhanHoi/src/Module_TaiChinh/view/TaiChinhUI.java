package Module_TaiChinh.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Presentation helpers for the three financial panels, matching HoaDonPanel. */
final class TaiChinhUI {
    static final Color NAVY = new Color(23, 54, 93);
    static final Color BACKGROUND = new Color(245, 247, 250);
    static final Color BLUE = new Color(41, 98, 160);
    static final Color GREEN = new Color(39, 145, 87);
    static final Color ORANGE = new Color(230, 151, 37);
    static final Color RED = new Color(192, 57, 43);
    static final Color GRAY = new Color(108, 117, 125);

    private TaiChinhUI() { }

    static JPanel tieuDe(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(NAVY);
        JLabel subtitle = new JLabel(description);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        text.add(heading);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);
        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    static JPanel khung(String title) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(12, 12, 12, 12)));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(NAVY);
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    static JButton nut(String title, Color color) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(9, 12, 9, 12));
        return button;
    }

    static void bang(JTable table) {
        table.setRowHeight(29);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(225, 233, 243));
        table.getTableHeader().setForeground(NAVY);
        table.getTableHeader().setReorderingAllowed(false);
    }

    static void truong(JPanel panel, String title, JComponent input, int column, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = column;
        c.gridy = row;
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, c);
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(input, c);
    }

    static void nutTraCuu(JPanel form, JButton button) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;
        form.add(button, c);
    }
}

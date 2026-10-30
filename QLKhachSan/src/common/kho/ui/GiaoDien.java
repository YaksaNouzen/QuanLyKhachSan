package common.kho.ui;

import common.ui.ColoredButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public final class GiaoDien {
    public static final Color XANH_DAM = new Color(23, 54, 93);
    public static final Color NEN = new Color(245, 247, 250);
    public static final Color XANH = new Color(43, 101, 168);
    public static final Color XANH_LA = new Color(39, 145, 87);
    public static final Color CAM = new Color(207, 125, 34);
    public static final Color DO = new Color(190, 68, 64);
    public static final Color TIM = new Color(101, 76, 157);
    public static final Color NHAT = new Color(228, 238, 249);

    private GiaoDien() {
    }

    public static ColoredButton nut(String text, Color background, Color foreground) {
        ColoredButton button = new ColoredButton(text, background,
                background.darker(), foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 8, 36));
        return button;
    }

    public static JPanel panelTrangTri() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    public static JLabel tieuDe(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setForeground(XANH_DAM);
        return label;
    }

    public static GridBagConstraints rangBuoc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = x % 2 == 1 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        gbc.weightx = x % 2 == 1 ? 1 : 0;
        return gbc;
    }

    public static void baoLoi(JPanel cha, Exception e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            message = e.getClass().getSimpleName();
        }
        JOptionPane.showMessageDialog(cha, message, "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }
}

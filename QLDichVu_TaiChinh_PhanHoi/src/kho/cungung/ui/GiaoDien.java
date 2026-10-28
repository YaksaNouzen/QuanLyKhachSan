package kho.cungung.ui;

import java.awt.Color;
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

    private GiaoDien() {
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

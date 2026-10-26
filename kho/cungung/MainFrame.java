package kho.cungung;

import Module_QuanLyNhaCungCap.view.NhaCungCapPanel;
import Module_QuanLyNhapHang.view.PhieuNhapPanel;
import Module_QuanLyVatTu.view.VatTuPanel;
import kho.cungung.ui.GiaoDien;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final VatTuPanel vatTuPanel = new VatTuPanel();
    private final NhaCungCapPanel nhaCungCapPanel = new NhaCungCapPanel();
    private final PhieuNhapPanel phieuNhapPanel = new PhieuNhapPanel();

    public MainFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ KHÁCH SẠN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        content.setBackground(GiaoDien.NEN);
        content.add(taoTrangChu(), "HOME");
        content.add(vatTuPanel, "VATTU");
        content.add(nhaCungCapPanel, "NHACUNGCAP");
        content.add(phieuNhapPanel, "PHIEUNHAP");

        add(taoSidebar(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        cardLayout.show(content, "HOME");
    }

    private JPanel taoSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(GiaoDien.XANH_DAM);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel logo = new JLabel("HOTEL MANAGER");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 19));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("KHO & CUNG ỨNG");
        subtitle.setForeground(new Color(210, 220, 235));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(35));
        sidebar.add(taoNut("Trang chủ", "HOME", null));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(taoNut("Quản lý vật tư", "VATTU", vatTuPanel::taiDuLieu));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(taoNut("Nhà cung cấp", "NHACUNGCAP", nhaCungCapPanel::taiDuLieu));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(taoNut("Nhập hàng", "PHIEUNHAP", phieuNhapPanel::taiDuLieu));
        sidebar.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("Phiên bản 1.0");
        footer.setForeground(new Color(190, 205, 225));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);
        return sidebar;
    }

    private JButton taoNut(String ten, String trang, Runnable khiMo) {
        JButton button = new JButton(ten);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(195, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(GiaoDien.XANH_DAM);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 15, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            if (khiMo != null) {
                khiMo.run();
            }
            cardLayout.show(content, trang);
        });
        return button;
    }

    private JPanel taoTrangChu() {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setBackground(GiaoDien.NEN);
        JPanel box = GiaoDien.panelTrangTri();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(35, 45, 35, 45));
        JLabel title = GiaoDien.tieuDe("QUẢN LÝ KHO & CUNG ỨNG");
        JLabel desc = new JLabel("Quản lý vật tư, nhà cung cấp và phiếu nhập hàng.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(15));
        box.add(desc);
        panel.add(box);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MainFrame().setVisible(true);
        });
    }
}

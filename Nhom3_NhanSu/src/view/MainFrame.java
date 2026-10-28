package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import util.DBConnection;
import util.GiaoDien;
import util.PhienDangNhap;

/**
 * Màn hình chính: menu bên trái, nội dung bên phải.
 * Menu hiện ra tùy theo vai trò của người đăng nhập (PHÂN QUYỀN).
 */
public class MainFrame extends JFrame {

    private static final Color MAU_MENU_CHON = new Color(40, 80, 130);

    private JPanel pnMenu;
    private JPanel pnNoiDung;
    private final List<JButton> dsNutMenu = new ArrayList<>();

    public MainFrame() {
        initComponents();
        setTitle("HỆ THỐNG QUẢN LÝ KHÁCH SẠN" + (DBConnection.laCheDoChayThu() ? "  (CHẠY THỬ - dữ liệu mẫu)" : ""));
        setSize(1400, 900);
        setMinimumSize(new Dimension(1200, 750));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // mở toàn màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dsNutMenu.get(0).setBackground(MAU_MENU_CHON);   // tô sáng "Trang chủ" lúc mới vào
        hienThi(new TrangChuPanel());
    }

    private void initComponents() {
        pnMenu = new JPanel();
        pnMenu.setLayout(new BoxLayout(pnMenu, BoxLayout.Y_AXIS));
        pnMenu.setBackground(GiaoDien.MAU_MENU);
        pnMenu.setPreferredSize(new Dimension(300, 0));
        pnMenu.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JLabel lblLogo = new JLabel("HOTEL MANAGER");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblPhu = new JLabel("QUẢN LÝ KHÁCH SẠN");
        lblPhu.setFont(GiaoDien.FONT_CHU);
        lblPhu.setForeground(new Color(200, 210, 225));
        lblPhu.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMenu.add(lblLogo);
        pnMenu.add(lblPhu);
        pnMenu.add(Box.createVerticalStrut(40));

        // ===== PHÂN QUYỀN MENU =====
        String vaiTro = PhienDangNhap.getTaiKhoan().getMaVaiTro();

        themMenu("Trang chủ", () -> hienThi(new TrangChuPanel()));

        if (vaiTro.equals("ADMIN")) {
            themMenu("Quản lý nhân viên", () -> hienThi(new NhanVienPanel(false)));
            themMenu("Quản lý ca làm", () -> hienThi(new CaLamPanel()));
        }
        if (vaiTro.equals("KETOAN")) {
            // Kế toán chỉ được xem danh sách nhân viên (để tính lương)
            themMenu("Danh sách nhân viên", () -> hienThi(new NhanVienPanel(true)));
        }

        themMenu("Lịch trực & bàn giao", () -> hienThi(new PhanCaPanel()));

        if (vaiTro.equals("ADMIN")) {
            themMenu("Tài khoản & phân quyền", () -> hienThi(new TaiKhoanPanel()));
        }

        themMenu("Đổi mật khẩu", () -> hienThi(new DoiMatKhauPanel()));
        // Các nhóm khác (Phòng, Hóa đơn...) thêm menu của mình vào đây theo cùng cách

        pnMenu.add(Box.createVerticalGlue());

        JButton btnDangXuat = taoNutMenu("Đăng xuất");
        btnDangXuat.addActionListener(e -> dangXuat());
        pnMenu.add(btnDangXuat);
        pnMenu.add(Box.createVerticalStrut(15));

        JLabel lblPhienBan = new JLabel("Phiên bản 1.0");
        lblPhienBan.setForeground(new Color(200, 210, 225));
        lblPhienBan.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMenu.add(lblPhienBan);

        pnNoiDung = new JPanel(new BorderLayout());
        pnNoiDung.setBackground(GiaoDien.MAU_NEN);

        add(pnMenu, BorderLayout.WEST);
        add(pnNoiDung, BorderLayout.CENTER);
    }

    // Tạo 1 nút menu, khi bấm thì chạy hanhDong và tô sáng nút đó
    private void themMenu(String ten, Runnable hanhDong) {
        JButton nut = taoNutMenu(ten);
        nut.addActionListener(e -> {
            for (JButton b : dsNutMenu) {
                b.setBackground(GiaoDien.MAU_MENU);
            }
            nut.setBackground(MAU_MENU_CHON);
            hanhDong.run();
        });
        dsNutMenu.add(nut);
        pnMenu.add(nut);
    }

    private JButton taoNutMenu(String ten) {
        JButton nut = new JButton(ten);
        nut.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nut.setForeground(Color.WHITE);
        nut.setBackground(GiaoDien.MAU_MENU);
        nut.setHorizontalAlignment(JButton.LEFT);
        nut.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        nut.setFocusPainted(false);
        nut.setOpaque(true);
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nut.setPreferredSize(new Dimension(300, 55));
        nut.setMaximumSize(new Dimension(300, 55));   // BoxLayout dùng 2 kích thước này để giữ chiều cao nút
        nut.setAlignmentX(Component.CENTER_ALIGNMENT);
        return nut;
    }

    // Đổi nội dung bên phải
    private void hienThi(JPanel pn) {
        pnNoiDung.removeAll();
        pnNoiDung.add(pn, BorderLayout.CENTER);
        pnNoiDung.revalidate();
        pnNoiDung.repaint();
    }


    private void dangXuat() {
        int chon = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (chon == JOptionPane.YES_OPTION) {
            PhienDangNhap.dangXuat();
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }
}

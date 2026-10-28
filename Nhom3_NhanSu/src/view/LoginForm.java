package view;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import model.NhanVien;
import model.TaiKhoan;
import util.DBConnection;
import util.GiaoDien;
import util.PhienDangNhap;

public class LoginForm extends JFrame {

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap, btnThoat;

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private int soLanSai = 0;

    public LoginForm() {
        initComponents();
        setTitle("ĐĂNG NHẬP HỆ THỐNG");
        setSize(760, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // Bên trái: logo
        JPanel pnTrai = new JPanel();
        pnTrai.setLayout(new BoxLayout(pnTrai, BoxLayout.Y_AXIS));
        pnTrai.setBackground(GiaoDien.MAU_MENU);
        pnTrai.setPreferredSize(new Dimension(300, 0));
        pnTrai.setBorder(BorderFactory.createEmptyBorder(130, 40, 0, 0));
        JLabel lblLogo = new JLabel("HOTEL MANAGER");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);
        JLabel lblPhu = new JLabel("QUẢN LÝ KHÁCH SẠN");
        lblPhu.setFont(GiaoDien.FONT_CHU);
        lblPhu.setForeground(new Color(200, 210, 225));
        pnTrai.add(lblLogo);
        pnTrai.add(lblPhu);

        // Bên phải: form đăng nhập
        JPanel pnPhai = new JPanel(new BorderLayout());
        pnPhai.setBackground(Color.WHITE);
        pnPhai.setBorder(BorderFactory.createEmptyBorder(50, 40, 40, 40));

        JLabel lblTieuDe = new JLabel("ĐĂNG NHẬP");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTieuDe.setForeground(GiaoDien.MAU_TIEU_DE);
        pnPhai.add(lblTieuDe, BorderLayout.NORTH);

        JPanel pnForm = new JPanel(new GridLayout(4, 1, 0, 6));
        pnForm.setBackground(Color.WHITE);
        pnForm.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        txtTenDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();
        txtTenDangNhap.setFont(GiaoDien.FONT_CHU);
        txtMatKhau.setFont(GiaoDien.FONT_CHU);
        pnForm.add(GiaoDien.taoNhan("Tên đăng nhập:"));
        pnForm.add(txtTenDangNhap);
        pnForm.add(GiaoDien.taoNhan("Mật khẩu:"));
        pnForm.add(txtMatKhau);
        pnPhai.add(pnForm, BorderLayout.CENTER);

        JPanel pnNut = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnNut.setBackground(Color.WHITE);
        btnDangNhap = GiaoDien.taoNut("Đăng nhập", GiaoDien.XANH_DUONG);
        btnThoat = GiaoDien.taoNut("Thoát", GiaoDien.XAM);
        pnNut.add(btnDangNhap);
        pnNut.add(new JLabel("   "));
        pnNut.add(btnThoat);
        pnPhai.add(pnNut, BorderLayout.SOUTH);

        add(pnTrai, BorderLayout.WEST);
        add(pnPhai, BorderLayout.CENTER);

        // Sự kiện
        btnDangNhap.addActionListener(e -> dangNhap());
        txtTenDangNhap.addActionListener(e -> txtMatKhau.requestFocus());   // Enter ở ô tên -> xuống ô mật khẩu
        txtMatKhau.addActionListener(e -> dangNhap());   // nhấn Enter ở ô mật khẩu
        btnThoat.addActionListener(e -> System.exit(0));
    }

    private void dangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        // B1: kiểm tra rỗng
        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu!");
            return;
        }

        // B2: không kết nối được database thì báo riêng, không tính là nhập sai.
        // Cho phép chọn chạy thử với dữ liệu mẫu (khi máy chưa cài SQL Server)
        if (!DBConnection.kiemTraKetNoi()) {
            int chon = JOptionPane.showConfirmDialog(this, "Không kết nối được database!\n"
                    + "Kiểm tra SQL Server đã bật chưa và thông tin trong DBConnection.java\n\n"
                    + "Bạn có muốn CHẠY THỬ với dữ liệu mẫu không?\n"
                    + "(dữ liệu chỉ lưu tạm, tắt chương trình là mất)",
                    "Lỗi kết nối", JOptionPane.YES_NO_OPTION);
            if (chon != JOptionPane.YES_OPTION || !DBConnection.batCheDoChayThu()) {
                return;
            }
        }

        // B3: tìm tài khoản trong database
        TaiKhoan tk = taiKhoanDAO.dangNhap(tenDangNhap, matKhau);
        if (tk == null) {
            soLanSai++;
            if (soLanSai >= 3) {
                JOptionPane.showMessageDialog(this, "Bạn đã nhập sai 3 lần. Chương trình sẽ thoát!");
                System.exit(0);
            }
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu! (lần " + soLanSai + "/3)");
            txtMatKhau.setText("");
            return;
        }

        // B4: tài khoản bị khóa thì không cho vào
        if (!tk.isTrangThai()) {
            JOptionPane.showMessageDialog(this, "Tài khoản đã bị khóa. Liên hệ Admin!");
            return;
        }

        // B5: nhân viên đã nghỉ việc thì không cho vào
        NhanVien nv = nhanVienDAO.getById(tk.getMaNV());
        if (nv != null && nv.getTrangThai().equals("Đã nghỉ việc")) {
            JOptionPane.showMessageDialog(this, "Nhân viên đã nghỉ việc, không thể đăng nhập!");
            return;
        }

        // B6: lưu phiên đăng nhập, mở màn hình chính
        PhienDangNhap.setTaiKhoan(tk);
        new MainFrame().setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}

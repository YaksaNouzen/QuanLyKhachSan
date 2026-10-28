package view;

import dao.TaiKhoanDAO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import util.DBConnection;
import util.GiaoDien;
import util.PhienDangNhap;

/**
 * MODULE 9: ĐỔI MẬT KHẨU (mọi vai trò đều dùng được)
 */
public class DoiMatKhauPanel extends JPanel {

    private JPasswordField txtMatKhauCu, txtMatKhauMoi, txtXacNhan;
    private JButton btnDoiMatKhau, btnHuy;
    private final TaiKhoanDAO dao = new TaiKhoanDAO();

    public DoiMatKhauPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GiaoDien.MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        JPanel khung = GiaoDien.taoKhung("Đổi mật khẩu cho tài khoản: " + PhienDangNhap.getTaiKhoan().getTenDangNhap());
        khung.setLayout(new GridLayout(3, 2, 15, 12));
        txtMatKhauCu = new JPasswordField();
        txtMatKhauMoi = new JPasswordField();
        txtXacNhan = new JPasswordField();
        khung.add(GiaoDien.taoNhan("Mật khẩu hiện tại:"));
        khung.add(txtMatKhauCu);
        khung.add(GiaoDien.taoNhan("Mật khẩu mới:"));
        khung.add(txtMatKhauMoi);
        khung.add(GiaoDien.taoNhan("Nhập lại mật khẩu mới:"));
        khung.add(txtXacNhan);

        JPanel hangNut = GiaoDien.taoHangNut();
        btnDoiMatKhau = GiaoDien.taoNut("Đổi mật khẩu", GiaoDien.XANH_DUONG);
        btnHuy = GiaoDien.taoNut("Nhập lại", GiaoDien.XAM);
        hangNut.add(btnDoiMatKhau);
        hangNut.add(btnHuy);

        JPanel giua = new JPanel(new BorderLayout());
        giua.setOpaque(false);
        giua.add(khung, BorderLayout.NORTH);
        giua.add(hangNut, BorderLayout.CENTER);

        add(GiaoDien.taoTieuDe("ĐỔI MẬT KHẨU", "Mật khẩu mới phải có ít nhất 6 ký tự"), BorderLayout.NORTH);
        add(giua, BorderLayout.CENTER);

        btnDoiMatKhau.addActionListener(e -> doiMatKhau());
        btnHuy.addActionListener(e -> xoaForm());
    }

    private void doiMatKhau() {
        String tenDN = PhienDangNhap.getTaiKhoan().getTenDangNhap();
        String cu = new String(txtMatKhauCu.getPassword());
        String moi = new String(txtMatKhauMoi.getPassword());
        String xacNhan = new String(txtXacNhan.getPassword());

        if (cu.isEmpty() || moi.isEmpty() || xacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ 3 ô!");
            return;
        }
        // Kiểm tra trên form trước, đúng hết rồi mới hỏi database
        if (moi.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }
        if (!moi.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Nhập lại mật khẩu không khớp!");
            return;
        }
        if (moi.equals(cu)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải khác mật khẩu cũ!");
            return;
        }
        if (!DBConnection.kiemTraKetNoi()) {
            JOptionPane.showMessageDialog(this, "Không kết nối được database!");
            return;
        }
        // Dùng lại hàm đăng nhập để kiểm tra mật khẩu cũ có đúng không
        if (dao.dangNhap(tenDN, cu) == null) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!");
            return;
        }

        if (dao.capNhatMatKhau(tenDN, moi)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            xoaForm();
        } else {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!");
        }
    }

    private void xoaForm() {
        txtMatKhauCu.setText("");
        txtMatKhauMoi.setText("");
        txtXacNhan.setText("");
    }
}

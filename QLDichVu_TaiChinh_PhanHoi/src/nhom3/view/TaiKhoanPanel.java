package nhom3.view;

import nhom3.dao.NhanVienDAO;
import nhom3.dao.TaiKhoanDAO;
import nhom3.dao.VaiTroDAO;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nhom3.model.NhanVien;
import nhom3.model.TaiKhoan;
import nhom3.model.VaiTro;
import nhom3.util.GiaoDien;
import nhom3.util.PhienDangNhap;

/**
 * MODULE 9: QUẢN LÝ TÀI KHOẢN & PHÂN QUYỀN (chỉ Admin vào được)
 * - Tạo tài khoản cho nhân viên, gán vai trò
 * - Khóa / mở khóa, đặt lại mật khẩu, xóa tài khoản
 */
public class TaiKhoanPanel extends JPanel {

    private static final String MAT_KHAU_MAC_DINH = "123456";

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cboNhanVien;
    private JComboBox<VaiTro> cboVaiTro;
    private JComboBox<String> cboTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnDatLaiMK, btnLamMoi;
    private JTable tblTaiKhoan;
    private DefaultTableModel model;

    private final TaiKhoanDAO dao = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();
    private List<TaiKhoan> danhSach;

    public TaiKhoanPanel() {
        initComponents();
        loadComboBox();
        loadTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GiaoDien.MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        JPanel khungThongTin = GiaoDien.taoKhung("Thông tin tài khoản");
        khungThongTin.setLayout(new BorderLayout());
        JPanel luoi = GiaoDien.taoLuoiForm();
        txtTenDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();
        cboNhanVien = new JComboBox<>();
        cboVaiTro = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Bị khóa"});
        luoi.add(GiaoDien.taoNhan("Tên đăng nhập:"));
        luoi.add(txtTenDangNhap);
        luoi.add(GiaoDien.taoNhan("Mật khẩu:"));
        luoi.add(txtMatKhau);
        luoi.add(GiaoDien.taoNhan("Nhân viên:"));
        luoi.add(cboNhanVien);
        luoi.add(GiaoDien.taoNhan("Vai trò:"));
        luoi.add(cboVaiTro);
        luoi.add(GiaoDien.taoNhan("Trạng thái:"));
        luoi.add(cboTrangThai);
        khungThongTin.add(luoi);

        JPanel khungBang = GiaoDien.taoKhung("Danh sách tài khoản");
        khungBang.setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Tên đăng nhập", "Mã NV", "Họ tên", "Vai trò", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTaiKhoan = new JTable(model);
        GiaoDien.dinhDangBang(tblTaiKhoan);
        khungBang.add(new JScrollPane(tblTaiKhoan));

        JPanel hangNut = GiaoDien.taoHangNut();
        btnThem = GiaoDien.taoNut("Tạo tài khoản", GiaoDien.XANH_LA);
        btnSua = GiaoDien.taoNut("Sửa quyền / trạng thái", GiaoDien.CAM);
        btnXoa = GiaoDien.taoNut("Xóa tài khoản", GiaoDien.DO);
        btnDatLaiMK = GiaoDien.taoNut("Đặt lại mật khẩu", GiaoDien.TIM);
        btnLamMoi = GiaoDien.taoNut("Làm mới", GiaoDien.XAM);
        hangNut.add(btnThem);
        hangNut.add(btnSua);
        hangNut.add(btnXoa);
        hangNut.add(btnDatLaiMK);
        hangNut.add(btnLamMoi);

        JPanel phanTren = new JPanel();
        phanTren.setLayout(new BoxLayout(phanTren, BoxLayout.Y_AXIS));
        phanTren.setOpaque(false);
        phanTren.add(GiaoDien.taoTieuDe("TÀI KHOẢN & PHÂN QUYỀN", "Quản lý tài khoản đăng nhập và vai trò người dùng"));
        phanTren.add(khungThongTin);
        add(phanTren, BorderLayout.NORTH);
        add(khungBang, BorderLayout.CENTER);
        add(hangNut, BorderLayout.SOUTH);

        tblTaiKhoan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {   // chỉ chạy 1 lần khi đã thả chuột
                hienThiDongDangChon();
            }
        });
        btnThem.addActionListener(e -> themTaiKhoan());
        btnSua.addActionListener(e -> suaTaiKhoan());
        btnXoa.addActionListener(e -> xoaTaiKhoan());
        btnDatLaiMK.addActionListener(e -> datLaiMatKhau());
        btnLamMoi.addActionListener(e -> lamMoi());
    }

    private void loadComboBox() {
        cboNhanVien.removeAllItems();
        for (NhanVien nv : nhanVienDAO.getNhanVienDangLam()) {
            cboNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTen());
        }
        cboVaiTro.removeAllItems();
        for (VaiTro vt : vaiTroDAO.getAll()) {
            cboVaiTro.addItem(vt);
        }
    }

    private void loadTable() {
        model.setRowCount(0);
        danhSach = dao.getAll();
        for (TaiKhoan tk : danhSach) {
            model.addRow(new Object[]{tk.getTenDangNhap(), tk.getMaNV(), tk.getTenNV(),
                tk.getTenVaiTro(), tk.isTrangThai() ? "Hoạt động" : "Bị khóa"});
        }
    }

    private void hienThiDongDangChon() {
        int dong = tblTaiKhoan.getSelectedRow();
        if (dong < 0) {
            return;
        }
        TaiKhoan tk = danhSach.get(dong);
        txtTenDangNhap.setText(tk.getTenDangNhap());
        txtMatKhau.setText("");

        // Chọn đúng nhân viên trong combobox (nếu không có thì thêm vào)
        String item = tk.getMaNV() + " - " + tk.getTenNV();
        if (((DefaultComboBoxModel<String>) cboNhanVien.getModel()).getIndexOf(item) < 0) {
            cboNhanVien.addItem(item);
        }
        cboNhanVien.setSelectedItem(item);

        // Chọn đúng vai trò
        for (int i = 0; i < cboVaiTro.getItemCount(); i++) {
            if (cboVaiTro.getItemAt(i).getMaVaiTro().equals(tk.getMaVaiTro())) {
                cboVaiTro.setSelectedIndex(i);
            }
        }
        cboTrangThai.setSelectedIndex(tk.isTrangThai() ? 0 : 1);

        // Đã có tài khoản thì không đổi tên đăng nhập, nhân viên, mật khẩu ở đây
        txtTenDangNhap.setEditable(false);
        txtMatKhau.setEnabled(false);
        cboNhanVien.setEnabled(false);
    }

    private void themTaiKhoan() {
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDN.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập và mật khẩu không được để trống!");
            return;
        }
        if (!tenDN.matches("[a-zA-Z0-9_]{4,30}")) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập từ 4-30 ký tự, chỉ gồm chữ, số, dấu _");
            return;
        }
        if (matKhau.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (cboNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Chưa có nhân viên để tạo tài khoản!");
            return;
        }
        String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];

        if (dao.kiemTraTenDangNhap(tenDN)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
            return;
        }
        if (dao.nhanVienDaCoTaiKhoan(maNV)) {
            JOptionPane.showMessageDialog(this, "Nhân viên " + maNV + " đã có tài khoản rồi!");
            return;
        }

        VaiTro vt = (VaiTro) cboVaiTro.getSelectedItem();
        TaiKhoan tk = new TaiKhoan(tenDN, matKhau, maNV, vt.getMaVaiTro(), cboTrangThai.getSelectedIndex() == 0);
        if (dao.them(tk)) {
            JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo tài khoản thất bại!");
        }
    }

    private void suaTaiKhoan() {
        int dong = tblTaiKhoan.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn tài khoản cần sửa!");
            return;
        }
        TaiKhoan tk = danhSach.get(dong);
        VaiTro vt = (VaiTro) cboVaiTro.getSelectedItem();
        boolean hoatDong = cboTrangThai.getSelectedIndex() == 0;

        // Không cho Admin tự hạ quyền hoặc tự khóa chính mình
        if (laChinhMinh(tk) && (!vt.getMaVaiTro().equals("ADMIN") || !hoatDong)) {
            JOptionPane.showMessageDialog(this, "Không thể tự đổi quyền hoặc tự khóa tài khoản đang đăng nhập!");
            return;
        }

        tk.setMaVaiTro(vt.getMaVaiTro());
        tk.setTrangThai(hoatDong);
        if (dao.sua(tk)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void xoaTaiKhoan() {
        int dong = tblTaiKhoan.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn tài khoản cần xóa!");
            return;
        }
        TaiKhoan tk = danhSach.get(dong);
        if (laChinhMinh(tk)) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản đang đăng nhập!");
            return;
        }
        int chon = JOptionPane.showConfirmDialog(this, "Xóa tài khoản " + tk.getTenDangNhap() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (chon == JOptionPane.YES_OPTION && dao.xoa(tk.getTenDangNhap())) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        }
    }

    // Nhân viên quên mật khẩu -> Admin đặt lại về mật khẩu mặc định
    private void datLaiMatKhau() {
        int dong = tblTaiKhoan.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn tài khoản cần đặt lại mật khẩu!");
            return;
        }
        TaiKhoan tk = danhSach.get(dong);
        int chon = JOptionPane.showConfirmDialog(this, "Đặt lại mật khẩu của " + tk.getTenDangNhap()
                + " về " + MAT_KHAU_MAC_DINH + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (chon == JOptionPane.YES_OPTION && dao.capNhatMatKhau(tk.getTenDangNhap(), MAT_KHAU_MAC_DINH)) {
            JOptionPane.showMessageDialog(this, "Đã đặt lại mật khẩu thành " + MAT_KHAU_MAC_DINH);
        }
    }

    private boolean laChinhMinh(TaiKhoan tk) {
        return tk.getTenDangNhap().equals(PhienDangNhap.getTaiKhoan().getTenDangNhap());
    }

    private void lamMoi() {
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtTenDangNhap.setEditable(true);
        txtMatKhau.setEnabled(true);
        cboNhanVien.setEnabled(true);
        cboTrangThai.setSelectedIndex(0);
        tblTaiKhoan.clearSelection();
        loadComboBox();
        loadTable();
    }
}

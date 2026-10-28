package view;

import dao.NhanVienDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.Period;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.NhanVien;
import util.GiaoDien;

/**
 * MODULE 7: QUẢN LÝ NHÂN VIÊN
 * - CRUD hồ sơ nhân viên
 * - Phân bộ phận, chức vụ
 * - Tìm kiếm, quản lý trạng thái làm việc
 */
public class NhanVienPanel extends JPanel {

    private static final String[] BO_PHAN = {"Lễ tân", "Buồng phòng", "Kế toán", "Bảo vệ", "Quản lý"};
    private static final String[] CHUC_VU = {"Trưởng bộ phận", "Nhân viên", "Thực tập"};
    private static final String[] TRANG_THAI = {"Đang làm", "Nghỉ phép", "Đã nghỉ việc"};

    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtSDT, txtEmail, txtDiaChi, txtNgayVaoLam;
    private JComboBox<String> cboGioiTinh, cboBoPhan, cboChucVu, cboTrangThai;
    private JTextField txtTuKhoa;
    private JComboBox<String> cboLocBoPhan, cboLocTrangThai;
    private JButton btnTimKiem, btnBoLoc, btnThem, btnSua, btnXoa, btnLamMoi;
    private JTable tblNhanVien;
    private DefaultTableModel model;

    private final NhanVienDAO dao = new NhanVienDAO();
    private List<NhanVien> danhSach;   // danh sách đang hiện trên bảng
    private final boolean chiXem;      // true: chỉ được xem (Kế toán)

    public NhanVienPanel(boolean chiXem) {
        this.chiXem = chiXem;
        initComponents();
        lamMoi();   // xóa form, điền sẵn ngày vào làm = hôm nay, load bảng
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GiaoDien.MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        // ----- Khung thông tin -----
        JPanel khungThongTin = GiaoDien.taoKhung("Thông tin nhân viên");
        khungThongTin.setLayout(new BorderLayout());
        JPanel luoi = GiaoDien.taoLuoiForm();
        txtMaNV = new JTextField();
        txtHoTen = new JTextField();
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        txtNgaySinh = new JTextField();
        txtSDT = new JTextField();
        txtEmail = new JTextField();
        txtDiaChi = new JTextField();
        cboBoPhan = new JComboBox<>(BO_PHAN);
        cboChucVu = new JComboBox<>(CHUC_VU);
        txtNgayVaoLam = new JTextField();
        cboTrangThai = new JComboBox<>(TRANG_THAI);

        luoi.add(GiaoDien.taoNhan("Mã nhân viên:"));
        luoi.add(txtMaNV);
        luoi.add(GiaoDien.taoNhan("Họ tên:"));
        luoi.add(txtHoTen);
        luoi.add(GiaoDien.taoNhan("Giới tính:"));
        luoi.add(cboGioiTinh);
        luoi.add(GiaoDien.taoNhan("Ngày sinh (yyyy-MM-dd):"));
        luoi.add(txtNgaySinh);
        luoi.add(GiaoDien.taoNhan("Số điện thoại:"));
        luoi.add(txtSDT);
        luoi.add(GiaoDien.taoNhan("Email:"));
        luoi.add(txtEmail);
        luoi.add(GiaoDien.taoNhan("Địa chỉ:"));
        luoi.add(txtDiaChi);
        luoi.add(GiaoDien.taoNhan("Ngày vào làm (yyyy-MM-dd):"));
        luoi.add(txtNgayVaoLam);
        luoi.add(GiaoDien.taoNhan("Bộ phận:"));
        luoi.add(cboBoPhan);
        luoi.add(GiaoDien.taoNhan("Chức vụ:"));
        luoi.add(cboChucVu);
        luoi.add(GiaoDien.taoNhan("Trạng thái:"));
        luoi.add(cboTrangThai);
        khungThongTin.add(luoi);

        // ----- Khung tìm kiếm -----
        JPanel khungTimKiem = GiaoDien.taoKhung("Tìm kiếm nhân viên");
        khungTimKiem.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));
        txtTuKhoa = new JTextField(10);
        cboLocBoPhan = new JComboBox<>();
        cboLocBoPhan.addItem("Tất cả");
        for (String bp : BO_PHAN) {
            cboLocBoPhan.addItem(bp);
        }
        cboLocTrangThai = new JComboBox<>();
        cboLocTrangThai.addItem("Tất cả");
        for (String tt : TRANG_THAI) {
            cboLocTrangThai.addItem(tt);
        }
        btnTimKiem = GiaoDien.taoNut("Tìm kiếm", GiaoDien.XANH_DUONG);
        btnBoLoc = GiaoDien.taoNut("Bỏ lọc", GiaoDien.XAM);
        khungTimKiem.add(GiaoDien.taoNhan("Mã / Tên / SĐT:"));
        khungTimKiem.add(txtTuKhoa);
        khungTimKiem.add(GiaoDien.taoNhan("Bộ phận:"));
        khungTimKiem.add(cboLocBoPhan);
        khungTimKiem.add(GiaoDien.taoNhan("Trạng thái:"));
        khungTimKiem.add(cboLocTrangThai);
        khungTimKiem.add(btnTimKiem);
        khungTimKiem.add(btnBoLoc);

        // ----- Bảng danh sách -----
        JPanel khungBang = GiaoDien.taoKhung("Danh sách nhân viên");
        khungBang.setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Mã NV", "Họ tên", "Giới tính", "Ngày sinh",
            "SĐT", "Email", "Bộ phận", "Chức vụ", "Ngày vào làm", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // không cho sửa trực tiếp trên bảng
            }
        };
        tblNhanVien = new JTable(model);
        GiaoDien.dinhDangBang(tblNhanVien);
        khungBang.add(new JScrollPane(tblNhanVien));

        // ----- Hàng nút -----
        JPanel hangNut = GiaoDien.taoHangNut();
        btnThem = GiaoDien.taoNut("Thêm nhân viên", GiaoDien.XANH_LA);
        btnSua = GiaoDien.taoNut("Sửa nhân viên", GiaoDien.CAM);
        btnXoa = GiaoDien.taoNut("Xóa nhân viên", GiaoDien.DO);
        btnLamMoi = GiaoDien.taoNut("Làm mới", GiaoDien.XAM);
        if (!chiXem) {
            hangNut.add(btnThem);
            hangNut.add(btnSua);
            hangNut.add(btnXoa);
        }
        hangNut.add(btnLamMoi);

        // Ghép các phần
        JPanel phanTren = new JPanel();
        phanTren.setLayout(new BoxLayout(phanTren, BoxLayout.Y_AXIS));
        phanTren.setOpaque(false);
        phanTren.add(GiaoDien.taoTieuDe("QUẢN LÝ NHÂN VIÊN", "Quản lý hồ sơ, bộ phận, chức vụ và trạng thái làm việc"));
        phanTren.add(khungThongTin);
        phanTren.add(khungTimKiem);
        add(phanTren, BorderLayout.NORTH);
        add(khungBang, BorderLayout.CENTER);
        add(hangNut, BorderLayout.SOUTH);

        // ----- Sự kiện -----
        tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {   // chỉ chạy 1 lần khi đã thả chuột
                hienThiDongDangChon();
            }
        });
        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnXoa.addActionListener(e -> xoaNhanVien());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTimKiem.addActionListener(e -> timKiem());
        txtTuKhoa.addActionListener(e -> timKiem());
        btnBoLoc.addActionListener(e -> {
            txtTuKhoa.setText("");
            cboLocBoPhan.setSelectedIndex(0);
            cboLocTrangThai.setSelectedIndex(0);
            loadTable(dao.getAll());
        });
    }

    // Đổ danh sách lên bảng
    private void loadTable(List<NhanVien> list) {
        model.setRowCount(0);   // xóa hết dòng cũ
        danhSach = list;
        for (NhanVien nv : list) {
            model.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(), nv.getGioiTinh(), nv.getNgaySinh(),
                nv.getSdt(), nv.getEmail(), nv.getBoPhan(), nv.getChucVu(), nv.getNgayVaoLam(), nv.getTrangThai()});
        }
    }

    // Click vào 1 dòng -> đưa dữ liệu lên form
    private void hienThiDongDangChon() {
        int dong = tblNhanVien.getSelectedRow();
        if (dong < 0) {
            return;
        }
        NhanVien nv = danhSach.get(dong);
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        cboGioiTinh.setSelectedItem(nv.getGioiTinh());
        txtNgaySinh.setText(nv.getNgaySinh() == null ? "" : nv.getNgaySinh().toString());
        txtSDT.setText(nv.getSdt());
        txtEmail.setText(nv.getEmail());
        txtDiaChi.setText(nv.getDiaChi());
        cboBoPhan.setSelectedItem(nv.getBoPhan());
        cboChucVu.setSelectedItem(nv.getChucVu());
        txtNgayVaoLam.setText(nv.getNgayVaoLam() == null ? "" : nv.getNgayVaoLam().toString());
        cboTrangThai.setSelectedItem(nv.getTrangThai());
        txtMaNV.setEditable(false);   // không cho sửa khóa chính
    }

    /**
     * Kiểm tra dữ liệu trên form. Hợp lệ thì trả về đối tượng NhanVien,
     * sai thì báo lỗi và trả về null.
     */
    private NhanVien layDuLieuForm() {
        String ma = txtMaNV.getText().trim().toUpperCase();
        String ten = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã và họ tên không được để trống!");
            return null;
        }
        if (!ma.matches("NV\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên phải có dạng NVxxx (VD: NV005)!");
            return null;
        }
        if (!sdt.matches("0\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 số và bắt đầu bằng 0!");
            return null;
        }
        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Email không đúng định dạng!");
            return null;
        }

        Date ngaySinh, ngayVaoLam;
        try {
            ngaySinh = Date.valueOf(LocalDate.parse(txtNgaySinh.getText().trim()));
            ngayVaoLam = Date.valueOf(LocalDate.parse(txtNgayVaoLam.getText().trim()));
        } catch (DateTimeParseException e) {   // sai định dạng hoặc ngày không có thật (VD 30/02)
            JOptionPane.showMessageDialog(this, "Ngày phải nhập theo dạng yyyy-MM-dd!");
            return null;
        }

        // Tính tuổi chính xác đến ngày, phải từ 18 trở lên
        int tuoi = Period.between(ngaySinh.toLocalDate(), LocalDate.now()).getYears();
        if (tuoi < 18) {
            JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi!");
            return null;
        }
        if (ngayVaoLam.before(ngaySinh)) {
            JOptionPane.showMessageDialog(this, "Ngày vào làm phải sau ngày sinh!");
            return null;
        }

        return new NhanVien(ma, ten, cboGioiTinh.getSelectedItem().toString(), ngaySinh, sdt, email,
                txtDiaChi.getText().trim(), cboBoPhan.getSelectedItem().toString(),
                cboChucVu.getSelectedItem().toString(), ngayVaoLam, cboTrangThai.getSelectedItem().toString());
    }

    private void themNhanVien() {
        NhanVien nv = layDuLieuForm();
        if (nv == null) {
            return;
        }
        if (dao.getById(nv.getMaNV()) != null) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên " + nv.getMaNV() + " đã tồn tại!");
            return;
        }
        if (dao.them(nv)) {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    private void suaNhanVien() {
        if (tblNhanVien.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên cần sửa trên bảng!");
            return;
        }
        NhanVien nv = layDuLieuForm();
        if (nv == null) {
            return;
        }
        if (dao.sua(nv)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void xoaNhanVien() {
        int dong = tblNhanVien.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên cần xóa trên bảng!");
            return;
        }
        String maNV = danhSach.get(dong).getMaNV();
        int chon = JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + maNV + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (chon != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.xoa(maNV)) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } else {
            // Bị ràng buộc khóa ngoại -> gợi ý đổi trạng thái thay vì xóa
            JOptionPane.showMessageDialog(this, "Không xóa được vì nhân viên đã có tài khoản hoặc lịch làm.\n"
                    + "Hãy đổi trạng thái sang \"Đã nghỉ việc\".");
        }
    }

    private void timKiem() {
        loadTable(dao.timKiem(txtTuKhoa.getText().trim(),
                cboLocBoPhan.getSelectedItem().toString(),
                cboLocTrangThai.getSelectedItem().toString()));
    }

    private void lamMoi() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtNgayVaoLam.setText(LocalDate.now().toString());
        cboGioiTinh.setSelectedIndex(0);
        cboBoPhan.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        txtMaNV.setEditable(true);
        tblNhanVien.clearSelection();
        loadTable(dao.getAll());
    }
}

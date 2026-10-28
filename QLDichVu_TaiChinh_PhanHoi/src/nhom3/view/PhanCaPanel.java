package nhom3.view;

import nhom3.dao.CaLamDAO;
import nhom3.dao.NhanVienDAO;
import nhom3.dao.PhanCaDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nhom3.model.CaLam;
import nhom3.model.NhanVien;
import nhom3.model.PhanCa;
import nhom3.util.GiaoDien;
import nhom3.util.PhienDangNhap;

/**
 * MODULE 8 (phần 2): PHÂN LỊCH - XEM LỊCH TRỰC - KIỂM TRA CA TRÙNG - BÀN GIAO CA
 * Admin: phân ca, xóa phân ca, bàn giao mọi ca.
 * Người khác: chỉ xem lịch và bàn giao ca của chính mình.
 */
public class PhanCaPanel extends JPanel {

    private JComboBox<String> cboNhanVien;
    private JComboBox<CaLam> cboCa;
    private JTextField txtNgayLam;
    private JTextField txtTuNgay, txtDenNgay;
    private JComboBox<String> cboLocNV;
    private JTextArea txtNoiDungBanGiao;
    private JLabel lblCaDangChon;
    private JButton btnPhanCa, btnXoa, btnXemLich, btnHomNay, btnBanGiao, btnLamMoi;
    private JTable tblLich;
    private DefaultTableModel model;

    private final PhanCaDAO phanCaDAO = new PhanCaDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final CaLamDAO caLamDAO = new CaLamDAO();
    private List<PhanCa> danhSach;
    private final boolean laAdmin = PhienDangNhap.laAdmin();

    public PhanCaPanel() {
        initComponents();
        loadComboBox();
        xemHomNay();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GiaoDien.MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        // ----- Khung phân lịch (chỉ Admin thấy) -----
        JPanel khungPhanCa = GiaoDien.taoKhung("Phân lịch nhân viên");
        khungPhanCa.setLayout(new BorderLayout());
        JPanel luoi = GiaoDien.taoLuoiForm();
        cboNhanVien = new JComboBox<>();
        cboCa = new JComboBox<>();
        txtNgayLam = new JTextField(LocalDate.now().toString());
        luoi.add(GiaoDien.taoNhan("Nhân viên:"));
        luoi.add(cboNhanVien);
        luoi.add(GiaoDien.taoNhan("Ca làm:"));
        luoi.add(cboCa);
        luoi.add(GiaoDien.taoNhan("Ngày làm (yyyy-MM-dd):"));
        luoi.add(txtNgayLam);
        khungPhanCa.add(luoi);

        // ----- Khung xem lịch trực -----
        JPanel khungXemLich = GiaoDien.taoKhung("Xem lịch trực");
        khungXemLich.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtTuNgay = new JTextField(10);
        txtDenNgay = new JTextField(10);
        cboLocNV = new JComboBox<>();
        btnXemLich = GiaoDien.taoNut("Xem lịch", GiaoDien.XANH_DUONG);
        btnHomNay = GiaoDien.taoNut("Hôm nay", GiaoDien.XAM);
        khungXemLich.add(GiaoDien.taoNhan("Từ ngày:"));
        khungXemLich.add(txtTuNgay);
        khungXemLich.add(GiaoDien.taoNhan("  Đến ngày:"));
        khungXemLich.add(txtDenNgay);
        khungXemLich.add(GiaoDien.taoNhan("  Nhân viên:"));
        khungXemLich.add(cboLocNV);
        khungXemLich.add(btnXemLich);
        khungXemLich.add(btnHomNay);

        // ----- Khung bàn giao ca -----
        JPanel khungBanGiao = GiaoDien.taoKhung("Bàn giao ca");
        khungBanGiao.setLayout(new BorderLayout(10, 5));
        lblCaDangChon = GiaoDien.taoNhan("Chưa chọn ca nào trên bảng");
        txtNoiDungBanGiao = new JTextArea(3, 20);
        txtNoiDungBanGiao.setFont(GiaoDien.FONT_CHU);
        txtNoiDungBanGiao.setLineWrap(true);
        khungBanGiao.add(lblCaDangChon, BorderLayout.NORTH);
        khungBanGiao.add(new JScrollPane(txtNoiDungBanGiao), BorderLayout.CENTER);

        // ----- Bảng lịch -----
        JPanel khungBang = GiaoDien.taoKhung("Lịch trực");
        khungBang.setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Mã PC", "Ngày làm", "Mã NV", "Họ tên",
            "Ca", "Giờ làm", "Trạng thái", "Nội dung bàn giao"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLich = new JTable(model);
        GiaoDien.dinhDangBang(tblLich);
        khungBang.add(new JScrollPane(tblLich));

        // ----- Hàng nút -----
        JPanel hangNut = GiaoDien.taoHangNut();
        btnPhanCa = GiaoDien.taoNut("Phân ca", GiaoDien.XANH_LA);
        btnXoa = GiaoDien.taoNut("Xóa phân ca", GiaoDien.DO);
        btnBanGiao = GiaoDien.taoNut("Bàn giao ca", GiaoDien.XANH_NGOC);
        btnLamMoi = GiaoDien.taoNut("Làm mới", GiaoDien.XAM);
        if (laAdmin) {
            hangNut.add(btnPhanCa);
            hangNut.add(btnXoa);
        }
        hangNut.add(btnBanGiao);
        hangNut.add(btnLamMoi);

        // Ghép: bảng bên trái, khung bàn giao bên phải
        JPanel giua = new JPanel(new BorderLayout(10, 0));
        giua.setOpaque(false);
        khungBanGiao.setPreferredSize(new Dimension(320, 0));
        giua.add(khungBang, BorderLayout.CENTER);
        giua.add(khungBanGiao, BorderLayout.EAST);

        JPanel phanTren = new JPanel();
        phanTren.setLayout(new BoxLayout(phanTren, BoxLayout.Y_AXIS));
        phanTren.setOpaque(false);
        phanTren.add(GiaoDien.taoTieuDe("LỊCH TRỰC & BÀN GIAO CA", "Phân lịch, xem lịch trực và bàn giao ca làm việc"));
        if (laAdmin) {
            phanTren.add(khungPhanCa);
        }
        phanTren.add(khungXemLich);

        add(phanTren, BorderLayout.NORTH);
        add(giua, BorderLayout.CENTER);
        add(hangNut, BorderLayout.SOUTH);

        // ----- Sự kiện -----
        tblLich.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {   // chỉ chạy 1 lần khi đã thả chuột
                hienThiDongDangChon();
            }
        });
        btnPhanCa.addActionListener(e -> phanCa());
        btnXoa.addActionListener(e -> xoaPhanCa());
        btnXemLich.addActionListener(e -> xemLich());
        btnHomNay.addActionListener(e -> xemHomNay());
        btnBanGiao.addActionListener(e -> banGiaoCa());
        btnLamMoi.addActionListener(e -> {
            loadComboBox();
            xemLich();
        });
    }

    // Nạp nhân viên đang làm và danh sách ca vào combobox
    private void loadComboBox() {
        cboNhanVien.removeAllItems();
        cboLocNV.removeAllItems();
        cboLocNV.addItem("Tất cả");
        for (NhanVien nv : nhanVienDAO.getNhanVienDangLam()) {
            String item = nv.getMaNV() + " - " + nv.getHoTen();
            cboNhanVien.addItem(item);
            cboLocNV.addItem(item);
        }
        cboCa.removeAllItems();
        for (CaLam ca : caLamDAO.getAll()) {
            cboCa.addItem(ca);
        }
    }

    // "NV001 - Nguyễn Văn An" -> "NV001"
    private String layMaNV(String item) {
        return item.split(" - ")[0];
    }

    private void xemHomNay() {
        String homNay = LocalDate.now().toString();
        txtTuNgay.setText(homNay);
        txtDenNgay.setText(homNay);
        cboLocNV.setSelectedIndex(0);
        xemLich();
    }

    private void xemLich() {
        Date tuNgay, denNgay;
        try {
            tuNgay = Date.valueOf(LocalDate.parse(txtTuNgay.getText().trim()));
            denNgay = Date.valueOf(LocalDate.parse(txtDenNgay.getText().trim()));
        } catch (DateTimeParseException e) {   // sai định dạng hoặc ngày không có thật (VD 30/02)
            JOptionPane.showMessageDialog(this, "Ngày phải nhập dạng yyyy-MM-dd!");
            return;
        }
        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày!");
            return;
        }
        String chon = (String) cboLocNV.getSelectedItem();
        String maNV = (chon == null || chon.equals("Tất cả")) ? "" : layMaNV(chon);

        model.setRowCount(0);
        danhSach = phanCaDAO.getLichTruc(tuNgay, denNgay, maNV);
        for (PhanCa pc : danhSach) {
            model.addRow(new Object[]{pc.getMaPhanCa(), pc.getNgayLam(), pc.getMaNV(), pc.getTenNV(),
                pc.getTenCa(), pc.getGioLam(), pc.getTrangThai(), pc.getNoiDungBanGiao()});
        }
        lblCaDangChon.setText("Chưa chọn ca nào trên bảng");
        txtNoiDungBanGiao.setText("");
    }

    private void hienThiDongDangChon() {
        int dong = tblLich.getSelectedRow();
        if (dong < 0) {
            return;
        }
        PhanCa pc = danhSach.get(dong);
        lblCaDangChon.setText(pc.getTenNV() + " - " + pc.getTenCa() + " - " + pc.getNgayLam());
        txtNoiDungBanGiao.setText(pc.getNoiDungBanGiao() == null ? "" : pc.getNoiDungBanGiao());
    }

    private void phanCa() {
        if (cboNhanVien.getSelectedItem() == null || cboCa.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Chưa có nhân viên hoặc ca làm để phân!");
            return;
        }
        String maNV = layMaNV(cboNhanVien.getSelectedItem().toString());
        CaLam ca = (CaLam) cboCa.getSelectedItem();
        Date ngay;
        try {
            ngay = Date.valueOf(LocalDate.parse(txtNgayLam.getText().trim()));
        } catch (DateTimeParseException e) {   // sai định dạng hoặc ngày không có thật (VD 30/02)
            JOptionPane.showMessageDialog(this, "Ngày làm phải nhập dạng yyyy-MM-dd!");
            return;
        }

        // KIỂM TRA CA TRÙNG trước khi lưu
        CaLam caTrung = phanCaDAO.kiemTraTrungCa(maNV, ca, ngay);
        if (caTrung != null) {
            JOptionPane.showMessageDialog(this, "Trùng ca! Nhân viên " + maNV + " ngày " + ngay
                    + " đã có " + caTrung.getTenCa() + " (" + caTrung.getGioBatDau() + " - "
                    + caTrung.getGioKetThuc() + ")");
            return;
        }

        if (phanCaDAO.them(new PhanCa(maNV, ca.getMaCa(), ngay))) {
            JOptionPane.showMessageDialog(this, "Phân ca thành công!");
            // Hiện lịch của ngày vừa phân
            txtTuNgay.setText(ngay.toString());
            txtDenNgay.setText(ngay.toString());
            xemLich();
        } else {
            JOptionPane.showMessageDialog(this, "Phân ca thất bại!");
        }
    }

    private void xoaPhanCa() {
        int dong = tblLich.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn ca cần xóa trên bảng!");
            return;
        }
        PhanCa pc = danhSach.get(dong);
        if (pc.getTrangThai().equals("Đã bàn giao")) {
            JOptionPane.showMessageDialog(this, "Ca đã bàn giao, không được xóa!");
            return;
        }
        int chon = JOptionPane.showConfirmDialog(this, "Xóa lịch của " + pc.getTenNV() + " ngày " + pc.getNgayLam() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (chon == JOptionPane.YES_OPTION && phanCaDAO.xoa(pc.getMaPhanCa())) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            xemLich();
        }
    }

    private void banGiaoCa() {
        int dong = tblLich.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn ca cần bàn giao trên bảng!");
            return;
        }
        PhanCa pc = danhSach.get(dong);
        String noiDung = txtNoiDungBanGiao.getText().trim();

        // Chỉ người làm ca đó hoặc Admin mới được bàn giao
        String maNVDangNhap = PhienDangNhap.getTaiKhoan().getMaNV();
        if (!laAdmin && !pc.getMaNV().equals(maNVDangNhap)) {
            JOptionPane.showMessageDialog(this, "Bạn chỉ được bàn giao ca của chính mình!");
            return;
        }
        if (pc.getTrangThai().equals("Đã bàn giao")) {
            JOptionPane.showMessageDialog(this, "Ca này đã được bàn giao rồi!");
            return;
        }
        // Ca của ngày mai trở đi thì chưa bàn giao được
        if (pc.getNgayLam().toLocalDate().isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ca này chưa diễn ra, chưa thể bàn giao!");
            return;
        }
        if (noiDung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập nội dung bàn giao (tiền quỹ, phòng, việc còn tồn...)!");
            return;
        }

        if (phanCaDAO.banGiao(pc.getMaPhanCa(), noiDung)) {
            JOptionPane.showMessageDialog(this, "Bàn giao ca thành công!");
            xemLich();
        } else {
            JOptionPane.showMessageDialog(this, "Bàn giao thất bại!");
        }
    }
}

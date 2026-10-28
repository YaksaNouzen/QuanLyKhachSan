package view;

import dao.CaLamDAO;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.CaLam;
import util.GiaoDien;

/**
 * MODULE 8 (phần 1): TẠO CA LÀM
 * Khai báo các ca: Ca sáng, Ca chiều, Ca đêm...
 */
public class CaLamPanel extends JPanel {

    private JTextField txtMaCa, txtTenCa, txtGioBatDau, txtGioKetThuc;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private JTable tblCaLam;
    private DefaultTableModel model;

    private final CaLamDAO dao = new CaLamDAO();
    private List<CaLam> danhSach;

    public CaLamPanel() {
        initComponents();
        loadTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GiaoDien.MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        JPanel khungThongTin = GiaoDien.taoKhung("Thông tin ca làm");
        khungThongTin.setLayout(new BorderLayout());
        JPanel luoi = GiaoDien.taoLuoiForm();
        txtMaCa = new JTextField();
        txtTenCa = new JTextField();
        txtGioBatDau = new JTextField();
        txtGioKetThuc = new JTextField();
        luoi.add(GiaoDien.taoNhan("Mã ca:"));
        luoi.add(txtMaCa);
        luoi.add(GiaoDien.taoNhan("Tên ca:"));
        luoi.add(txtTenCa);
        luoi.add(GiaoDien.taoNhan("Giờ bắt đầu (HH:mm):"));
        luoi.add(txtGioBatDau);
        luoi.add(GiaoDien.taoNhan("Giờ kết thúc (HH:mm):"));
        luoi.add(txtGioKetThuc);
        khungThongTin.add(luoi);

        JPanel khungBang = GiaoDien.taoKhung("Danh sách ca làm");
        khungBang.setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Mã ca", "Tên ca", "Giờ bắt đầu",
            "Giờ kết thúc", "Số giờ", "Loại ca"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCaLam = new JTable(model);
        GiaoDien.dinhDangBang(tblCaLam);
        khungBang.add(new JScrollPane(tblCaLam));

        JPanel hangNut = GiaoDien.taoHangNut();
        btnThem = GiaoDien.taoNut("Thêm ca", GiaoDien.XANH_LA);
        btnSua = GiaoDien.taoNut("Sửa ca", GiaoDien.CAM);
        btnXoa = GiaoDien.taoNut("Xóa ca", GiaoDien.DO);
        btnLamMoi = GiaoDien.taoNut("Làm mới", GiaoDien.XAM);
        hangNut.add(btnThem);
        hangNut.add(btnSua);
        hangNut.add(btnXoa);
        hangNut.add(btnLamMoi);

        JPanel phanTren = new JPanel();
        phanTren.setLayout(new BoxLayout(phanTren, BoxLayout.Y_AXIS));
        phanTren.setOpaque(false);
        phanTren.add(GiaoDien.taoTieuDe("QUẢN LÝ CA LÀM", "Tạo và quản lý các ca làm việc trong khách sạn"));
        phanTren.add(khungThongTin);
        add(phanTren, BorderLayout.NORTH);
        add(khungBang, BorderLayout.CENTER);
        add(hangNut, BorderLayout.SOUTH);

        tblCaLam.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {   // chỉ chạy 1 lần khi đã thả chuột
                hienThiDongDangChon();
            }
        });
        btnThem.addActionListener(e -> themCa());
        btnSua.addActionListener(e -> suaCa());
        btnXoa.addActionListener(e -> xoaCa());
        btnLamMoi.addActionListener(e -> lamMoi());
    }

    private void loadTable() {
        model.setRowCount(0);
        danhSach = dao.getAll();
        for (CaLam ca : danhSach) {
            double soGio = (ca.getPhutKetThuc() - ca.getPhutBatDau()) / 60.0;
            String loai = ca.getPhutKetThuc() > 24 * 60 ? "Qua đêm" : "Trong ngày";
            model.addRow(new Object[]{ca.getMaCa(), ca.getTenCa(), ca.getGioBatDau(),
                ca.getGioKetThuc(), soGio, loai});
        }
    }

    private void hienThiDongDangChon() {
        int dong = tblCaLam.getSelectedRow();
        if (dong < 0) {
            return;
        }
        CaLam ca = danhSach.get(dong);
        txtMaCa.setText(ca.getMaCa());
        txtTenCa.setText(ca.getTenCa());
        txtGioBatDau.setText(ca.getGioBatDau());
        txtGioKetThuc.setText(ca.getGioKetThuc());
        txtMaCa.setEditable(false);
    }

    private CaLam layDuLieuForm() {
        String ma = txtMaCa.getText().trim().toUpperCase();
        String ten = txtTenCa.getText().trim();
        String gioBD = txtGioBatDau.getText().trim();
        String gioKT = txtGioKetThuc.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã ca và tên ca không được để trống!");
            return null;
        }
        // Giờ hợp lệ: 00:00 -> 23:59
        String mauGio = "([01]\\d|2[0-3]):[0-5]\\d";
        if (!gioBD.matches(mauGio) || !gioKT.matches(mauGio)) {
            JOptionPane.showMessageDialog(this, "Giờ phải nhập dạng HH:mm (VD: 06:00, 22:30)!");
            return null;
        }
        if (gioBD.equals(gioKT)) {
            JOptionPane.showMessageDialog(this, "Giờ bắt đầu và kết thúc không được trùng nhau!");
            return null;
        }
        return new CaLam(ma, ten, gioBD, gioKT);
    }

    private void themCa() {
        CaLam ca = layDuLieuForm();
        if (ca == null) {
            return;
        }
        if (dao.getById(ca.getMaCa()) != null) {
            JOptionPane.showMessageDialog(this, "Mã ca đã tồn tại!");
            return;
        }
        if (dao.them(ca)) {
            JOptionPane.showMessageDialog(this, "Thêm ca thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    private void suaCa() {
        if (tblCaLam.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn ca cần sửa!");
            return;
        }
        CaLam ca = layDuLieuForm();
        if (ca == null) {
            return;
        }
        if (dao.sua(ca)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void xoaCa() {
        int dong = tblCaLam.getSelectedRow();
        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn ca cần xóa!");
            return;
        }
        String maCa = danhSach.get(dong).getMaCa();
        int chon = JOptionPane.showConfirmDialog(this, "Xóa ca " + maCa + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (chon != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.xoa(maCa)) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Không xóa được vì ca này đã được phân cho nhân viên!");
        }
    }

    private void lamMoi() {
        txtMaCa.setText("");
        txtTenCa.setText("");
        txtGioBatDau.setText("");
        txtGioKetThuc.setText("");
        txtMaCa.setEditable(true);
        tblCaLam.clearSelection();
        loadTable();
    }
}

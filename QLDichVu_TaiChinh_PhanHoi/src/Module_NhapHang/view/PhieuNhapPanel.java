package Module_NhapHang.view;

import Module_NhaCungCap.controller.NhaCungCapController;
import Module_NhaCungCap.model.NhaCungCap;
import Module_NhapHang.controller.PhieuNhapController;
import Module_NhapHang.model.ChiTietPhieuNhap;
import Module_NhapHang.model.PhieuNhap;
import Module_KhoVatTu.controller.VatTuController;
import Module_KhoVatTu.model.VatTu;
import common.kho.ui.GiaoDien;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PhieuNhapPanel extends JPanel {
    private static final DateTimeFormatter NGAY_GIO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final NumberFormat tien = NumberFormat.getNumberInstance(
            Locale.forLanguageTag("vi-VN"));
    private final PhieuNhapController controller = new PhieuNhapController();
    private final NhaCungCapController nccController = new NhaCungCapController();
    private final VatTuController vatTuController = new VatTuController();

    private final JTextField txtTim = new JTextField(14);
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>(
            new String[]{"Tất cả", "Nháp", "Đã xác nhận", "Đã hủy"});
    private final JTextField txtTuNgay = new JTextField(9);
    private final JTextField txtDenNgay = new JTextField(9);
    private final DefaultTableModel modelDanhSach = new DefaultTableModel(
            new String[]{"Mã phiếu", "Nhà cung cấp", "Ngày nhập",
                "Tổng tiền", "Trạng thái", "Người lập"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tblDanhSach = new JTable(modelDanhSach);

    private final JTextField txtMa = new JTextField();
    private final JComboBox<NhaCungCap> cboNhaCungCap = new JComboBox<>();
    private final JTextField txtNgayNhap = new JTextField();
    private final JTextField txtNguoiLap = new JTextField();
    private final JTextField txtGhiChu = new JTextField();
    private final JLabel lblTrangThai = new JLabel("Nháp");
    private final JComboBox<VatTu> cboVatTu = new JComboBox<>();
    private final JTextField txtSoLuong = new JTextField(6);
    private final JTextField txtDonGia = new JTextField(10);
    private final JLabel lblTongTien = new JLabel("0 VND");
    private final DefaultTableModel modelChiTiet = new DefaultTableModel(
            new String[]{"Mã vật tư", "Tên vật tư", "Số lượng",
                "Đơn giá", "Thành tiền"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tblChiTiet = new JTable(modelChiTiet);

    public PhieuNhapPanel() {
        taoGiaoDien();
        lamMoiPhieu();
    }

    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(GiaoDien.NEN);
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = GiaoDien.tieuDe("QUẢN LÝ NHẬP HÀNG");
        title.setAlignmentX(CENTER_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(12));
        header.add(taoBoLoc());
        add(header, BorderLayout.NORTH);

        tblDanhSach.setRowHeight(26);
        tblDanhSach.setAutoCreateRowSorter(true);
        tblDanhSach.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDanhSach.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) moPhieuDangChon();
        });
        JPanel dsPanel = GiaoDien.panelTrangTri();
        dsPanel.setLayout(new BorderLayout(5, 5));
        dsPanel.add(new JLabel("Danh sách phiếu nhập"), BorderLayout.NORTH);
        dsPanel.add(new JScrollPane(tblDanhSach), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                dsPanel, taoKhuVucChiTiet());
        split.setResizeWeight(0.38);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JPanel taoBoLoc() {
        JPanel panel = GiaoDien.panelTrangTri();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 7, 2));
        JButton tim = new JButton("Tìm kiếm");
        panel.add(new JLabel("Từ khóa:")); panel.add(txtTim);
        panel.add(new JLabel("Trạng thái:")); panel.add(cboLocTrangThai);
        panel.add(new JLabel("Từ ngày:")); panel.add(txtTuNgay);
        panel.add(new JLabel("Đến ngày:")); panel.add(txtDenNgay); panel.add(tim);
        tim.addActionListener(e -> taiDanhSach());
        txtTim.addActionListener(e -> taiDanhSach());
        cboLocTrangThai.addActionListener(e -> taiDanhSach());
        return panel;
    }

    private JPanel taoKhuVucChiTiet() {
        JPanel wrapper = GiaoDien.panelTrangTri();
        wrapper.setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.add(new JLabel("Mã phiếu:"), GiaoDien.rangBuoc(0, 0));
        form.add(txtMa, GiaoDien.rangBuoc(1, 0));
        form.add(new JLabel("Nhà cung cấp:"), GiaoDien.rangBuoc(2, 0));
        form.add(cboNhaCungCap, GiaoDien.rangBuoc(3, 0));
        form.add(new JLabel("Ngày nhập:"), GiaoDien.rangBuoc(0, 1));
        form.add(txtNgayNhap, GiaoDien.rangBuoc(1, 1));
        form.add(new JLabel("Người lập:"), GiaoDien.rangBuoc(2, 1));
        form.add(txtNguoiLap, GiaoDien.rangBuoc(3, 1));
        form.add(new JLabel("Ghi chú:"), GiaoDien.rangBuoc(0, 2));
        form.add(txtGhiChu, GiaoDien.rangBuoc(1, 2));
        form.add(new JLabel("Trạng thái:"), GiaoDien.rangBuoc(2, 2));
        form.add(lblTrangThai, GiaoDien.rangBuoc(3, 2));
        wrapper.add(form, BorderLayout.NORTH);

        JPanel chiTiet = new JPanel(new BorderLayout(5, 5));
        chiTiet.setBackground(Color.WHITE);
        JPanel themDong = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 2));
        themDong.setBackground(Color.WHITE);
        JButton them = new JButton("Thêm dòng");
        JButton xoa = new JButton("Xóa dòng");
        themDong.add(new JLabel("Vật tư:")); themDong.add(cboVatTu);
        themDong.add(new JLabel("Số lượng:")); themDong.add(txtSoLuong);
        themDong.add(new JLabel("Đơn giá:")); themDong.add(txtDonGia);
        themDong.add(them); themDong.add(xoa);
        chiTiet.add(themDong, BorderLayout.NORTH);
        tblChiTiet.setRowHeight(25);
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chiTiet.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);
        wrapper.add(chiTiet, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        buttons.setBackground(Color.WHITE);
        JButton moi = new JButton("Phiếu mới");
        JButton luu = new JButton("Lưu nháp");
        JButton xacNhan = new JButton("Xác nhận nhập kho");
        JButton huy = new JButton("Hủy phiếu");
        buttons.add(new JLabel("Tổng tiền:")); buttons.add(lblTongTien);
        buttons.add(moi); buttons.add(luu); buttons.add(xacNhan); buttons.add(huy);
        wrapper.add(buttons, BorderLayout.SOUTH);

        them.addActionListener(e -> themDong());
        xoa.addActionListener(e -> xoaDong());
        moi.addActionListener(e -> lamMoiPhieu());
        luu.addActionListener(e -> luuPhieu());
        xacNhan.addActionListener(e -> xacNhanPhieu());
        huy.addActionListener(e -> huyPhieu());
        return wrapper;
    }

    public void taiDuLieu() {
        taiDanhMuc();
        taiDanhSach();
    }

    private void taiDanhMuc() {
        try {
            String maNccCu = cboNhaCungCap.getSelectedItem() instanceof NhaCungCap n
                    ? n.getMaNhaCungCap() : null;
            cboNhaCungCap.removeAllItems();
            for (NhaCungCap ncc : nccController.layDangHoatDong()) {
                cboNhaCungCap.addItem(ncc);
                if (ncc.getMaNhaCungCap().equals(maNccCu)) cboNhaCungCap.setSelectedItem(ncc);
            }
            cboVatTu.removeAllItems();
            for (VatTu vt : vatTuController.layDangHoatDong()) cboVatTu.addItem(vt);
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void taiDanhSach() {
        try {
            LocalDate from = docNgayLoc(txtTuNgay.getText());
            LocalDate to = docNgayLoc(txtDenNgay.getText());
            List<PhieuNhap> ds = controller.timKiem(txtTim.getText(),
                    (String) cboLocTrangThai.getSelectedItem(), from, to);
            modelDanhSach.setRowCount(0);
            for (PhieuNhap p : ds) {
                modelDanhSach.addRow(new Object[]{p.getMaPhieuNhap(),
                    p.getMaNhaCungCap() + " - " + p.getTenNhaCungCap(),
                    p.getNgayNhap().format(NGAY_GIO), tien.format(p.getTongTien()),
                    p.getTrangThai(), p.getNguoiLap()});
            }
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private LocalDate docNgayLoc(String text) {
        if (text == null || text.isBlank()) return null;
        try { return LocalDate.parse(text.trim()); }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày lọc phải có dạng yyyy-MM-dd.");
        }
    }

    private void themDong() {
        try {
            if (!(cboVatTu.getSelectedItem() instanceof VatTu vt)) {
                throw new IllegalArgumentException("Chưa có vật tư đang hoạt động.");
            }
            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                if (vt.getMaVatTu().equals(modelChiTiet.getValueAt(i, 0))) {
                    throw new IllegalArgumentException("Vật tư đã có trong phiếu.");
                }
            }
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            BigDecimal donGia = new BigDecimal(txtDonGia.getText().trim());
            if (soLuong <= 0 || donGia.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0 và đơn giá không được âm.");
            }
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
            modelChiTiet.addRow(new Object[]{vt.getMaVatTu(), vt.getTenVatTu(),
                soLuong, donGia, thanhTien});
            txtSoLuong.setText(""); txtDonGia.setText(""); capNhatTongTien();
        } catch (NumberFormatException e) {
            GiaoDien.baoLoi(this, new IllegalArgumentException("Số lượng hoặc đơn giá không hợp lệ."));
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void xoaDong() {
        int row = tblChiTiet.getSelectedRow();
        if (row >= 0) {
            modelChiTiet.removeRow(tblChiTiet.convertRowIndexToModel(row));
            capNhatTongTien();
        }
    }

    private PhieuNhap docPhieuTuForm() {
        PhieuNhap phieu = new PhieuNhap();
        phieu.setMaPhieuNhap(txtMa.getText().trim());
        if (cboNhaCungCap.getSelectedItem() instanceof NhaCungCap ncc) {
            phieu.setMaNhaCungCap(ncc.getMaNhaCungCap());
        }
        try { phieu.setNgayNhap(LocalDateTime.parse(txtNgayNhap.getText().trim(), NGAY_GIO)); }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày nhập phải có dạng yyyy-MM-dd HH:mm.");
        }
        phieu.setNguoiLap(txtNguoiLap.getText().trim());
        phieu.setGhiChu(txtGhiChu.getText().trim());
        for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
            phieu.getChiTiet().add(new ChiTietPhieuNhap(phieu.getMaPhieuNhap(),
                    modelChiTiet.getValueAt(i, 0).toString(),
                    modelChiTiet.getValueAt(i, 1).toString(),
                    ((Number) modelChiTiet.getValueAt(i, 2)).intValue(),
                    (BigDecimal) modelChiTiet.getValueAt(i, 3)));
        }
        return phieu;
    }

    private void luuPhieu() {
        try {
            controller.luu(docPhieuTuForm());
            JOptionPane.showMessageDialog(this, "Đã lưu phiếu nhập ở trạng thái Nháp.");
            txtMa.setEditable(false); taiDanhSach();
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void xacNhanPhieu() {
        if (JOptionPane.showConfirmDialog(this,
                "Xác nhận phiếu sẽ cộng tồn kho và không thể sửa lại. Tiếp tục?",
                "Xác nhận nhập kho", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            controller.xacNhan(txtMa.getText().trim());
            JOptionPane.showMessageDialog(this, "Đã xác nhận và cộng tồn kho.");
            taiDanhSach(); moPhieu(txtMa.getText().trim());
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void huyPhieu() {
        try {
            controller.huy(txtMa.getText().trim());
            JOptionPane.showMessageDialog(this, "Đã hủy phiếu nhập.");
            taiDanhSach(); moPhieu(txtMa.getText().trim());
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void moPhieuDangChon() {
        int row = tblDanhSach.getSelectedRow();
        if (row < 0) return;
        row = tblDanhSach.convertRowIndexToModel(row);
        moPhieu(modelDanhSach.getValueAt(row, 0).toString());
    }

    private void moPhieu(String ma) {
        try {
            PhieuNhap p = controller.timTheoMa(ma);
            if (p == null) return;
            txtMa.setText(p.getMaPhieuNhap()); txtMa.setEditable(false);
            chonNhaCungCap(p.getMaNhaCungCap());
            txtNgayNhap.setText(p.getNgayNhap().format(NGAY_GIO));
            txtNguoiLap.setText(p.getNguoiLap()); txtGhiChu.setText(p.getGhiChu());
            lblTrangThai.setText(p.getTrangThai()); modelChiTiet.setRowCount(0);
            for (ChiTietPhieuNhap d : p.getChiTiet()) {
                modelChiTiet.addRow(new Object[]{d.getMaVatTu(), d.getTenVatTu(),
                    d.getSoLuong(), d.getDonGia(), d.getThanhTien()});
            }
            capNhatTongTien();
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void chonNhaCungCap(String ma) {
        for (int i = 0; i < cboNhaCungCap.getItemCount(); i++) {
            if (ma.equals(cboNhaCungCap.getItemAt(i).getMaNhaCungCap())) {
                cboNhaCungCap.setSelectedIndex(i); return;
            }
        }
    }

    private void capNhatTongTien() {
        BigDecimal tong = BigDecimal.ZERO;
        for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
            tong = tong.add((BigDecimal) modelChiTiet.getValueAt(i, 4));
        }
        lblTongTien.setText(tien.format(tong) + " VND");
    }

    private void lamMoiPhieu() {
        txtMa.setEditable(true); txtMa.setText("");
        txtNgayNhap.setText(LocalDateTime.now().format(NGAY_GIO));
        txtNguoiLap.setText(""); txtGhiChu.setText("");
        lblTrangThai.setText("Nháp"); modelChiTiet.setRowCount(0);
        lblTongTien.setText("0 VND"); tblDanhSach.clearSelection();
    }
}


package Module_TaiChinh.view;

import Module_TaiChinh.model.HoaDon;
import Module_TaiChinh.controller.HoaDonController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class HoaDonPanel extends JPanel {

    private final HoaDonController hoaDonController;
    private final Consumer<String> moChiTietHoaDon;
    private final Consumer<String> moThanhToanHoaDon;

    // Bang du lieu
    private JTable tblHoaDon;
    private DefaultTableModel tableModel;

    // Cac o nhap thong tin hoa don
    private JTextField txtMaHoaDon;
    private JTextField txtMaDatPhong;
    private JTextField txtNgayLap;
    private JTextField txtTienGiam;
    private JTextField txtTongTien;
    private JComboBox<String> cboTrangThai;

    // Cac o tim kiem
    private JTextField txtTimMaHoaDon;
    private JTextField txtTimMaDatPhong;
    private JTextField txtTimMaKhachHang;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;

    private final DecimalFormat dinhDangTien =
            new DecimalFormat("#,##0.##");

    public HoaDonPanel() {
        this(null, null);
    }

    public HoaDonPanel(Consumer<String> moChiTietHoaDon) {
        this(moChiTietHoaDon, null);
    }

    public HoaDonPanel(
            Consumer<String> moChiTietHoaDon,
            Consumer<String> moThanhToanHoaDon) {
        hoaDonController = new HoaDonController();
        this.moChiTietHoaDon = moChiTietHoaDon;
        this.moThanhToanHoaDon = moThanhToanHoaDon;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        taoGiaoDien();
        taiDanhSachHoaDon();
    }

    // =====================================================
    // 1. TAO GIAO DIEN
    // =====================================================
    private void taoGiaoDien() {

        // Tieu de
        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setOpaque(false);

        JLabel lblTieuDe = new JLabel("QUẢN LÝ HÓA ĐƠN");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        lblTieuDe.setForeground(new Color(23, 54, 93));

        JLabel lblMoTa = new JLabel(
                "Quản lý thông tin và tra cứu hóa đơn khách sạn");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);

        JPanel pnlTextTieuDe = new JPanel();
        pnlTextTieuDe.setOpaque(false);
        pnlTextTieuDe.setLayout(
                new BoxLayout(pnlTextTieuDe, BoxLayout.Y_AXIS));

        pnlTextTieuDe.add(lblTieuDe);
        pnlTextTieuDe.add(Box.createVerticalStrut(5));
        pnlTextTieuDe.add(lblMoTa);

        pnlTieuDe.add(pnlTextTieuDe, BorderLayout.WEST);

        // Khu vuc nhap lieu va tim kiem
        JPanel pnlTren = new JPanel();
        pnlTren.setOpaque(false);
        pnlTren.setLayout(new BoxLayout(pnlTren, BoxLayout.Y_AXIS));

        pnlTren.add(pnlTieuDe);
        pnlTren.add(Box.createVerticalStrut(15));
        pnlTren.add(taoPanelNhapLieu());
        pnlTren.add(Box.createVerticalStrut(12));
        pnlTren.add(taoPanelTimKiem());

        add(pnlTren, BorderLayout.NORTH);
        add(taoPanelBang(), BorderLayout.CENTER);
        add(taoPanelNutChucNang(), BorderLayout.SOUTH);
    }

    // =====================================================
    // 2. PANEL NHAP LIEU
    // =====================================================
    private JPanel taoPanelNhapLieu() {

        JPanel panel = taoPanelTrangTri("Thông tin hóa đơn");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        txtMaHoaDon = new JTextField(15);
        txtMaDatPhong = new JTextField(15);
        txtNgayLap = new JTextField(15);
        txtTienGiam = new JTextField(15);
        txtTongTien = new JTextField(15);

        // Tong tien duoc tinh tu cac dong chi tiet
        txtTongTien.setEditable(false);
        txtTongTien.setBackground(new Color(240, 240, 240));

        cboTrangThai = new JComboBox<>(new String[]{
            "Chưa thanh toán",
            "Đã thanh toán"
        });

        int hang = 0;

        themTruong(form, "Mã hóa đơn:", txtMaHoaDon,
                0, hang);
        themTruong(form, "Mã đặt phòng:", txtMaDatPhong,
                2, hang);

        hang++;

        themTruong(form, "Ngày lập (yyyy-MM-dd):",
                txtNgayLap, 0, hang);
        themTruong(form, "Tiền giảm (VNĐ):",
                txtTienGiam, 2, hang);

        hang++;

        themTruong(form, "Tổng tiền (VNĐ):",
                txtTongTien, 0, hang);
        themTruong(form, "Trạng thái:",
                cboTrangThai, 2, hang);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // 3. PANEL TIM KIEM
    // =====================================================
    private JPanel taoPanelTimKiem() {

        JPanel panel = taoPanelTrangTri("Tìm kiếm hóa đơn");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        txtTimMaHoaDon = new JTextField(12);
        txtTimMaDatPhong = new JTextField(12);
        txtTimMaKhachHang = new JTextField(12);
        txtTuNgay = new JTextField(12);
        txtDenNgay = new JTextField(12);

        themTruong(form, "Mã hóa đơn:", txtTimMaHoaDon,
                0, 0);
        themTruong(form, "Mã đặt phòng:", txtTimMaDatPhong,
                2, 0);
        themTruong(form, "Mã khách hàng:", txtTimMaKhachHang,
                4, 0);

        themTruong(form, "Từ ngày:", txtTuNgay,
                0, 1);
        themTruong(form, "Đến ngày:", txtDenNgay,
                2, 1);

        JButton btnTimKiem = taoNut(
                "Tìm kiếm", new Color(41, 98, 160));

        JButton btnBoLoc = taoNut(
                "Bỏ lọc", new Color(108, 117, 125));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel pnlNutTimKiem = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlNutTimKiem.setBackground(Color.WHITE);
        pnlNutTimKiem.add(btnTimKiem);
        pnlNutTimKiem.add(btnBoLoc);

        form.add(pnlNutTimKiem, gbc);

        btnTimKiem.addActionListener(e -> timKiemHoaDon());
        btnBoLoc.addActionListener(e -> {
            xoaBoLoc();
            taiDanhSachHoaDon();
        });

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // 4. PANEL BANG HOA DON
    // =====================================================
    private JPanel taoPanelBang() {

        JPanel panel = taoPanelTrangTri("Danh sách hóa đơn");

        String[] cot = {
            "Mã hóa đơn",
            "Mã đặt phòng",
            "Ngày lập",
            "Tiền giảm (VNĐ)",
            "Tổng tiền (VNĐ)",
            "Trạng thái"
        };

        tableModel = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(tableModel);
        tblHoaDon.setRowHeight(29);
        tblHoaDon.setFont(new Font("Arial", Font.PLAIN, 13));
        tblHoaDon.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        tblHoaDon.setAutoCreateRowSorter(true);

        tblHoaDon.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));
        tblHoaDon.getTableHeader().setBackground(
                new Color(225, 233, 243));
        tblHoaDon.getTableHeader().setForeground(
                new Color(23, 54, 93));
        tblHoaDon.getTableHeader().setReorderingAllowed(false);

        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblHoaDon.getColumnModel().getColumn(2).setPreferredWidth(105);
        tblHoaDon.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblHoaDon.getColumnModel().getColumn(4).setPreferredWidth(145);
        tblHoaDon.getColumnModel().getColumn(5).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setPreferredSize(new Dimension(700, 230));

        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiHoaDonDuocChon();
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // 5. CAC NUT CHUC NANG
    // =====================================================
    private JPanel taoPanelNutChucNang() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JButton btnThem = taoNut(
                "Thêm hóa đơn", new Color(39, 145, 87));

        JButton btnSua = taoNut(
                "Sửa hóa đơn", new Color(230, 151, 37));

        JButton btnXoa = taoNut(
                "Xóa hóa đơn", new Color(192, 57, 43));

        JButton btnTinhTien = taoNut(
                "Tính lại tổng tiền", new Color(41, 98, 160));

        JButton btnXemChiTiet = taoNut(
                "Xem chi tiết", new Color(92, 73, 145));

        JButton btnThanhToan = taoNut(
                "Thanh toán", new Color(32, 130, 120));

        JButton btnLamMoi = taoNut(
                "Làm mới", new Color(108, 117, 125));

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnTinhTien);
        panel.add(btnXemChiTiet);
        panel.add(btnThanhToan);
        panel.add(btnLamMoi);

        btnThem.addActionListener(e -> themHoaDon());
        btnSua.addActionListener(e -> suaHoaDon());
        btnXoa.addActionListener(e -> xoaHoaDon());
        btnTinhTien.addActionListener(e -> tinhLaiTongTien());
        btnXemChiTiet.addActionListener(e -> xemChiTietHoaDon());
        btnThanhToan.addActionListener(e -> thanhToanHoaDon());
        btnLamMoi.addActionListener(e -> {
            xoaForm();
            taiDanhSachHoaDon();
        });

        return panel;
    }

    // =====================================================
    // 6. TAI DANH SACH HOA DON
    // =====================================================
    private void taiDanhSachHoaDon() {

        try {
            List<HoaDon> danhSach = hoaDonController.getAll();

            tableModel.setRowCount(0);

            for (HoaDon hd : danhSach) {
                themHoaDonVaoBang(hd);
            }

        } catch (Exception e) {
            hienThiLoi("Không thể tải danh sách hóa đơn.", e);
        }
    }

    /**
     * Cho MainFrame yeu cau tai lai du lieu khi quay ve man hinh hoa don.
     */
    public void lamMoiDanhSach() {
        taiDanhSachHoaDon();
    }

    private void themHoaDonVaoBang(HoaDon hd) {

        tableModel.addRow(new Object[]{
            hd.getMaHoaDon(),
            hd.getMaDatPhong(),
            hd.getNgayLap(),
            dinhDangTien.format(hd.getTienGiam()),
            dinhDangTien.format(hd.getTongTien()),
            hd.getTrangThai()
        });
    }

    // =====================================================
    // 7. HIEN THI HOA DON KHI CHON DONG
    // =====================================================
    private void hienThiHoaDonDuocChon() {

        int selectedRow = tblHoaDon.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int row = tblHoaDon.convertRowIndexToModel(selectedRow);

        txtMaHoaDon.setText(
                tableModel.getValueAt(row, 0).toString());
        txtMaDatPhong.setText(
                tableModel.getValueAt(row, 1).toString());
        txtNgayLap.setText(
                tableModel.getValueAt(row, 2).toString());

        txtTienGiam.setText(
                tableModel.getValueAt(row, 3).toString()
                        .replace(",", ""));

        txtTongTien.setText(
                tableModel.getValueAt(row, 4).toString()
                        .replace(",", ""));

        cboTrangThai.setSelectedItem(
                tableModel.getValueAt(row, 5).toString());

        // Ma hoa don la khoa, khong cho sua truc tiep
        txtMaHoaDon.setEditable(false);
    }

    // =====================================================
    // 8. THEM HOA DON
    // =====================================================
    private void themHoaDon() {

        try {
            HoaDon hd = docHoaDonTuForm(true);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn thêm hóa đơn "
                            + hd.getMaHoaDon() + "?",
                    "Xác nhận thêm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean thanhCong = hoaDonController.themHoaDon(hd);

            if (thanhCong) {
                JOptionPane.showMessageDialog(
                        this,
                        "Thêm hóa đơn thành công.\n"
                        + "Hãy thêm chi tiết hóa đơn rồi tính lại tổng tiền."
                );

                xoaForm();
                taiDanhSachHoaDon();
            } else {
                thongBaoThatBai("Thêm hóa đơn");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Không thể thêm hóa đơn",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =====================================================
    // 9. SUA HOA DON
    // =====================================================
    private void suaHoaDon() {

        if (tblHoaDon.getSelectedRow() < 0) {
            thongBaoChuaChon();
            return;
        }

        try {
            HoaDon hd = docHoaDonTuForm(false);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn cập nhật hóa đơn "
                            + hd.getMaHoaDon() + "?",
                    "Xác nhận sửa",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean thanhCong = hoaDonController.suaHoaDon(hd);

            if (thanhCong) {
                JOptionPane.showMessageDialog(
                        this, "Cập nhật hóa đơn thành công.");
                taiDanhSachHoaDon();
                chonHoaDon(hd.getMaHoaDon());
            } else {
                thongBaoThatBai("Cập nhật hóa đơn");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Không thể sửa hóa đơn",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =====================================================
    // 10. XOA HOA DON
    // =====================================================
    private void xoaHoaDon() {

        if (tblHoaDon.getSelectedRow() < 0) {
            thongBaoChuaChon();
            return;
        }

        String maHoaDon = txtMaHoaDon.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "CẢNH BÁO: Xóa hóa đơn " + maHoaDon
                + " sẽ xóa cả chi tiết và lịch sử thanh toán "
                + "theo cách DAO hiện tại.\n"
                + "Bạn có chắc chắn muốn tiếp tục?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean thanhCong = hoaDonController.xoaHoaDon(maHoaDon);

            if (thanhCong) {
                JOptionPane.showMessageDialog(
                        this, "Xóa hóa đơn thành công.");
                xoaForm();
                taiDanhSachHoaDon();
            } else {
                thongBaoThatBai("Xóa hóa đơn");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Không thể xóa hóa đơn",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // 11. TINH LAI TONG TIEN
    // =====================================================
    private void tinhLaiTongTien() {

        if (tblHoaDon.getSelectedRow() < 0) {
            thongBaoChuaChon();
            return;
        }

        String maHoaDon = txtMaHoaDon.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tính lại tổng tiền hóa đơn " + maHoaDon
                + " từ các dòng chi tiết và tiền giảm?",
                "Xác nhận tính tiền",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean thanhCong =
                    hoaDonController.capNhatTongTien(maHoaDon);

            if (thanhCong) {
                JOptionPane.showMessageDialog(
                        this,
                        "Đã tính và lưu lại tổng tiền hóa đơn."
                );

                taiDanhSachHoaDon();
                chonHoaDon(maHoaDon);

            } else {
                thongBaoThatBai("Cập nhật tổng tiền");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Không thể tính tổng tiền",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // 12. MO CHI TIET CUA HOA DON DANG CHON
    // =====================================================
    private void xemChiTietHoaDon() {
        String maHoaDon = layMaHoaDonDangChon();

        if (maHoaDon == null) {
            thongBaoChuaChon();
            return;
        }

        if (moChiTietHoaDon == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Man hinh hien tai chua duoc gan voi MainFrame.",
                    "Thong bao",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        moChiTietHoaDon.accept(maHoaDon);
    }

    private void thanhToanHoaDon() {
        String maHoaDon = layMaHoaDonDangChon();

        if (maHoaDon == null) {
            thongBaoChuaChon();
            return;
        }

        if (moThanhToanHoaDon == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Man hinh thanh toan chua duoc gan voi MainFrame.",
                    "Thong bao",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        moThanhToanHoaDon.accept(maHoaDon);
    }

    private String layMaHoaDonDangChon() {
        int dongView = tblHoaDon.getSelectedRow();

        if (dongView < 0) {
            return null;
        }

        int dongModel = tblHoaDon.convertRowIndexToModel(dongView);
        return tableModel.getValueAt(dongModel, 0).toString();
    }

    // =====================================================
    // 13. TIM KIEM HOA DON
    // =====================================================
    private void timKiemHoaDon() {

        try {
            List<HoaDon> danhSach = hoaDonController.search(
                    txtTimMaHoaDon.getText().trim(),
                    txtTimMaDatPhong.getText().trim(),
                    txtTimMaKhachHang.getText().trim(),
                    txtTuNgay.getText().trim(),
                    txtDenNgay.getText().trim()
            );

            tableModel.setRowCount(0);

            for (HoaDon hd : danhSach) {
                themHoaDonVaoBang(hd);
            }

            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Không tìm thấy hóa đơn phù hợp.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Lỗi tìm kiếm",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =====================================================
    // 14. DOC DU LIEU TU FORM
    // =====================================================
    private HoaDon docHoaDonTuForm(boolean laThemMoi) {

        String maHoaDon = txtMaHoaDon.getText().trim();
        String maDatPhong = txtMaDatPhong.getText().trim();
        String ngayLap = txtNgayLap.getText().trim();
        String chuoiTienGiam = txtTienGiam.getText()
                .trim().replace(",", "").replace(" ", "");

        if (maHoaDon.isEmpty()
                || maDatPhong.isEmpty()
                || ngayLap.isEmpty()
                || chuoiTienGiam.isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ thông tin hóa đơn."
            );
        }

        double tienGiam;

        try {
            tienGiam = Double.parseDouble(chuoiTienGiam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Tiền giảm phải là số hợp lệ."
            );
        }

        double tongTien = 0;

        if (!laThemMoi) {
            HoaDon hoaDonCu =
                    hoaDonController.findById(maHoaDon);

            if (hoaDonCu == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy hóa đơn cần sửa."
                );
            }

            // Tong tien hien tai duoc Service bao toan khi sua
            tongTien = hoaDonCu.getTongTien();
        }

        return new HoaDon(
                maHoaDon,
                maDatPhong,
                ngayLap,
                tienGiam,
                tongTien,
                cboTrangThai.getSelectedItem().toString()
        );
    }

    // =====================================================
    // 15. CAC HAM HO TRO
    // =====================================================
    private JPanel taoPanelTrangTri(String tieuDe) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 232)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lbl = new JLabel(tieuDe);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(23, 54, 93));

        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    private void themTruong(
            JPanel panel,
            String ten,
            JComponent input,
            int cot,
            int hang) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = cot;
        gbc.gridy = hang;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JLabel label = new JLabel(ten);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, gbc);

        gbc.gridx = cot + 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(input, gbc);
    }

    private JButton taoNut(String ten, Color mau) {

        JButton btn = new JButton(ten);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(mau);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(9, 12, 9, 12));

        return btn;
    }

    private void chonHoaDon(String maHoaDon) {

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ma = tableModel.getValueAt(i, 0).toString();

            if (ma.equals(maHoaDon)) {
                int viewRow = tblHoaDon.convertRowIndexToView(i);
                tblHoaDon.setRowSelectionInterval(viewRow, viewRow);
                tblHoaDon.scrollRectToVisible(
                        tblHoaDon.getCellRect(viewRow, 0, true));
                return;
            }
        }
    }

    private void xoaBoLoc() {
        txtTimMaHoaDon.setText("");
        txtTimMaDatPhong.setText("");
        txtTimMaKhachHang.setText("");
        txtTuNgay.setText("");
        txtDenNgay.setText("");
    }

    private void xoaForm() {
        txtMaHoaDon.setText("");
        txtMaDatPhong.setText("");
        txtNgayLap.setText("");
        txtTienGiam.setText("0");
        txtTongTien.setText("0");
        cboTrangThai.setSelectedItem("Chưa thanh toán");
        txtMaHoaDon.setEditable(true);
        tblHoaDon.clearSelection();
    }

    private void thongBaoChuaChon() {
        JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn một hóa đơn trong bảng.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void thongBaoThatBai(String thaoTac) {
        JOptionPane.showMessageDialog(
                this,
                thaoTac + " không thành công. "
                + "Hãy kiểm tra dữ liệu hoặc cửa sổ Output.",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void hienThiLoi(String thongBao, Exception e) {
        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                thongBao + "\n" + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

package Module_TaiChinh.view;

import Module_TaiChinh.controller.ChiTietHoaDonController;
import Module_TaiChinh.controller.HoaDonController;
import Module_TaiChinh.model.ChiTietHoaDon;
import Module_TaiChinh.model.HoaDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChiTietHoaDonPanel extends JPanel {

    private final ChiTietHoaDonController controller;
    private final HoaDonController hoaDonController;
    private final NumberFormat dinhDangTien
            = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    private String maHoaDonDangXem = "";
    private List<ChiTietHoaDon> danhSachDangHienThi = new ArrayList<>();

    private JLabel lblMaHoaDonHienThi;
    private JLabel lblTongTien;
    private JTextField txtTimMaHoaDon;
    private JTable tblChiTiet;
    private DefaultTableModel tableModel;

    public ChiTietHoaDonPanel() {
        controller = new ChiTietHoaDonController();
        hoaDonController = new HoaDonController();
        taoGiaoDien();
    }

    // =====================================================
    // 1. GIAO DIEN CHINH: CHI HIEN THI TO HOA DON
    // =====================================================
    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(TaiChinhUI.BACKGROUND);

        JPanel tren = new JPanel();
        tren.setOpaque(false);
        tren.setLayout(new BoxLayout(tren, BoxLayout.Y_AXIS));
        tren.add(TaiChinhUI.tieuDe("CHI TIẾT HÓA ĐƠN",
                "Tra cứu các khoản phí và quản lý chi tiết hóa đơn khách sạn"));
        tren.add(Box.createVerticalStrut(15));
        JPanel thongTin = TaiChinhUI.khung("Thông tin chi tiết hóa đơn");
        lblMaHoaDonHienThi = new JLabel("Ma hoa don: Chua tra cuu");
        lblMaHoaDonHienThi.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMaHoaDonHienThi.setForeground(TaiChinhUI.NAVY);
        lblMaHoaDonHienThi.setBorder(new EmptyBorder(0, 6, 0, 6));
        thongTin.add(lblMaHoaDonHienThi, BorderLayout.CENTER);
        tren.add(thongTin);
        tren.add(Box.createVerticalStrut(12));
        tren.add(taoDauHoaDon());
        add(tren, BorderLayout.NORTH);
        add(taoPanelHoaDon(), BorderLayout.CENTER);
        add(taoChanHoaDon(), BorderLayout.SOUTH);
    }

    private JPanel taoPanelHoaDon() {
        JPanel panel = TaiChinhUI.khung("Danh sách chi tiết hóa đơn");
        panel.add(taoBangChiTiet(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoDauHoaDon() {
        JPanel panel = TaiChinhUI.khung("Tra cứu hóa đơn");
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        txtTimMaHoaDon = new JTextField(15);
        JButton btnTimKiem = TaiChinhUI.nut("Tìm hóa đơn", TaiChinhUI.BLUE);
        TaiChinhUI.truong(form, "Mã hóa đơn:", txtTimMaHoaDon, 0, 0);
        TaiChinhUI.nutTraCuu(form, btnTimKiem);
        panel.add(form, BorderLayout.CENTER);
        btnTimKiem.addActionListener(e -> timTheoMaHoaDon());
        txtTimMaHoaDon.addActionListener(e -> timTheoMaHoaDon());
        return panel;
    }

    private JScrollPane taoBangChiTiet() {
        String[] cot = {
            "STT",
            "Ma chi tiet",
            "Mo ta khoan phi",
            "Loai chi phi",
            "Ma phong",
            "Ma dich vu",
            "So luong",
            "Don gia",
            "Thanh tien"
        };

        tableModel = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = new JTable(tableModel);
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TaiChinhUI.bang(tblChiTiet);
        tblChiTiet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblChiTiet.setAutoCreateRowSorter(true);
        tblChiTiet.getTableHeader().setReorderingAllowed(false);

        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(260);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblChiTiet.getColumnModel().getColumn(5).setPreferredWidth(95);
        tblChiTiet.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblChiTiet.getColumnModel().getColumn(7).setPreferredWidth(120);
        tblChiTiet.getColumnModel().getColumn(8).setPreferredWidth(130);

        JScrollPane scroll = new JScrollPane(tblChiTiet);
        scroll.setPreferredSize(new Dimension(700, 180));
        return scroll;
    }

    private JPanel taoChanHoaDon() {
        JPanel panelChan = new JPanel(new BorderLayout(0, 12));
        panelChan.setOpaque(false);
        JPanel panelTongTien = new JPanel(new BorderLayout());
        panelTongTien.setBackground(Color.WHITE);
        panelTongTien.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(12, 12, 12, 12)));
        lblTongTien = new JLabel("TONG CHI TIET: 0 VND");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblTongTien.setForeground(TaiChinhUI.NAVY);
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);
        panelTongTien.add(lblTongTien, BorderLayout.CENTER);

        JPanel panelThaoTac = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelThaoTac.setOpaque(false);
        JButton btnThem = TaiChinhUI.nut("Thêm chi tiết", TaiChinhUI.GREEN);
        JButton btnSua = TaiChinhUI.nut("Sửa chi tiết", TaiChinhUI.ORANGE);
        JButton btnXoa = TaiChinhUI.nut("Xóa chi tiết", TaiChinhUI.RED);
        JButton btnCapNhatTong = TaiChinhUI.nut("Cập nhật tổng hóa đơn", TaiChinhUI.BLUE);
        panelThaoTac.add(btnThem);
        panelThaoTac.add(btnSua);
        panelThaoTac.add(btnXoa);
        panelThaoTac.add(btnCapNhatTong);
        btnThem.addActionListener(e -> moHopThoaiThem());
        btnSua.addActionListener(e -> moHopThoaiSua());
        btnXoa.addActionListener(e -> xoaChiTietDangChon());
        btnCapNhatTong.addActionListener(e -> capNhatTongHoaDon());
        panelChan.add(panelTongTien, BorderLayout.NORTH);
        panelChan.add(panelThaoTac, BorderLayout.SOUTH);
        return panelChan;
    }

    // =====================================================
    // 2. TRA CUU VA TAI DU LIEU
    // =====================================================
    private void timTheoMaHoaDon() {
        String maHoaDon = txtTimMaHoaDon.getText().trim();

        if (maHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Hay nhap ma hoa don can tim.");
            return;
        }

        hienThiHoaDon(maHoaDon);
    }

    
    public void hienThiHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return;
        }

        String maCanXem = maHoaDon.trim();

        try {
            HoaDon hoaDon = hoaDonController.findById(maCanXem);

            if (hoaDon == null) {
                maHoaDonDangXem = "";
                danhSachDangHienThi = new ArrayList<>();
                tableModel.setRowCount(0);
                txtTimMaHoaDon.setText(maCanXem);
                lblMaHoaDonHienThi.setText(
                        "Khong tim thay hoa don: " + maCanXem);
                capNhatTongTien();

                JOptionPane.showMessageDialog(
                        this, "Khong tim thay hoa don " + maCanXem);
                return;
            }

            maHoaDonDangXem = hoaDon.getMaHoaDon();
            txtTimMaHoaDon.setText(maHoaDonDangXem);
            lblMaHoaDonHienThi.setText(
                    "Ma hoa don: " + maHoaDonDangXem);
            taiDanhSachChiTiet();

            if (danhSachDangHienThi.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Hoa don " + maHoaDonDangXem
                                + " chua co dong chi tiet. Ban co the them moi.");
            }
        } catch (Exception e) {
            hienThiLoi("Khong the tim chi tiet hoa don.", e);
        }
    }

    private void taiDanhSachChiTiet() {
        tableModel.setRowCount(0);
        danhSachDangHienThi = new ArrayList<>();

        if (maHoaDonDangXem == null
                || maHoaDonDangXem.trim().isEmpty()) {
            capNhatTongTien();
            return;
        }

        List<ChiTietHoaDon> ketQua
                = controller.getByMaHoaDon(maHoaDonDangXem);

        if (ketQua != null) {
            danhSachDangHienThi = new ArrayList<>(ketQua);
        }

        for (int i = 0; i < danhSachDangHienThi.size(); i++) {
            themChiTietVaoBang(i + 1, danhSachDangHienThi.get(i));
        }

        capNhatTongTien();
    }

    private void themChiTietVaoBang(int stt, ChiTietHoaDon ct) {
        tableModel.addRow(new Object[]{
            stt,
            ct.getMaChiTiet(),
            ct.getMoTa(),
            ct.getLoaiChiPhi(),
            hienThiNull(ct.getMaPhong()),
            hienThiNull(ct.getMaDichVu()),
            ct.getSoLuong(),
            dinhDangTien.format(ct.getDonGia()),
            dinhDangTien.format(ct.getThanhTien())
        });
    }

    private void capNhatTongTien() {
        double tongTien = 0;

        for (ChiTietHoaDon chiTiet : danhSachDangHienThi) {
            tongTien += chiTiet.getThanhTien();
        }

        lblTongTien.setText(
                "TONG CHI TIET: " + dinhDangTien.format(tongTien) + " VND");
    }

    // =====================================================
    // 3. THEM VA SUA BANG JDialog RIENG
    // =====================================================
    private void moHopThoaiThem() {
        if (maHoaDonDangXem == null
                || maHoaDonDangXem.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay tim hoac chon hoa don truoc khi them.");
            return;
        }

        moHopThoaiChiTiet(null);
    }

    private void moHopThoaiSua() {
        ChiTietHoaDon chiTiet = layChiTietDangChon();

        if (chiTiet == null) {
            JOptionPane.showMessageDialog(
                    this, "Hay chon dong chi tiet can sua.");
            return;
        }

        moHopThoaiChiTiet(chiTiet);
    }

    private void moHopThoaiChiTiet(ChiTietHoaDon duLieuCu) {
        boolean laThemMoi = duLieuCu == null;

        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                cuaSoCha,
                laThemMoi ? "Them chi tiet hoa don" : "Sua chi tiet hoa don",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 15, 5, 15));
        form.setBackground(Color.WHITE);

        JTextField txtMaChiTiet = new JTextField(18);
        JTextField txtMaHoaDon = new JTextField(18);
        JTextField txtMaPhong = new JTextField(18);
        JTextField txtMaDichVu = new JTextField(18);
        JTextField txtLoaiChiPhi = new JTextField(18);
        JTextField txtMoTa = new JTextField(18);
        JTextField txtSoLuong = new JTextField(18);
        JTextField txtDonGia = new JTextField(18);
        JTextField txtThanhTien = new JTextField(18);

        txtMaHoaDon.setText(maHoaDonDangXem);
        txtMaHoaDon.setEditable(false);
        txtThanhTien.setEditable(false);
        txtThanhTien.setText("0");
        txtSoLuong.setText("1");
        txtDonGia.setText("0");

        if (!laThemMoi) {
            txtMaChiTiet.setText(duLieuCu.getMaChiTiet());
            txtMaChiTiet.setEditable(false);
            txtMaHoaDon.setText(duLieuCu.getMaHoaDon());
            txtMaPhong.setText(hienThiNull(duLieuCu.getMaPhong()));
            txtMaDichVu.setText(hienThiNull(duLieuCu.getMaDichVu()));
            txtLoaiChiPhi.setText(duLieuCu.getLoaiChiPhi());
            txtMoTa.setText(duLieuCu.getMoTa());
            txtSoLuong.setText(String.valueOf(duLieuCu.getSoLuong()));
            txtDonGia.setText(String.valueOf(duLieuCu.getDonGia()));
            txtThanhTien.setText(
                    dinhDangTien.format(duLieuCu.getThanhTien()));
        }

        int hang = 0;
        themTruong(form, "Ma chi tiet:", txtMaChiTiet, hang++);
        themTruong(form, "Ma hoa don:", txtMaHoaDon, hang++);
        themTruong(form, "Ma phong:", txtMaPhong, hang++);
        themTruong(form, "Ma dich vu:", txtMaDichVu, hang++);
        themTruong(form, "Loai chi phi:", txtLoaiChiPhi, hang++);
        themTruong(form, "Mo ta:", txtMoTa, hang++);
        themTruong(form, "So luong:", txtSoLuong, hang++);
        themTruong(form, "Don gia:", txtDonGia, hang++);
        themTruong(form, "Thanh tien:", txtThanhTien, hang);

        JPanel panelNut = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 10));
        JButton btnTinhTien = TaiChinhUI.nut("Tính thành tiền", TaiChinhUI.BLUE);
        JButton btnLuu = TaiChinhUI.nut(laThemMoi ? "Thêm" : "Lưu thay đổi", laThemMoi ? TaiChinhUI.GREEN : TaiChinhUI.ORANGE);
        JButton btnHuy = TaiChinhUI.nut("Hủy", TaiChinhUI.GRAY);

        panelNut.add(btnTinhTien);
        panelNut.add(btnLuu);
        panelNut.add(btnHuy);

        btnTinhTien.addActionListener(e -> {
            try {
                int soLuong = docSoLuong(txtSoLuong.getText());
                double donGia = docTien(txtDonGia.getText());

                ChiTietHoaDon tam = new ChiTietHoaDon();
                tam.setSoLuong(soLuong);
                tam.setDonGia(donGia);

                double thanhTien = controller.tinhThanhTien(tam);
                txtThanhTien.setText(dinhDangTien.format(thanhTien));
            } catch (Exception ex) {
                hienThiLoi("Khong the tinh thanh tien.", ex);
            }
        });

        btnLuu.addActionListener(e -> {
            try {
                ChiTietHoaDon chiTiet = docDuLieuTuDialog(
                        txtMaChiTiet,
                        txtMaHoaDon,
                        txtMaPhong,
                        txtMaDichVu,
                        txtLoaiChiPhi,
                        txtMoTa,
                        txtSoLuong,
                        txtDonGia);

                boolean thanhCong = laThemMoi
                        ? controller.themChiTiet(chiTiet)
                        : controller.suaChiTiet(chiTiet);

                if (!thanhCong) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            laThemMoi
                                    ? "Them that bai. Hay kiem tra ma va rang buoc du lieu."
                                    : "Cap nhat that bai. Hay kiem tra lai du lieu.",
                            "Thong bao",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                taiDanhSachChiTiet();
                JOptionPane.showMessageDialog(
                        dialog,
                        laThemMoi
                                ? "Them chi tiet hoa don thanh cong."
                                : "Cap nhat chi tiet hoa don thanh cong.");
                dialog.dispose();
            } catch (Exception ex) {
                hienThiLoi(
                        laThemMoi
                                ? "Khong the them chi tiet hoa don."
                                : "Khong the sua chi tiet hoa don.",
                        ex);
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(panelNut, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(btnLuu);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(540, dialog.getHeight()));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void themTruong(
            JPanel panel, String ten, JComponent truong, int hang) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = hang;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(ten), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(truong, gbc);
    }

    private ChiTietHoaDon docDuLieuTuDialog(
            JTextField txtMaChiTiet,
            JTextField txtMaHoaDon,
            JTextField txtMaPhong,
            JTextField txtMaDichVu,
            JTextField txtLoaiChiPhi,
            JTextField txtMoTa,
            JTextField txtSoLuong,
            JTextField txtDonGia) {

        String maChiTiet = txtMaChiTiet.getText().trim();
        String maHoaDon = txtMaHoaDon.getText().trim();
        String maPhong = txtMaPhong.getText().trim();
        String maDichVu = txtMaDichVu.getText().trim();
        String loaiChiPhi = txtLoaiChiPhi.getText().trim();
        String moTa = txtMoTa.getText().trim();

        if (maChiTiet.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma chi tiet khong duoc de trong.");
        }
        if (maHoaDon.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma hoa don khong duoc de trong.");
        }
        if (loaiChiPhi.isEmpty()) {
            throw new IllegalArgumentException(
                    "Loai chi phi khong duoc de trong.");
        }
        if (moTa.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mo ta khong duoc de trong.");
        }

        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
        chiTiet.setMaChiTiet(maChiTiet);
        chiTiet.setMaHoaDon(maHoaDon);
        chiTiet.setMaPhong(maPhong.isEmpty() ? null : maPhong);
        chiTiet.setMaDichVu(maDichVu.isEmpty() ? null : maDichVu);
        chiTiet.setLoaiChiPhi(loaiChiPhi);
        chiTiet.setMoTa(moTa);
        chiTiet.setSoLuong(docSoLuong(txtSoLuong.getText()));
        chiTiet.setDonGia(docTien(txtDonGia.getText()));

        return chiTiet;
    }

    // =====================================================
    // 4. XOA TRUC TIEP THEO DONG DANG CHON
    // =====================================================
    private void xoaChiTietDangChon() {
        ChiTietHoaDon chiTiet = layChiTietDangChon();

        if (chiTiet == null) {
            JOptionPane.showMessageDialog(
                    this, "Hay chon dong chi tiet can xoa.");
            return;
        }

        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Ban co chac chan muon xoa chi tiet "
                        + chiTiet.getMaChiTiet() + " khong?",
                "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean thanhCong
                    = controller.xoaChiTiet(chiTiet.getMaChiTiet());

            if (!thanhCong) {
                JOptionPane.showMessageDialog(
                        this,
                        "Xoa that bai. Hay kiem tra lai du lieu.",
                        "Thong bao",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            taiDanhSachChiTiet();
            JOptionPane.showMessageDialog(
                    this, "Xoa chi tiet thanh cong.");
        } catch (Exception e) {
            hienThiLoi("Khong the xoa chi tiet hoa don.", e);
        }
    }

    private ChiTietHoaDon layChiTietDangChon() {
        int dongView = tblChiTiet.getSelectedRow();

        if (dongView < 0) {
            return null;
        }

        int dongModel = tblChiTiet.convertRowIndexToModel(dongView);

        if (dongModel < 0
                || dongModel >= danhSachDangHienThi.size()) {
            return null;
        }

        return danhSachDangHienThi.get(dongModel);
    }

    // =====================================================
    // 5. DONG BO TONG CHI TIET VAO BANG HOA DON
    // =====================================================
    private void capNhatTongHoaDon() {
        if (maHoaDonDangXem == null
                || maHoaDonDangXem.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Hay tim hoac chon hoa don truoc.");
            return;
        }

        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Tong hoa don se duoc tinh bang tong chi tiet tru tien giam.\n"
                        + "Ban co muon cap nhat hoa don "
                        + maHoaDonDangXem + " khong?",
                "Xac nhan cap nhat tong tien",
                JOptionPane.YES_NO_OPTION);

        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean thanhCong
                    = hoaDonController.capNhatTongTien(maHoaDonDangXem);

            if (thanhCong) {
                HoaDon hoaDon
                        = hoaDonController.findById(maHoaDonDangXem);
                JOptionPane.showMessageDialog(
                        this,
                        "Da cap nhat tong hoa don: "
                                + dinhDangTien.format(hoaDon.getTongTien())
                                + " VND.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Cap nhat tong hoa don khong thanh cong.",
                        "Thong bao",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            hienThiLoi("Khong the cap nhat tong hoa don.", e);
        }
    }

    // =====================================================
    // 6. CAC HAM HO TRO
    // =====================================================
    private int docSoLuong(String giaTri) {
        try {
            return Integer.parseInt(giaTri.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "So luong phai la so nguyen hop le.");
        }
    }

    private double docTien(String giaTri) {
        String chuoiTien = giaTri.trim().replace(" ", "");

        if (chuoiTien.contains(",") && chuoiTien.contains(".")) {
            chuoiTien = chuoiTien.replace(".", "").replace(",", ".");
        } else {
            chuoiTien = chuoiTien.replace(",", ".");
        }

        try {
            return Double.parseDouble(chuoiTien);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Don gia phai la so hop le.");
        }
    }

    private String hienThiNull(String giaTri) {
        return giaTri == null ? "" : giaTri;
    }

    private void hienThiLoi(String thongBao, Exception e) {
        e.printStackTrace();

        String chiTietLoi = e.getMessage();
        if (chiTietLoi == null || chiTietLoi.isBlank()) {
            chiTietLoi = e.getClass().getSimpleName();
        }

        JOptionPane.showMessageDialog(
                this,
                thongBao + "\n" + chiTietLoi,
                "Loi",
                JOptionPane.ERROR_MESSAGE);
    }
}

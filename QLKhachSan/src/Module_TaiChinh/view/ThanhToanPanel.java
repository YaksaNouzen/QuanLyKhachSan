package Module_TaiChinh.view;

import Module_TaiChinh.controller.HoaDonController;
import Module_TaiChinh.controller.ThanhToanController;
import Module_TaiChinh.model.HoaDon;
import Module_TaiChinh.model.ThanhToan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThanhToanPanel extends JPanel {

    private final ThanhToanController thanhToanController;
    private final HoaDonController hoaDonController;
    private final NumberFormat dinhDangTien
            = NumberFormat.getNumberInstance(
                    Locale.forLanguageTag("vi-VN"));

    private String maHoaDonDangXem = "";
    private List<ThanhToan> danhSachDangHienThi = new ArrayList<>();

    private JTextField txtTimMaHoaDon;
    private JLabel lblMaHoaDon;
    private JLabel lblTongHoaDon;
    private JLabel lblDaThanhToan;
    private JLabel lblConLai;
    private JLabel lblTrangThai;
    private JTable tblThanhToan;
    private DefaultTableModel tableModel;

    public ThanhToanPanel() {
        thanhToanController = new ThanhToanController();
        hoaDonController = new HoaDonController();
        taoGiaoDien();
    }

    // =====================================================
    // 1. TAO GIAO DIEN
    // =====================================================
    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(TaiChinhUI.BACKGROUND);
        JPanel panelTren = new JPanel();
        panelTren.setOpaque(false);
        panelTren.setLayout(new BoxLayout(panelTren, BoxLayout.Y_AXIS));
        panelTren.add(TaiChinhUI.tieuDe("QUẢN LÝ THANH TOÁN",
                "Theo dõi lịch sử thanh toán và số tiền còn lại của hóa đơn"));
        panelTren.add(Box.createVerticalStrut(15));
        panelTren.add(taoPanelTongQuan());
        panelTren.add(Box.createVerticalStrut(12));
        panelTren.add(taoPanelTimKiem());
        add(panelTren, BorderLayout.NORTH);
        add(taoPanelBang(), BorderLayout.CENTER);
        add(taoPanelNut(), BorderLayout.SOUTH);
    }

    private JPanel taoPanelTimKiem() {
        JPanel panel = taoPanelTrangTri("Tra cứu hóa đơn");
        JPanel noiDung = new JPanel(new GridBagLayout());
        noiDung.setBackground(Color.WHITE);
        txtTimMaHoaDon = new JTextField(15);
        JButton btnTim = TaiChinhUI.nut("Tìm hóa đơn", TaiChinhUI.BLUE);
        TaiChinhUI.truong(noiDung, "Mã hóa đơn:", txtTimMaHoaDon, 0, 0);
        TaiChinhUI.nutTraCuu(noiDung, btnTim);
        panel.add(noiDung, BorderLayout.CENTER);
        btnTim.addActionListener(e -> timHoaDon());
        txtTimMaHoaDon.addActionListener(e -> timHoaDon());
        return panel;
    }

    private JPanel taoPanelTongQuan() {
        JPanel panel = taoPanelTrangTri("Thông tin thanh toán");
        JPanel noiDung = new JPanel(new GridLayout(2, 3, 10, 5));
        noiDung.setBackground(Color.WHITE);

        lblMaHoaDon = taoNhanTongQuan("Ma hoa don", "Chua tra cuu");
        lblTongHoaDon = taoNhanTongQuan("Tong hoa don", "0 VND");
        lblDaThanhToan = taoNhanTongQuan("Da thanh toan", "0 VND");
        lblConLai = taoNhanTongQuan("Con lai", "0 VND");
        lblTrangThai = taoNhanTongQuan("Trang thai", "-");

        noiDung.add(lblMaHoaDon);
        noiDung.add(lblTongHoaDon);
        noiDung.add(lblDaThanhToan);
        noiDung.add(lblConLai);
        noiDung.add(lblTrangThai);
        panel.add(noiDung, BorderLayout.CENTER);
        return panel;
    }

    private JLabel taoNhanTongQuan(String tieuDe, String giaTri) {
        JLabel label = new JLabel(
                "<html><span style='color:#777777'>" + tieuDe
                        + "</span><br><b>" + giaTri + "</b></html>");
        label.setBorder(new EmptyBorder(8, 8, 8, 8));
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        return label;
    }

    private JPanel taoPanelBang() {
        JPanel panel = taoPanelTrangTri("Danh sách giao dịch thanh toán");

        String[] cot = {
            "Ma thanh toan",
            "Ngay thanh toan",
            "Phuong thuc",
            "So tien",
            "Trang thai"
        };

        tableModel = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThanhToan = new JTable(tableModel);
        TaiChinhUI.bang(tblThanhToan);
        tblThanhToan.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        tblThanhToan.setAutoCreateRowSorter(true);
        tblThanhToan.getTableHeader().setReorderingAllowed(false);

        tblThanhToan.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblThanhToan.getColumnModel().getColumn(1).setPreferredWidth(125);
        tblThanhToan.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblThanhToan.getColumnModel().getColumn(3).setPreferredWidth(135);
        tblThanhToan.getColumnModel().getColumn(4).setPreferredWidth(130);

        panel.add(new JScrollPane(tblThanhToan), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JButton btnThem = TaiChinhUI.nut("Thêm thanh toán", TaiChinhUI.GREEN);
        JButton btnSua = TaiChinhUI.nut("Sửa giao dịch", TaiChinhUI.ORANGE);
        JButton btnXoa = TaiChinhUI.nut("Xóa giao dịch", TaiChinhUI.RED);
        JButton btnLamMoi = TaiChinhUI.nut("Tải lại", TaiChinhUI.GRAY);

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);

        btnThem.addActionListener(e -> moHopThoaiThem());
        btnSua.addActionListener(e -> moHopThoaiSua());
        btnXoa.addActionListener(e -> xoaGiaoDich());
        btnLamMoi.addActionListener(e -> taiLaiHoaDonDangXem());
        return panel;
    }

    private JPanel taoPanelTrangTri(String tieuDe) {
        return TaiChinhUI.khung(tieuDe);
    }

    // =====================================================
    // 2. TRA CUU VA TAI LICH SU THANH TOAN
    // =====================================================
    private void timHoaDon() {
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

        try {
            HoaDon hoaDon = hoaDonController.findById(maHoaDon.trim());

            if (hoaDon == null) {
                xoaDuLieuDangHienThi();
                txtTimMaHoaDon.setText(maHoaDon.trim());
                JOptionPane.showMessageDialog(
                        this, "Khong tim thay hoa don " + maHoaDon.trim());
                return;
            }

            maHoaDonDangXem = hoaDon.getMaHoaDon();
            txtTimMaHoaDon.setText(maHoaDonDangXem);
            taiLaiHoaDonDangXem();
        } catch (Exception e) {
            hienThiLoi("Khong the tai thong tin thanh toan.", e);
        }
    }

    private void taiLaiHoaDonDangXem() {
        if (maHoaDonDangXem == null
                || maHoaDonDangXem.isBlank()) {
            JOptionPane.showMessageDialog(
                    this, "Hay tim hoa don truoc.");
            return;
        }

        try {
            HoaDon hoaDon
                    = hoaDonController.findById(maHoaDonDangXem);

            if (hoaDon == null) {
                xoaDuLieuDangHienThi();
                JOptionPane.showMessageDialog(
                        this, "Hoa don khong con ton tai.");
                return;
            }

            List<ThanhToan> ketQua
                    = thanhToanController.getByMaHoaDon(
                            maHoaDonDangXem);
            danhSachDangHienThi = ketQua == null
                    ? new ArrayList<>() : new ArrayList<>(ketQua);

            tableModel.setRowCount(0);
            for (ThanhToan thanhToan : danhSachDangHienThi) {
                tableModel.addRow(new Object[]{
                    thanhToan.getMaThanhToan(),
                    thanhToan.getNgayThanhToan(),
                    thanhToan.getPhuongThuc(),
                    dinhDangTien.format(thanhToan.getSoTien()),
                    thanhToan.getTrangThai()
                });
            }

            capNhatThongTinTongQuan(hoaDon);
        } catch (Exception e) {
            hienThiLoi("Khong the tai lai thanh toan.", e);
        }
    }

    private void capNhatThongTinTongQuan(HoaDon hoaDon) {
        double tongHoaDon
                = hoaDonController.tinhTongTien(hoaDon.getMaHoaDon());
        double daThanhToan
                = thanhToanController.tinhTongDaThanhToan(
                        hoaDon.getMaHoaDon());
        double conLai
                = thanhToanController.tinhSoTienConLai(
                        hoaDon.getMaHoaDon());

        lblMaHoaDon.setText(taoNoiDungNhan(
                "Ma hoa don", hoaDon.getMaHoaDon()));
        lblTongHoaDon.setText(taoNoiDungNhan(
                "Tong hoa don", dinhDangTien.format(tongHoaDon) + " VND"));
        lblDaThanhToan.setText(taoNoiDungNhan(
                "Da thanh toan", dinhDangTien.format(daThanhToan) + " VND"));
        lblConLai.setText(taoNoiDungNhan(
                "Con lai", dinhDangTien.format(conLai) + " VND"));
        lblTrangThai.setText(taoNoiDungNhan(
                "Trang thai", hoaDon.getTrangThai()));
    }

    private String taoNoiDungNhan(String tieuDe, String giaTri) {
        return "<html><span style='color:#777777'>" + tieuDe
                + "</span><br><b>" + giaTri + "</b></html>";
    }

    private void xoaDuLieuDangHienThi() {
        maHoaDonDangXem = "";
        danhSachDangHienThi = new ArrayList<>();
        tableModel.setRowCount(0);
        lblMaHoaDon.setText(taoNoiDungNhan("Ma hoa don", "Chua tra cuu"));
        lblTongHoaDon.setText(taoNoiDungNhan("Tong hoa don", "0 VND"));
        lblDaThanhToan.setText(taoNoiDungNhan("Da thanh toan", "0 VND"));
        lblConLai.setText(taoNoiDungNhan("Con lai", "0 VND"));
        lblTrangThai.setText(taoNoiDungNhan("Trang thai", "-"));
    }

    // =====================================================
    // 3. THEM, SUA GIAO DICH BANG JDialog
    // =====================================================
    private void moHopThoaiThem() {
        if (maHoaDonDangXem == null
                || maHoaDonDangXem.isBlank()) {
            JOptionPane.showMessageDialog(
                    this, "Hay tim hoa don truoc khi thanh toan.");
            return;
        }

        try {
            double conLai = thanhToanController.tinhSoTienConLai(
                    maHoaDonDangXem);

            if (conLai <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Hoa don nay da duoc thanh toan du.");
                return;
            }

            moHopThoaiGiaoDich(null);
        } catch (Exception e) {
            hienThiLoi("Khong the kiem tra so tien con lai.", e);
        }
    }

    private void moHopThoaiSua() {
        ThanhToan giaoDich = layGiaoDichDangChon();

        if (giaoDich == null) {
            JOptionPane.showMessageDialog(
                    this, "Hay chon giao dich can sua.");
            return;
        }

        moHopThoaiGiaoDich(giaoDich);
    }

    private void moHopThoaiGiaoDich(ThanhToan duLieuCu) {
        boolean laThemMoi = duLieuCu == null;

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                laThemMoi ? "Them thanh toan" : "Sua giao dich thanh toan",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 15, 5, 15));
        form.setBackground(Color.WHITE);

        JTextField txtMaThanhToan = new JTextField(20);
        JTextField txtMaHoaDon = new JTextField(20);
        JTextField txtNgayThanhToan = new JTextField(20);
        JComboBox<String> cboPhuongThuc = new JComboBox<>(new String[]{
            "Tiền mặt", "Chuyển khoản", "Thẻ"
        });
        JTextField txtSoTien = new JTextField(20);
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{
            "Thành công", "Chờ xử lý", "Thất bại"
        });

        txtMaHoaDon.setText(maHoaDonDangXem);
        txtMaHoaDon.setEditable(false);
        txtNgayThanhToan.setText(LocalDate.now().toString());

        if (!laThemMoi) {
            txtMaThanhToan.setText(duLieuCu.getMaThanhToan());
            txtMaThanhToan.setEditable(false);
            txtMaHoaDon.setText(duLieuCu.getMaHoaDon());
            txtNgayThanhToan.setText(duLieuCu.getNgayThanhToan());
            cboPhuongThuc.setSelectedItem(duLieuCu.getPhuongThuc());
            txtSoTien.setText(String.valueOf(duLieuCu.getSoTien()));
            cboTrangThai.setSelectedItem(duLieuCu.getTrangThai());
        } else {
            try {
                txtSoTien.setText(String.valueOf(
                        thanhToanController.tinhSoTienConLai(
                                maHoaDonDangXem)));
            } catch (Exception e) {
                txtSoTien.setText("0");
            }
        }

        int hang = 0;
        themTruong(form, "Ma thanh toan:", txtMaThanhToan, hang++);
        themTruong(form, "Ma hoa don:", txtMaHoaDon, hang++);
        themTruong(form, "Ngay thanh toan (yyyy-MM-dd):",
                txtNgayThanhToan, hang++);
        themTruong(form, "Phuong thuc:", cboPhuongThuc, hang++);
        themTruong(form, "So tien:", txtSoTien, hang++);
        themTruong(form, "Trang thai:", cboTrangThai, hang);

        JPanel panelNut = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 10));
        JButton btnLuu = TaiChinhUI.nut(laThemMoi ? "Thêm thanh toán" : "Lưu thay đổi", laThemMoi ? TaiChinhUI.GREEN : TaiChinhUI.ORANGE);
        JButton btnHuy = TaiChinhUI.nut("Hủy", TaiChinhUI.GRAY);
        panelNut.add(btnLuu);
        panelNut.add(btnHuy);

        btnLuu.addActionListener(e -> {
            try {
                ThanhToan thanhToan = new ThanhToan(
                        txtMaThanhToan.getText().trim(),
                        txtMaHoaDon.getText().trim(),
                        txtNgayThanhToan.getText().trim(),
                        cboPhuongThuc.getSelectedItem().toString(),
                        docTien(txtSoTien.getText()),
                        cboTrangThai.getSelectedItem().toString());

                boolean thanhCong = laThemMoi
                        ? thanhToanController.themThanhToan(thanhToan)
                        : thanhToanController.suaThanhToan(thanhToan);

                if (!thanhCong) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "Khong the luu giao dich. Hay kiem tra du lieu.",
                            "Thong bao",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                taiLaiHoaDonDangXem();
                JOptionPane.showMessageDialog(
                        dialog, "Luu giao dich thanh cong.");
                dialog.dispose();
            } catch (Exception ex) {
                hienThiLoi("Khong the luu giao dich thanh toan.", ex);
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
            JPanel panel, String ten, JComponent input, int hang) {
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
        panel.add(input, gbc);
    }

    // =====================================================
    // 4. XOA GIAO DICH
    // =====================================================
    private void xoaGiaoDich() {
        ThanhToan giaoDich = layGiaoDichDangChon();

        if (giaoDich == null) {
            JOptionPane.showMessageDialog(
                    this, "Hay chon giao dich can xoa.");
            return;
        }

        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Xoa giao dich " + giaoDich.getMaThanhToan()
                        + " se tinh lai trang thai hoa don.\n"
                        + "Ban co chac chan muon xoa khong?",
                "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean thanhCong = thanhToanController.xoaThanhToan(
                    giaoDich.getMaThanhToan());

            if (thanhCong) {
                taiLaiHoaDonDangXem();
                JOptionPane.showMessageDialog(
                        this, "Xoa giao dich thanh cong.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Xoa giao dich khong thanh cong.",
                        "Thong bao",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            hienThiLoi("Khong the xoa giao dich.", e);
        }
    }

    private ThanhToan layGiaoDichDangChon() {
        int dongView = tblThanhToan.getSelectedRow();

        if (dongView < 0) {
            return null;
        }

        int dongModel = tblThanhToan.convertRowIndexToModel(dongView);

        if (dongModel < 0
                || dongModel >= danhSachDangHienThi.size()) {
            return null;
        }

        return danhSachDangHienThi.get(dongModel);
    }

    // =====================================================
    // 5. HAM HO TRO
    // =====================================================
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
                    "So tien thanh toan phai la so hop le.");
        }
    }

    private void hienThiLoi(String thongBao, Exception e) {
        e.printStackTrace();

        String chiTiet = e.getMessage();
        if (chiTiet == null || chiTiet.isBlank()) {
            chiTiet = e.getClass().getSimpleName();
        }

        JOptionPane.showMessageDialog(
                this,
                thongBao + "\n" + chiTiet,
                "Loi",
                JOptionPane.ERROR_MESSAGE);
    }
}

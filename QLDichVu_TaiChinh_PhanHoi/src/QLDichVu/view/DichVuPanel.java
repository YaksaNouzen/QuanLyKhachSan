
package QLDichVu.view;

import QLDichVu.controller.DichVuController;
import QLDichVu.model.DichVu;
import QLDichVu.model.LoaiDichVu;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DichVuPanel extends JPanel {
private void taiDanhSachLoai() {

    try {
        cboLoaiDV.removeAllItems();

        for (LoaiDichVu loai : controller.layDanhSachLoai()) {
            cboLoaiDV.addItem(loai);
        }

    } catch (IllegalStateException e) {
        baoLoi(e);
    }
}
    private final DichVuController controller;

    // Cac o nhap lieu
    private JTextField txtMaDV;
    private JComboBox<LoaiDichVu> cboLoaiDV;

    private JTextField txtTenDV;
    private JTextField txtDonGia;
    private JTextField txtDonViTinh;
    private JTextField txtMoTa;
    private JTextField txtTimKiem;

    // Trang thai dich vu
    private JCheckBox chkTrangThai;

    // Bang du lieu
    private JTable tblDichVu;
    private DefaultTableModel model;

    // Ma dich vu dang duoc chon
    private int maDVDangChon = -1;

    public DichVuPanel() {
        this(new DichVuController());
    }

    public DichVuPanel(DichVuController controller) {
        if (controller == null) {
            throw new IllegalArgumentException(
                    "Controller dich vu khong hop le.");
        }
        this.controller = controller;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        setBackground(new Color(245, 247, 250));

        khoiTaoGiaoDien();
        taiDanhSachLoai();
        taiDanhSach();
    }

    private void khoiTaoGiaoDien() {
        JPanel tren = new JPanel();
        tren.setOpaque(false);
        tren.setLayout(new BoxLayout(tren, BoxLayout.Y_AXIS));
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("QUẢN LÝ DỊCH VỤ");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(23, 54, 93));
        JLabel subtitle = new JLabel("Quản lý thông tin và tra cứu dịch vụ khách sạn");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);
        tieuDe.add(text, BorderLayout.WEST);
        tren.add(tieuDe);
        tren.add(Box.createVerticalStrut(15));

        txtMaDV = new JTextField(15);
        txtMaDV.setEditable(false);
        txtMaDV.setBackground(new Color(240, 240, 240));
        cboLoaiDV = new JComboBox<>();
        txtTenDV = new JTextField(15);
        txtDonGia = new JTextField(15);
        txtDonViTinh = new JTextField(15);
        txtMoTa = new JTextField(15);
        chkTrangThai = new JCheckBox("Đang hoạt động", true);
        chkTrangThai.setOpaque(false);
        JPanel nhap = taoPanelTrangTri("Thông tin dịch vụ");
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        themTruong(form, "Mã dịch vụ:", txtMaDV, 0, 0);
        themTruong(form, "Loại dịch vụ:", cboLoaiDV, 2, 0);
        themTruong(form, "Tên dịch vụ:", txtTenDV, 0, 1);
        themTruong(form, "Đơn giá (VNĐ):", txtDonGia, 2, 1);
        themTruong(form, "Đơn vị tính:", txtDonViTinh, 0, 2);
        themTruong(form, "Trạng thái:", chkTrangThai, 2, 2);
        themTruong(form, "Mô tả:", txtMoTa, 0, 3);
        nhap.add(form, BorderLayout.CENTER);
        tren.add(nhap);
        tren.add(Box.createVerticalStrut(12));

        JPanel tim = taoPanelTrangTri("Tìm kiếm dịch vụ");
        JPanel timForm = new JPanel(new GridBagLayout());
        timForm.setBackground(Color.WHITE);
        txtTimKiem = new JTextField(25);
        themTruong(timForm, "Tên dịch vụ:", txtTimKiem, 0, 0);
        JButton btnTimKiem = taoNut("Tìm kiếm", new Color(41, 98, 160));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        timForm.add(btnTimKiem, gbc);
        tim.add(timForm, BorderLayout.CENTER);
        tren.add(tim);
        add(tren, BorderLayout.NORTH);

        String[] cot = {"Mã DV", "Mã loại DV", "Tên dịch vụ", "Đơn giá (VNĐ)",
            "Đơn vị tính", "Mô tả", "Trạng thái"};
        model = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDichVu = new JTable(model);
        tblDichVu.setRowHeight(29);
        tblDichVu.setFont(new Font("Arial", Font.PLAIN, 13));
        tblDichVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDichVu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblDichVu.getTableHeader().setBackground(new Color(225, 233, 243));
        tblDichVu.getTableHeader().setForeground(new Color(23, 54, 93));
        tblDichVu.getTableHeader().setReorderingAllowed(false);
        int[] widths = {65, 85, 170, 120, 90, 180, 130};
        for (int i = 0; i < widths.length; i++) {
            tblDichVu.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JPanel bang = taoPanelTrangTri("Danh sách dịch vụ");
        JScrollPane scroll = new JScrollPane(tblDichVu);
        scroll.setPreferredSize(new Dimension(700, 230));
        bang.add(scroll, BorderLayout.CENTER);
        add(bang, BorderLayout.CENTER);

        JPanel nut = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        nut.setOpaque(false);
        JButton btnThem = taoNut("Thêm", new Color(39, 145, 87));
        JButton btnCapNhat = taoNut("Sửa", new Color(230, 151, 37));
        JButton btnNgung = taoNut("Ngừng hoạt động", new Color(192, 57, 43));
        JButton btnKhoiPhuc = taoNut("Khôi phục", new Color(32, 130, 120));
        JButton btnXoaTrang = taoNut("Xóa trắng", new Color(108, 117, 125));
        JButton btnLamMoi = taoNut("Làm mới", new Color(108, 117, 125));
        for (JButton button : new JButton[]{btnThem, btnCapNhat, btnNgung,
                btnKhoiPhuc, btnXoaTrang, btnLamMoi}) {
            nut.add(button);
        }
        add(nut, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> themDichVu());
        btnCapNhat.addActionListener(e -> capNhatDichVu());
        btnNgung.addActionListener(e -> ngungHoatDong());
        btnXoaTrang.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> timKiemDichVu());
        txtTimKiem.addActionListener(e -> timKiemDichVu());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            xoaTrangForm();
            taiDanhSach();
        });
        btnKhoiPhuc.addActionListener(e -> khoiPhucDichVu());
        tblDichVu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });
    }

    private JPanel taoPanelTrangTri(String tieuDe) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel label = new JLabel(tieuDe);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(new Color(23, 54, 93));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private void themTruong(JPanel panel, String ten, JComponent input, int cot, int hang) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = cot;
        gbc.gridy = hang;
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
    // Doc du lieu tu database va hien thi len bang
    private void taiDanhSach() {
        try {
            hienThiDanhSach(controller.layTatCa());
        } catch (IllegalStateException e) {
            baoLoi(e);
        }
    }

    private void hienThiDanhSach(List<DichVu> danhSach) {

        model.setRowCount(0);

        for (DichVu dv : danhSach) {
            model.addRow(new Object[]{
                dv.getMaDV(),
                dv.getMaLoaiDV(),
                dv.getTenDV(),
                dv.getDonGia(),
                dv.getDonViTinh(),
                dv.getMoTa(),
                dv.isTrangThai()
                        ? "Dang hoat dong"
                        : "Ngung hoat dong"
            });
        }
    }

    private void khoiPhucDichVu() {

        if (maDVDangChon <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay chon dich vu can khoi phuc."
            );
            return;
        }

        if (chkTrangThai.isSelected()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Dich vu nay dang hoat dong."
            );
            return;
        }

        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Ban co muon khoi phuc dich vu nay?",
                "Xac nhan",
                JOptionPane.YES_NO_OPTION
        );

        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controller.khoiPhuc(maDVDangChon)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Khoi phuc dich vu thanh cong!"
                );

                xoaTrangForm();
                taiDanhSach();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Khong the khoi phuc dich vu."
                );
            }

        } catch (IllegalArgumentException e) {
            baoDuLieuKhongHopLe(e);
        } catch (IllegalStateException e) {
            baoLoi(e);
        }
    }

    // Dua thong tin dong duoc chon len form
    private void hienThiThongTin() {

        int row = tblDichVu.getSelectedRow();

        if (row < 0) {
            return;
        }

        maDVDangChon = (int) model.getValueAt(row, 0);

        txtMaDV.setText(String.valueOf(maDVDangChon));
       int maLoai = (int) model.getValueAt(row, 1);
for (int i = 0; i < cboLoaiDV.getItemCount(); i++) {
    LoaiDichVu loai = cboLoaiDV.getItemAt(i);

    if (loai.getMaLoaiDV() == maLoai) {
        cboLoaiDV.setSelectedIndex(i);
        break;
    }
}
        txtTenDV.setText(
                giaTriChuoi(model.getValueAt(row, 2))
        );
        txtDonGia.setText(
                giaTriChuoi(model.getValueAt(row, 3))
        );
        txtDonViTinh.setText(
                giaTriChuoi(model.getValueAt(row, 4))
        );
        txtMoTa.setText(
                giaTriChuoi(model.getValueAt(row, 5))
        );

        String trangThai = giaTriChuoi(
                model.getValueAt(row, 6)
        );

        chkTrangThai.setSelected(
                trangThai.equals("Dang hoat dong")
        );
    }

    // Them dich vu
    private void themDichVu() {

        try {
            LoaiDichVu loai =
                    (LoaiDichVu) cboLoaiDV.getSelectedItem();
            if (loai == null) {
                throw new IllegalArgumentException(
                        "Hay chon loai dich vu.");
            }

            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    "Ban co chac muon them dich vu nay?",
                    "Xac nhan",
                    JOptionPane.YES_NO_OPTION
            );

            if (luaChon != JOptionPane.YES_OPTION) {
                return;
            }

            if (controller.themDichVu(
                    loai.getMaLoaiDV(),
                    txtTenDV.getText(),
                    txtDonGia.getText(),
                    txtDonViTinh.getText(),
                    txtMoTa.getText(),
                    chkTrangThai.isSelected())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Them dich vu thanh cong!"
                );

                xoaTrangForm();
                taiDanhSach();
            }

        } catch (IllegalArgumentException e) {
            baoDuLieuKhongHopLe(e);
        } catch (IllegalStateException e) {
            baoLoi(e);
        }
    }

    // Cap nhat dich vu
    private void capNhatDichVu() {

        if (maDVDangChon <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay chon dich vu can cap nhat trong bang."
            );
            return;
        }

        try {
            LoaiDichVu loai =
                    (LoaiDichVu) cboLoaiDV.getSelectedItem();
            if (loai == null) {
                throw new IllegalArgumentException(
                        "Hay chon loai dich vu.");
            }

            if (controller.capNhatDichVu(
                    maDVDangChon,
                    loai.getMaLoaiDV(),
                    txtTenDV.getText(),
                    txtDonGia.getText(),
                    txtDonViTinh.getText(),
                    txtMoTa.getText(),
                    chkTrangThai.isSelected())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cap nhat dich vu thanh cong!"
                );

                xoaTrangForm();
                taiDanhSach();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Khong tim thay dich vu de cap nhat."
                );
            }

        } catch (IllegalArgumentException e) {
            baoDuLieuKhongHopLe(e);
        } catch (IllegalStateException e) {
            baoLoi(e);
        }
    }

    // Xoa mem: chuyen TrangThai ve 0
    private void ngungHoatDong() {

        if (maDVDangChon <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay chon dich vu can ngung hoat dong."
            );
            return;
        }

        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Dich vu se duoc chuyen sang ngung hoat dong. "
                + "Ban co chac khong?",
                "Xac nhan ngung hoat dong",
                JOptionPane.YES_NO_OPTION
        );

        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controller.ngungHoatDong(maDVDangChon)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Da ngung hoat dong dich vu."
                );

                xoaTrangForm();
                taiDanhSach();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Dich vu da ngung hoat dong "
                        + "hoac khong ton tai."
                );
            }

        } catch (IllegalArgumentException e) {
            baoDuLieuKhongHopLe(e);
        } catch (IllegalStateException e) {
            baoLoi(e);
        }
    }

    // Tim kiem theo ten dich vu
    private void timKiemDichVu() {

        String tuKhoa = txtTimKiem.getText().trim();

        try {
            hienThiDanhSach(controller.timTheoTen(tuKhoa));

            xoaTrangForm();

        } catch (IllegalArgumentException e) {
            baoDuLieuKhongHopLe(e);
        } catch (IllegalStateException e) {
            baoLoi(e);
        }
    }

    // Xoa cac o nhap lieu
    private void xoaTrangForm() {

        maDVDangChon = -1;

        txtMaDV.setText("");
        if (cboLoaiDV.getItemCount() > 0) {
    cboLoaiDV.setSelectedIndex(0);
}
        txtTenDV.setText("");
        txtDonGia.setText("");
        txtDonViTinh.setText("");
        txtMoTa.setText("");

        chkTrangThai.setSelected(true);

        tblDichVu.clearSelection();
    }

    private String giaTriChuoi(Object value) {
        return value == null ? "" : value.toString();
    }

    private void baoDuLieuKhongHopLe(IllegalArgumentException e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Du lieu khong hop le",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void baoLoi(IllegalStateException e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Loi database",
                JOptionPane.ERROR_MESSAGE
        );
    }

}

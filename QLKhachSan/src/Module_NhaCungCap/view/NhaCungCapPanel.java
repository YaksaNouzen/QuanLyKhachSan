package Module_NhaCungCap.view;

import Module_NhaCungCap.controller.NhaCungCapController;
import Module_NhaCungCap.model.NhaCungCap;
import common.kho.ui.GiaoDien;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class NhaCungCapPanel extends JPanel {
    private final NhaCungCapController controller = new NhaCungCapController();
    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtDienThoai = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDiaChi = new JTextField();
    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{"Đang hoạt động", "Ngừng hoạt động"});
    private final JTextArea txtGhiChu = new JTextArea(2, 20);
    private final JTextField txtTim = new JTextField(20);
    private final JComboBox<String> cboLoc = new JComboBox<>(
            new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"});
    private final JButton btnDoiTrangThai = new JButton("Ngừng hoạt động");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã", "Tên nhà cung cấp", "Điện thoại", "Email",
                "Địa chỉ", "Trạng thái", "Ghi chú"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public NhaCungCapPanel() {
        taoGiaoDien();
    }

    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(GiaoDien.NEN);
        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        JLabel title = GiaoDien.tieuDe("QUẢN LÝ NHÀ CUNG CẤP");
        title.setAlignmentX(CENTER_ALIGNMENT);
        north.add(title);
        north.add(Box.createVerticalStrut(12));
        north.add(taoForm());
        north.add(Box.createVerticalStrut(10));
        north.add(taoBoLoc());
        add(north, BorderLayout.NORTH);
        table.setRowHeight(27);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) hienDongDangChon();
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JPanel taoForm() {
        JPanel wrapper = GiaoDien.panelTrangTri();
        wrapper.setLayout(new BorderLayout(8, 8));
        wrapper.add(new JLabel("Thông tin nhà cung cấp"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.add(new JLabel("Mã NCC:"), GiaoDien.rangBuoc(0, 0));
        form.add(txtMa, GiaoDien.rangBuoc(1, 0));
        form.add(new JLabel("Tên NCC:"), GiaoDien.rangBuoc(2, 0));
        form.add(txtTen, GiaoDien.rangBuoc(3, 0));
        form.add(new JLabel("Điện thoại:"), GiaoDien.rangBuoc(0, 1));
        form.add(txtDienThoai, GiaoDien.rangBuoc(1, 1));
        form.add(new JLabel("Email:"), GiaoDien.rangBuoc(2, 1));
        form.add(txtEmail, GiaoDien.rangBuoc(3, 1));
        form.add(new JLabel("Địa chỉ:"), GiaoDien.rangBuoc(0, 2));
        form.add(txtDiaChi, GiaoDien.rangBuoc(1, 2));
        form.add(new JLabel("Trạng thái:"), GiaoDien.rangBuoc(2, 2));
        form.add(cboTrangThai, GiaoDien.rangBuoc(3, 2));
        form.add(new JLabel("Ghi chú:"), GiaoDien.rangBuoc(0, 3));
        var gbc = GiaoDien.rangBuoc(1, 3); gbc.gridwidth = 3;
        form.add(new JScrollPane(txtGhiChu), gbc);
        wrapper.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setBackground(Color.WHITE);
        JButton them = new JButton("Thêm");
        JButton sua = new JButton("Sửa");
        JButton lichSu = new JButton("Lịch sử cung cấp");
        JButton moi = new JButton("Làm mới");
        btnDoiTrangThai.setEnabled(false);
        buttons.add(them); buttons.add(sua); buttons.add(btnDoiTrangThai);
        buttons.add(lichSu); buttons.add(moi);
        wrapper.add(buttons, BorderLayout.SOUTH);
        them.addActionListener(e -> thucHien(() -> controller.them(docForm()), "Đã thêm nhà cung cấp."));
        sua.addActionListener(e -> thucHien(() -> controller.capNhat(docForm()), "Đã cập nhật nhà cung cấp."));
        btnDoiTrangThai.addActionListener(e -> doiTrangThai());
        lichSu.addActionListener(e -> hienLichSu());
        moi.addActionListener(e -> { xoaForm(); taiDuLieu(); });
        return wrapper;
    }

    private JPanel taoBoLoc() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 232)));
        JButton tim = new JButton("Tìm kiếm");
        panel.add(new JLabel("Từ khóa:")); panel.add(txtTim);
        panel.add(new JLabel("Trạng thái:")); panel.add(cboLoc); panel.add(tim);
        tim.addActionListener(e -> taiDuLieu());
        txtTim.addActionListener(e -> taiDuLieu());
        cboLoc.addActionListener(e -> taiDuLieu());
        return panel;
    }

    public void taiDuLieu() {
        try {
            List<NhaCungCap> ds = controller.timKiem(txtTim.getText(),
                    (String) cboLoc.getSelectedItem());
            model.setRowCount(0);
            for (NhaCungCap ncc : ds) {
                model.addRow(new Object[]{ncc.getMaNhaCungCap(),
                    ncc.getTenNhaCungCap(), ncc.getSoDienThoai(), ncc.getEmail(),
                    ncc.getDiaChi(), ncc.getTrangThai(), ncc.getGhiChu()});
            }
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private NhaCungCap docForm() {
        return new NhaCungCap(txtMa.getText().trim(), txtTen.getText().trim(),
                txtDienThoai.getText().trim(), txtEmail.getText().trim(),
                txtDiaChi.getText().trim(), (String) cboTrangThai.getSelectedItem(),
                txtGhiChu.getText().trim());
    }

    private void hienDongDangChon() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        row = table.convertRowIndexToModel(row);
        txtMa.setText(chuoi(row, 0)); txtTen.setText(chuoi(row, 1));
        txtDienThoai.setText(chuoi(row, 2)); txtEmail.setText(chuoi(row, 3));
        txtDiaChi.setText(chuoi(row, 4)); cboTrangThai.setSelectedItem(chuoi(row, 5));
        txtGhiChu.setText(chuoi(row, 6)); txtMa.setEditable(false);
        boolean dangHoatDong = "Đang hoạt động".equals(chuoi(row, 5));
        btnDoiTrangThai.setText(dangHoatDong
                ? "Ngừng hoạt động" : "Kích hoạt lại");
        btnDoiTrangThai.setEnabled(true);
    }

    private String chuoi(int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private void hienLichSu() {
        String ma = txtMa.getText().trim();
        if (ma.isBlank()) {
            GiaoDien.baoLoi(this, new IllegalArgumentException("Hãy chọn nhà cung cấp."));
            return;
        }
        try {
            NumberFormat money = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
            DefaultTableModel modelPhieu = new DefaultTableModel(
                    new String[]{"Mã phiếu", "Ngày nhập", "Tổng tiền", "Trạng thái"}, 0);
            for (Object[] row : controller.lichSuNhap(ma)) {
                modelPhieu.addRow(new Object[]{row[0], row[1],
                    money.format((BigDecimal) row[2]), row[3]});
            }
            DefaultTableModel modelVatTu = new DefaultTableModel(
                    new String[]{"Mã vật tư", "Tên vật tư", "Tổng số lượng"}, 0);
            for (Object[] row : controller.vatTuDaCungCap(ma)) modelVatTu.addRow(row);
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Phiếu nhập", new JScrollPane(new JTable(modelPhieu)));
            tabs.addTab("Vật tư đã cung cấp", new JScrollPane(new JTable(modelVatTu)));
            JDialog dialog = new JDialog((java.awt.Frame) null,
                    "Lịch sử - " + ma, true);
            dialog.add(tabs); dialog.setSize(new Dimension(750, 430));
            dialog.setLocationRelativeTo(this); dialog.setVisible(true);
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void doiTrangThai() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GiaoDien.baoLoi(this,
                    new IllegalArgumentException("Hãy chọn nhà cung cấp."));
            return;
        }
        row = table.convertRowIndexToModel(row);
        String ma = chuoi(row, 0);
        String hienTai = chuoi(row, 5);
        String trangThaiMoi = "Đang hoạt động".equals(hienTai)
                ? "Ngừng hoạt động" : "Đang hoạt động";
        String thaoTac = "Đang hoạt động".equals(trangThaiMoi)
                ? "kích hoạt lại" : "ngừng hoạt động";

        int luaChon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn " + thaoTac + " nhà cung cấp " + ma + "?",
                "Xác nhận thay đổi trạng thái",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        thucHien(() -> controller.capNhatTrangThai(ma, trangThaiMoi),
                "Đã " + thaoTac + " nhà cung cấp.");
    }

    private void thucHien(HanhDong action, String message) {
        try {
            action.chay(); JOptionPane.showMessageDialog(this, message);
            xoaForm(); taiDuLieu();
        } catch (Exception e) { GiaoDien.baoLoi(this, e); }
    }

    private void xoaForm() {
        txtMa.setEditable(true); txtMa.setText(""); txtTen.setText("");
        txtDienThoai.setText(""); txtEmail.setText(""); txtDiaChi.setText("");
        txtGhiChu.setText(""); cboTrangThai.setSelectedIndex(0); table.clearSelection();
        btnDoiTrangThai.setText("Ngừng hoạt động");
        btnDoiTrangThai.setEnabled(false);
    }

    @FunctionalInterface private interface HanhDong { void chay() throws Exception; }
}

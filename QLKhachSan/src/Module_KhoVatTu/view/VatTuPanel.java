package Module_KhoVatTu.view;

import Module_KhoVatTu.controller.VatTuController;
import Module_KhoVatTu.model.VatTu;
import common.kho.ui.GiaoDien;
import common.ui.ColoredButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class VatTuPanel extends JPanel {
    private final VatTuController controller = new VatTuController();
    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtDonVi = new JTextField();
    private final JTextField txtTon = new JTextField("0");
    private final JTextField txtToiThieu = new JTextField("0");
    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{"Đang sử dụng", "Ngừng sử dụng"});
    private final JTextArea txtMoTa = new JTextArea(2, 20);
    private final JTextField txtTim = new JTextField(18);
    private final JComboBox<String> cboLoc = new JComboBox<>(
            new String[]{"Tất cả", "Đang sử dụng", "Ngừng sử dụng"});
    private final JCheckBox chkSapHet = new JCheckBox("Chỉ vật tư sắp hết");
    private final ColoredButton btnDoiTrangThai = GiaoDien.nut(
            "Ngừng sử dụng", GiaoDien.CAM, Color.WHITE);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã", "Tên vật tư", "Đơn vị", "Tồn kho",
                "Tồn tối thiểu", "Trạng thái", "Cảnh báo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public VatTuPanel() {
        taoGiaoDien();
    }

    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(GiaoDien.NEN);

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        JLabel title = GiaoDien.tieuDe("QUẢN LÝ VẬT TƯ");
        title.setAlignmentX(CENTER_ALIGNMENT);
        north.add(title);
        north.add(Box.createVerticalStrut(12));
        north.add(taoForm());
        north.add(Box.createVerticalStrut(10));
        north.add(taoBoLoc());
        add(north, BorderLayout.NORTH);

        table.setRowHeight(27);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) hienDongDangChon();
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JPanel taoForm() {
        JPanel wrapper = GiaoDien.panelTrangTri();
        wrapper.setLayout(new BorderLayout(8, 8));
        wrapper.add(new JLabel("Thông tin vật tư"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        txtTon.setEditable(false);
        txtMoTa.setLineWrap(true);
        form.add(new JLabel("Mã vật tư:"), GiaoDien.rangBuoc(0, 0));
        form.add(txtMa, GiaoDien.rangBuoc(1, 0));
        form.add(new JLabel("Tên vật tư:"), GiaoDien.rangBuoc(2, 0));
        form.add(txtTen, GiaoDien.rangBuoc(3, 0));
        form.add(new JLabel("Đơn vị tính:"), GiaoDien.rangBuoc(0, 1));
        form.add(txtDonVi, GiaoDien.rangBuoc(1, 1));
        form.add(new JLabel("Tồn kho:"), GiaoDien.rangBuoc(2, 1));
        form.add(txtTon, GiaoDien.rangBuoc(3, 1));
        form.add(new JLabel("Tồn tối thiểu:"), GiaoDien.rangBuoc(0, 2));
        form.add(txtToiThieu, GiaoDien.rangBuoc(1, 2));
        form.add(new JLabel("Trạng thái:"), GiaoDien.rangBuoc(2, 2));
        form.add(cboTrangThai, GiaoDien.rangBuoc(3, 2));
        form.add(new JLabel("Mô tả:"), GiaoDien.rangBuoc(0, 3));
        var moTaGbc = GiaoDien.rangBuoc(1, 3);
        moTaGbc.gridwidth = 3;
        form.add(new JScrollPane(txtMoTa), moTaGbc);
        wrapper.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setBackground(Color.WHITE);
        JButton btnThem = GiaoDien.nut("Thêm", GiaoDien.XANH_LA, Color.WHITE);
        JButton btnSua = GiaoDien.nut("Sửa", GiaoDien.XANH, Color.WHITE);
        JButton btnXuat = GiaoDien.nut("Xuất kho", GiaoDien.TIM, Color.WHITE);
        JButton btnMoi = GiaoDien.nut("Làm mới", GiaoDien.NHAT, GiaoDien.XANH_DAM);
        btnDoiTrangThai.setEnabled(false);
        buttons.add(btnThem); buttons.add(btnSua); buttons.add(btnDoiTrangThai);
        buttons.add(btnXuat); buttons.add(btnMoi);
        wrapper.add(buttons, BorderLayout.SOUTH);
        btnThem.addActionListener(e -> thucHien(() -> controller.them(docForm()), "Đã thêm vật tư."));
        btnSua.addActionListener(e -> thucHien(() -> controller.capNhat(docForm()), "Đã cập nhật vật tư."));
        btnDoiTrangThai.addActionListener(e -> doiTrangThai());
        btnXuat.addActionListener(e -> xuatKho());
        btnMoi.addActionListener(e -> { xoaForm(); taiDuLieu(); });
        return wrapper;
    }

    private JPanel taoBoLoc() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 232)));
        JButton btnTim = GiaoDien.nut("Tìm kiếm", GiaoDien.XANH, Color.WHITE);
        panel.add(new JLabel("Từ khóa:")); panel.add(txtTim);
        panel.add(new JLabel("Trạng thái:")); panel.add(cboLoc);
        panel.add(chkSapHet); panel.add(btnTim);
        btnTim.addActionListener(e -> taiDuLieu());
        txtTim.addActionListener(e -> taiDuLieu());
        cboLoc.addActionListener(e -> taiDuLieu());
        chkSapHet.addActionListener(e -> taiDuLieu());
        return panel;
    }

    public void taiDuLieu() {
        try {
            List<VatTu> ds = controller.timKiem(txtTim.getText(),
                    (String) cboLoc.getSelectedItem(), chkSapHet.isSelected());
            model.setRowCount(0);
            for (VatTu vt : ds) {
                model.addRow(new Object[]{vt.getMaVatTu(), vt.getTenVatTu(),
                    vt.getDonViTinh(), vt.getSoLuongTon(), vt.getMucTonToiThieu(),
                    vt.getTrangThai(), vt.sapHetHang() ? "Sắp hết" : ""});
            }
        } catch (Exception e) {
            GiaoDien.baoLoi(this, e);
        }
    }

    private VatTu docForm() {
        int toiThieu;
        try { toiThieu = Integer.parseInt(txtToiThieu.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Tồn tối thiểu phải là số nguyên."); }
        return new VatTu(txtMa.getText().trim(), txtTen.getText().trim(),
                txtDonVi.getText().trim(), 0, toiThieu,
                (String) cboTrangThai.getSelectedItem(), txtMoTa.getText().trim());
    }

    private void hienDongDangChon() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        row = table.convertRowIndexToModel(row);
        txtMa.setText(model.getValueAt(row, 0).toString());
        txtTen.setText(model.getValueAt(row, 1).toString());
        txtDonVi.setText(model.getValueAt(row, 2).toString());
        txtTon.setText(model.getValueAt(row, 3).toString());
        txtToiThieu.setText(model.getValueAt(row, 4).toString());
        cboTrangThai.setSelectedItem(model.getValueAt(row, 5));
        txtMa.setEditable(false);
        boolean dangSuDung = "Đang sử dụng".equals(model.getValueAt(row, 5));
        btnDoiTrangThai.setText(dangSuDung
                ? "Ngừng sử dụng" : "Kích hoạt lại");
        Color mau = dangSuDung ? GiaoDien.CAM : GiaoDien.XANH_LA;
        btnDoiTrangThai.setPalette(mau, mau.darker(), Color.WHITE);
        btnDoiTrangThai.setEnabled(true);
    }

    private void doiTrangThai() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GiaoDien.baoLoi(this,
                    new IllegalArgumentException("Hãy chọn vật tư."));
            return;
        }
        row = table.convertRowIndexToModel(row);
        String ma = model.getValueAt(row, 0).toString();
        String hienTai = model.getValueAt(row, 5).toString();
        String trangThaiMoi = "Đang sử dụng".equals(hienTai)
                ? "Ngừng sử dụng" : "Đang sử dụng";
        String thaoTac = "Đang sử dụng".equals(trangThaiMoi)
                ? "kích hoạt lại" : "ngừng sử dụng";

        int luaChon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn " + thaoTac + " vật tư " + ma + "?",
                "Xác nhận thay đổi trạng thái",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (luaChon != JOptionPane.YES_OPTION) {
            return;
        }

        thucHien(() -> controller.capNhatTrangThai(ma, trangThaiMoi),
                "Đã " + thaoTac + " vật tư.");
    }

    private void xuatKho() {
        try {
            String value = JOptionPane.showInputDialog(this, "Số lượng cần xuất:");
            if (value == null) return;
            controller.xuatKho(txtMa.getText().trim(), Integer.parseInt(value.trim()));
            JOptionPane.showMessageDialog(this, "Xuất kho thành công.");
            taiDuLieu();
        } catch (NumberFormatException e) {
            GiaoDien.baoLoi(this, new IllegalArgumentException("Số lượng phải là số nguyên."));
        } catch (Exception e) {
            GiaoDien.baoLoi(this, e);
        }
    }

    private void thucHien(HanhDong action, String message) {
        try {
            action.chay();
            JOptionPane.showMessageDialog(this, message);
            xoaForm();
            taiDuLieu();
        } catch (Exception e) {
            GiaoDien.baoLoi(this, e);
        }
    }

    private void xoaForm() {
        txtMa.setEditable(true);
        txtMa.setText(""); txtTen.setText(""); txtDonVi.setText("");
        txtTon.setText("0"); txtToiThieu.setText("0"); txtMoTa.setText("");
        cboTrangThai.setSelectedIndex(0); table.clearSelection();
        btnDoiTrangThai.setText("Ngừng sử dụng");
        btnDoiTrangThai.setPalette(GiaoDien.CAM, GiaoDien.CAM.darker(), Color.WHITE);
        btnDoiTrangThai.setEnabled(false);
    }

    @FunctionalInterface private interface HanhDong { void chay() throws Exception; }
}

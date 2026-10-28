package module.baotri.view;

import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import module.baotri.controller.BaoTriController;
import module.baotri.model.MucDoUuTien;
import module.baotri.model.PhieuBaoTri;
import module.baotri.model.TrangThaiBaoTri;
import module.phong.controller.PhongController;
import module.phong.model.Phong;

public class BaoTriPanel extends JPanel {
    private final BaoTriController controller;
    private final PhongController phongController;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {"Ma phieu", "Ma phong", "So phong", "Noi dung", "Muc do",
                    "Nguoi bao", "Phu trach", "Trang thai", "Ngay bao", "Bat dau",
                    "Ket thuc", "Chi phi", "Ket qua"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JComboBox<Object> cboTrangThai = new JComboBox<>();
    private final JComboBox<String> cboPhong = new JComboBox<>();
    private final JButton btnBatDau = new JButton("Bat dau sua");
    private final JButton btnHoanTat = new JButton("Hoan tat");
    private final JButton btnHuy = new JButton("Huy bao hong");
    private boolean loading;

    public BaoTriPanel(BaoTriController controller, PhongController phongController) {
        this.controller = controller;
        this.phongController = phongController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Bao tri phong");
        title.setFont(title.getFont().deriveFont(20f));
        top.add(title, BorderLayout.NORTH);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboTrangThai.addItem("Tat ca trang thai");
        for (TrangThaiBaoTri state : TrangThaiBaoTri.values()) cboTrangThai.addItem(state);
        filters.add(new JLabel("Trang thai:"));
        filters.add(cboTrangThai);
        filters.add(new JLabel("Ma phong:"));
        filters.add(cboPhong);
        top.add(filters, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(nut("Bao hong", this::baoHong));
        actions.add(btnBatDau);
        actions.add(btnHoanTat);
        actions.add(btnHuy);
        actions.add(nut("Tai lai", controller::refresh));
        top.add(actions, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        HotelTheme.table(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        cboTrangThai.addActionListener(event -> taiBang());
        cboPhong.addActionListener(event -> taiBang());
        table.getSelectionModel().addListSelectionListener(event -> capNhatNut());
        btnBatDau.addActionListener(event -> batDau());
        btnHoanTat.addActionListener(event -> hoanTat());
        btnHuy.addActionListener(event -> huy());
        controller.onChange(this::taiBang);
        controller.onError(message -> thongBao(message));
        controller.onBusy(value -> setCursor(java.awt.Cursor.getPredefinedCursor(
                value ? java.awt.Cursor.WAIT_CURSOR : java.awt.Cursor.DEFAULT_CURSOR)));
        phongController.onChange(this::napPhong);
        napPhong();
        controller.refresh();
    }

    private JButton nut(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void napPhong() {
        loading = true;
        String cu = (String) cboPhong.getSelectedItem();
        cboPhong.removeAllItems();
        cboPhong.addItem("Tat ca phong");
        for (Phong phong : phongController.rooms()) cboPhong.addItem(phong.maPhong());
        if (cu != null) cboPhong.setSelectedItem(cu);
        loading = false;
        taiBang();
    }

    private void taiBang() {
        if (loading) return;
        model.setRowCount(0);
        Object state = cboTrangThai.getSelectedItem();
        String maPhong = (String) cboPhong.getSelectedItem();
        for (PhieuBaoTri phieu : controller.phieus()) {
            if (state instanceof TrangThaiBaoTri selected && selected != phieu.trangThai()) continue;
            if (maPhong != null && !maPhong.equals("Tat ca phong")
                    && !maPhong.equals(phieu.maPhong())) continue;
            Phong phong = phongController.room(phieu.maPhong());
            model.addRow(new Object[] {phieu.maBaoTri(), phieu.maPhong(),
                    phong == null ? "" : phong.soPhong(), phieu.noiDung(), phieu.mucDo(),
                    phieu.nguoiBao(), phieu.nguoiPhuTrach(), phieu.trangThai(),
                    phieu.ngayBao(), phieu.batDau(), phieu.ketThuc(),
                    phieu.chiPhi(), phieu.ketQua()});
        }
        capNhatNut();
    }

    private void capNhatNut() {
        PhieuBaoTri phieu = chon();
        btnBatDau.setEnabled(phieu != null && phieu.trangThai() == TrangThaiBaoTri.BAO_HONG);
        btnHoanTat.setEnabled(phieu != null && phieu.trangThai() == TrangThaiBaoTri.DANG_SUA);
        btnHuy.setEnabled(phieu != null && phieu.trangThai() == TrangThaiBaoTri.BAO_HONG);
    }

    private PhieuBaoTri chon() {
        int selected = table.getSelectedRow();
        if (selected < 0) return null;
        String ma = model.getValueAt(table.convertRowIndexToModel(selected), 0).toString();
        return controller.phieus().stream().filter(item -> item.maBaoTri().equals(ma))
                .findFirst().orElse(null);
    }

    private void baoHong() {
        JComboBox<Choice> room = new JComboBox<>();
        for (Phong phong : phongController.rooms())
            room.addItem(new Choice(phong.maPhong(), phong.maPhong() + " - " + phong.soPhong()));
        JTextField content = new JTextField();
        JTextField reporter = new JTextField();
        JComboBox<MucDoUuTien> priority = new JComboBox<>(MucDoUuTien.values());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Phong"));
        form.add(room);
        form.add(new JLabel("Noi dung"));
        form.add(content);
        form.add(new JLabel("Nguoi bao"));
        form.add(reporter);
        form.add(new JLabel("Muc do uu tien"));
        form.add(priority);
        if (JOptionPane.showConfirmDialog(this, form, "Ghi nhan bao hong",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        Choice selected = (Choice) room.getSelectedItem();
        if (selected == null) {
            thongBao("Chua co phong de ghi nhan.");
            return;
        }
        controller.baoHong(selected.id(), content.getText(), reporter.getText(),
                (MucDoUuTien) priority.getSelectedItem());
    }

    private void batDau() {
        PhieuBaoTri phieu = chon();
        if (phieu == null) return;
        String owner = JOptionPane.showInputDialog(this, "Nguoi phu trach sua:");
        if (owner != null) controller.batDauSua(phieu.maBaoTri(), owner);
    }

    private void hoanTat() {
        PhieuBaoTri phieu = chon();
        if (phieu == null) return;
        JTextField cost = new JTextField();
        JTextField result = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Chi phi"));
        form.add(cost);
        form.add(new JLabel("Ket qua"));
        form.add(result);
        if (JOptionPane.showConfirmDialog(this, form, "Hoan tat bao tri",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            controller.hoanTat(phieu.maBaoTri(), new BigDecimal(cost.getText().trim()),
                    result.getText());
        } catch (NumberFormatException ex) {
            thongBao("Chi phi phai la so.");
        }
    }

    private void huy() {
        PhieuBaoTri phieu = chon();
        if (phieu != null && JOptionPane.showConfirmDialog(this,
                "Huy phieu " + phieu.maBaoTri() + "?", "Xac nhan",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            controller.huy(phieu.maBaoTri());
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thong tin bao tri",
                JOptionPane.WARNING_MESSAGE);
    }

    private record Choice(String id, String label) {
        public String toString() {
            return label;
        }
    }
}

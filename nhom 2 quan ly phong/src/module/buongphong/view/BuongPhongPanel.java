package module.buongphong.view;

import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
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
import module.buongphong.controller.BuongPhongController;
import module.buongphong.model.CongViecVeSinh;
import module.buongphong.model.TrangThaiVeSinh;
import module.phong.controller.PhongController;
import module.phong.model.Phong;
import module.phong.model.TrangThaiPhong;

public class BuongPhongPanel extends JPanel {
    private final BuongPhongController controller;
    private final PhongController phongController;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {"Ma viec", "Ma phong", "So phong", "Noi dung", "Phu trach",
                    "Trang thai", "Phan cong luc", "Bat dau", "Ket thuc"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JComboBox<Object> cboTrangThai = new JComboBox<>();
    private final JComboBox<String> cboPhong = new JComboBox<>();
    private final JButton btnBatDau = new JButton("Bat dau don");
    private final JButton btnHoanThanh = new JButton("Xac nhan hoan thanh");
    private final JButton btnHuy = new JButton("Huy phan cong");
    private boolean loading;

    public BuongPhongPanel(BuongPhongController controller, PhongController phongController) {
        this.controller = controller;
        this.phongController = phongController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Buong phong - ve sinh");
        title.setFont(title.getFont().deriveFont(20f));
        top.add(title, BorderLayout.NORTH);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboTrangThai.addItem("Tat ca trang thai");
        for (TrangThaiVeSinh state : TrangThaiVeSinh.values()) cboTrangThai.addItem(state);
        filters.add(new JLabel("Trang thai:"));
        filters.add(cboTrangThai);
        filters.add(new JLabel("Ma phong:"));
        filters.add(cboPhong);
        top.add(filters, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(nut("Phan cong don", this::phanCong));
        actions.add(btnBatDau);
        actions.add(btnHoanThanh);
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
        btnBatDau.addActionListener(event -> thaoTacChon(0));
        btnHoanThanh.addActionListener(event -> thaoTacChon(1));
        btnHuy.addActionListener(event -> thaoTacChon(2));
        controller.onChange(this::taiBang);
        controller.onError(message -> JOptionPane.showMessageDialog(
                this, message, "Thong tin buong phong", JOptionPane.WARNING_MESSAGE));
        controller.onBusy(value -> setCursor(java.awt.Cursor.getPredefinedCursor(
                value ? java.awt.Cursor.WAIT_CURSOR : java.awt.Cursor.DEFAULT_CURSOR)));
        phongController.onChange(this::napPhong);
        napPhong();
        controller.refresh();
    }

    private JButton nut(String title, Runnable action) {
        JButton button = new JButton(title);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void napPhong() {
        loading = true;
        String maCu = (String) cboPhong.getSelectedItem();
        cboPhong.removeAllItems();
        cboPhong.addItem("Tat ca phong");
        for (Phong phong : phongController.rooms())
            cboPhong.addItem(phong.maPhong());
        if (maCu != null) cboPhong.setSelectedItem(maCu);
        loading = false;
        taiBang();
    }

    private void taiBang() {
        if (loading) return;
        model.setRowCount(0);
        Object state = cboTrangThai.getSelectedItem();
        String room = (String) cboPhong.getSelectedItem();
        for (CongViecVeSinh job : controller.congViecs()) {
            if (state instanceof TrangThaiVeSinh selected && selected != job.trangThai()) continue;
            if (room != null && !room.equals("Tat ca phong") && !room.equals(job.maPhong())) continue;
            Phong phong = phongController.room(job.maPhong());
            model.addRow(new Object[] {job.maVeSinh(), job.maPhong(),
                    phong == null ? "" : phong.soPhong(), job.noiDung(), job.nguoiPhuTrach(),
                    job.trangThai(), job.ngayPhanCong(), job.batDau(), job.ketThuc()});
        }
        capNhatNut();
    }

    private void capNhatNut() {
        CongViecVeSinh job = chon();
        btnBatDau.setEnabled(job != null && job.trangThai() == TrangThaiVeSinh.CHO_DON);
        btnHoanThanh.setEnabled(job != null && job.trangThai() == TrangThaiVeSinh.DANG_DON);
        btnHuy.setEnabled(job != null && job.trangThai() == TrangThaiVeSinh.CHO_DON);
    }

    private CongViecVeSinh chon() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String ma = model.getValueAt(table.convertRowIndexToModel(row), 0).toString();
        return controller.congViecs().stream()
                .filter(job -> job.maVeSinh().equals(ma)).findFirst().orElse(null);
    }

    private void phanCong() {
        JComboBox<String> room = new JComboBox<>();
        for (Phong phong : phongController.rooms()) {
            if (!phong.ngungSuDung()
                    && (phong.trangThai() == TrangThaiPhong.TRONG
                    || phong.trangThai() == TrangThaiPhong.DANG_DON))
                room.addItem(phong.maPhong());
        }
        JTextField content = new JTextField();
        JTextField owner = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Phong"));
        form.add(room);
        form.add(new JLabel("Noi dung"));
        form.add(content);
        form.add(new JLabel("Nguoi phu trach"));
        form.add(owner);
        if (JOptionPane.showConfirmDialog(this, form, "Phan cong ve sinh",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        String maPhong = (String) room.getSelectedItem();
        if (maPhong == null) {
            thongBao("Khong co phong phu hop.");
            return;
        }
        controller.phanCong(maPhong, content.getText(), owner.getText());
    }

    private void thaoTacChon(int action) {
        CongViecVeSinh job = chon();
        if (job == null) return;
        if (action == 0) controller.batDauDon(job.maVeSinh());
        if (action == 1) {
            String owner = JOptionPane.showInputDialog(this, "Nguoi xac nhan hoan thanh:");
            if (owner != null) controller.xacNhanHoanThanh(job.maVeSinh(), owner);
        }
        if (action == 2 && JOptionPane.showConfirmDialog(this,
                "Huy phan cong " + job.maVeSinh() + "?", "Xac nhan",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            controller.huyPhanCong(job.maVeSinh());
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thong tin", JOptionPane.WARNING_MESSAGE);
    }
}

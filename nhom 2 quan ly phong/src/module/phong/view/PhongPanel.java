package module.phong.view;

import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import module.phong.controller.PhongController;
import module.phong.model.LoaiPhong;
import module.phong.model.Phong;
import module.phong.model.Tang;
import module.phong.model.TrangThaiPhong;
import static module.phong.model.TrangThaiPhong.TRONG;

public class PhongPanel extends JPanel {
    private final PhongController controller;
    private final JTabbedPane tabPhong = new JTabbedPane();
    private final JPanel pnlPhong = new JPanel(new BorderLayout(10, 10));
    private final JPanel pnlLoaiPhong = new JPanel(new BorderLayout(10, 10));
    private final JPanel pnlTang = new JPanel(new BorderLayout(10, 10));
    private final JPanel pnlSoDoPhong = new JPanel(new GridLayout(0, 4, 12, 12));
    private final DefaultTableModel roomModel = taoModel(
            "Ma phong", "So phong", "Loai phong", "Tang", "Gia/dem", "Trang thai", "Ngung SD");
    private final DefaultTableModel typeModel = taoModel(
            "Ma loai", "Ten loai", "Suc chua", "Gia/dem");
    private final DefaultTableModel floorModel = taoModel("Ma tang", "Ten tang", "So phong");
    private final JTable tblPhong = new JTable(roomModel);
    private final JTable tblLoai = new JTable(typeModel);
    private final JTable tblTang = new JTable(floorModel);
    private final JComboBox<Choice> cboLocTang = new JComboBox<>();
    private final JComboBox<Object> cboLocTrangThai = new JComboBox<>();
    private final JLabel lblTongQuan = new JLabel("Dang tai du lieu...");
    private final List<JButton> actionButtons = new ArrayList<>();
    private boolean loading;

    public PhongPanel(PhongController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        JLabel title = new JLabel("Quan ly phong");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(HotelTheme.INK);
        header.add(title, BorderLayout.NORTH);
        header.add(lblTongQuan, BorderLayout.CENTER);
        JButton btnTaiLai = nut("Tai lai", controller::refresh);
        header.add(btnTaiLai, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        taoTabPhong();
        taoTabLoai();
        taoTabTang();
        taoTabSoDo();
        tabPhong.addTab("Phong", pnlPhong);
        tabPhong.addTab("Loai phong", pnlLoaiPhong);
        tabPhong.addTab("Tang", pnlTang);
        tabPhong.addTab("So do phong", new JScrollPane(pnlSoDoPhong));
        add(tabPhong, BorderLayout.CENTER);

        controller.onChange(this::taiLaiGiaoDien);
        controller.onError(message -> JOptionPane.showMessageDialog(
                this, message, "Kiem tra thong tin", JOptionPane.WARNING_MESSAGE));
        controller.onBusy(value -> {
            actionButtons.forEach(button -> button.setEnabled(!value));
            setCursor(java.awt.Cursor.getPredefinedCursor(
                    value ? java.awt.Cursor.WAIT_CURSOR : java.awt.Cursor.DEFAULT_CURSOR));
        });
        controller.refresh();
    }

    public void taiLai() {
        controller.refresh();
    }

    private void taoTabPhong() {
        cboLocTang.addItem(new Choice("", "Tat ca tang"));
        cboLocTrangThai.addItem("Tat ca trang thai");
        for (TrangThaiPhong state : TrangThaiPhong.values()) cboLocTrangThai.addItem(state);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Tang:"));
        filters.add(cboLocTang);
        filters.add(new JLabel("Trang thai:"));
        filters.add(cboLocTrangThai);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(nut("Them phong", () -> suaPhong(null)));
        actions.add(nut("Cap nhat", this::capNhatPhong));
        actions.add(nut("Ngung su dung", this::ngungPhong));
        actions.add(nut("Tai lai", controller::refresh));
        JPanel top = new JPanel(new BorderLayout());
        top.add(filters, BorderLayout.NORTH);
        top.add(actions, BorderLayout.SOUTH);
        pnlPhong.add(top, BorderLayout.NORTH);
        cauHinhBang(tblPhong);
        pnlPhong.add(new JScrollPane(tblPhong), BorderLayout.CENTER);
        cboLocTang.addActionListener(event -> locPhongTheoBoLoc());
        cboLocTrangThai.addActionListener(event -> locPhongTheoBoLoc());
    }

    private void taoTabLoai() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(nut("Them loai", () -> suaLoai(null)));
        actions.add(nut("Cap nhat", this::capNhatLoai));
        actions.add(nut("Xoa loai", this::xoaLoai));
        pnlLoaiPhong.add(actions, BorderLayout.NORTH);
        cauHinhBang(tblLoai);
        pnlLoaiPhong.add(new JScrollPane(tblLoai), BorderLayout.CENTER);
    }

    private void taoTabTang() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(nut("Them tang", () -> suaTang(null)));
        actions.add(nut("Cap nhat", this::capNhatTang));
        actions.add(nut("Xoa tang", this::xoaTang));
        pnlTang.add(actions, BorderLayout.NORTH);
        cauHinhBang(tblTang);
        pnlTang.add(new JScrollPane(tblTang), BorderLayout.CENTER);
    }

    private void taoTabSoDo() {
        pnlSoDoPhong.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));
        cboLocTang.setPreferredSize(new Dimension(160, 28));
        cboLocTrangThai.setPreferredSize(new Dimension(180, 28));
        pnlSoDoPhong.setToolTipText("Mau the hien thi trang thai hien tai cua phong.");
    }

    private void taiLaiGiaoDien() {
        loading = true;
        String tangCu = maDangChon(cboLocTang);
        cboLocTang.removeAllItems();
        cboLocTang.addItem(new Choice("", "Tat ca tang"));
        for (Tang tang : controller.floors())
            cboLocTang.addItem(new Choice(tang.maTang(), tang.tenTang()));
        chonTang(tangCu);
        loading = false;
        capNhatBangLoai();
        capNhatBangTang();
        locDanhSach();
        capNhatSoDo();
        long trong = controller.rooms().stream().filter(p -> p.trangThai() == TRONG).count();
        lblTongQuan.setText(controller.rooms().size() + " phong, " + trong
                + " phong trong, " + controller.floors().size() + " tang");
    }

    private void chonTang(String maTang) {
        for (int i = 0; i < cboLocTang.getItemCount(); i++) {
            Choice choice = cboLocTang.getItemAt(i);
            if (choice.id().equals(maTang)) cboLocTang.setSelectedIndex(i);
        }
    }

    private void locDanhSach() {
        if (loading) return;
        roomModel.setRowCount(0);
        String maTang = maDangChon(cboLocTang);
        Object selected = cboLocTrangThai.getSelectedItem();
        for (Phong phong : controller.rooms()) {
            if (!maTang.isBlank() && !maTang.equals(phong.maTang())) continue;
            if (selected instanceof TrangThaiPhong state && state != phong.trangThai()) continue;
            LoaiPhong loai = controller.type(phong.maLoaiPhong());
            Tang tang = controller.floor(phong.maTang());
            BigDecimal gia = phong.giaPhong() == null ? loai.giaTheoNgay() : phong.giaPhong();
            roomModel.addRow(new Object[] {phong.maPhong(), phong.soPhong(), loai.tenLoai(),
                    tang.tenTang(), gia, phong.trangThai(), phong.ngungSuDung() ? "Co" : "Khong"});
        }
    }

    private void locPhongTheoBoLoc() {
        locDanhSach();
        capNhatSoDo();
    }

    private void capNhatBangLoai() {
        typeModel.setRowCount(0);
        for (LoaiPhong loai : controller.types())
            typeModel.addRow(new Object[] {loai.maLoaiPhong(), loai.tenLoai(),
                    loai.soNguoiToiDa(), loai.giaTheoNgay()});
    }

    private void capNhatBangTang() {
        floorModel.setRowCount(0);
        for (Tang tang : controller.floors()) {
            long soPhong = controller.rooms().stream()
                    .filter(phong -> phong.maTang().equals(tang.maTang())).count();
            floorModel.addRow(new Object[] {tang.maTang(), tang.tenTang(), soPhong});
        }
    }

    private void capNhatSoDo() {
        pnlSoDoPhong.removeAll();
        String maTang = maDangChon(cboLocTang);
        Object selected = cboLocTrangThai.getSelectedItem();
        for (Phong phong : controller.rooms()) {
            if (!maTang.isBlank() && !maTang.equals(phong.maTang())) continue;
            if (selected instanceof TrangThaiPhong state && state != phong.trangThai()) continue;
            JPanel card = new JPanel(new BorderLayout(0, 8));
            card.setBackground(Color.WHITE);
            Color mauVien = phong.ngungSuDung() ? Color.GRAY : mau(phong.trangThai());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(mauVien, 2),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            JLabel room = new JLabel("Phong " + phong.soPhong());
            room.setFont(room.getFont().deriveFont(Font.BOLD, 16f));
            JLabel type = new JLabel(controller.type(phong.maLoaiPhong()).tenLoai());
            JLabel state = new JLabel(phong.ngungSuDung()
                    ? "Ngung su dung" : phong.trangThai().toString());
            card.add(room, BorderLayout.NORTH);
            card.add(type, BorderLayout.CENTER);
            card.add(state, BorderLayout.SOUTH);
            card.setPreferredSize(new Dimension(190, 112));
            pnlSoDoPhong.add(card);
        }
        pnlSoDoPhong.revalidate();
        pnlSoDoPhong.repaint();
    }

    private Color mau(TrangThaiPhong state) {
        return switch (state) {
            case TRONG -> new Color(33, 139, 111);
            case DA_DAT -> new Color(117, 86, 179);
            case DANG_SU_DUNG -> new Color(56, 111, 175);
            case DANG_DON -> new Color(185, 132, 39);
            case BAO_TRI -> new Color(164, 76, 78);
        };
    }

    private void suaPhong(Phong current) {
        JTextField ma = new JTextField(current == null ? "" : current.maPhong());
        JTextField so = new JTextField(current == null ? "" : current.soPhong());
        JTextField gia = new JTextField(current == null || current.giaPhong() == null
                ? "" : current.giaPhong().toPlainString());
        JComboBox<Choice> loai = new JComboBox<>();
        JComboBox<Choice> tang = new JComboBox<>();
        for (LoaiPhong value : controller.types())
            loai.addItem(new Choice(value.maLoaiPhong(), value.tenLoai()));
        for (Tang value : controller.floors())
            tang.addItem(new Choice(value.maTang(), value.tenTang()));
        if (current != null) {
            chon(loai, current.maLoaiPhong());
            chon(tang, current.maTang());
        }
        ma.setEditable(current == null);
        JPanel form = form(new String[] {"Ma phong", "So phong", "Loai phong", "Tang", "Gia rieng (de trong de dung gia loai)"},
                new JComponent[] {ma, so, loai, tang, gia});
        if (JOptionPane.showConfirmDialog(this, form, "Thong tin phong",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Choice loaiChon = (Choice) loai.getSelectedItem();
            Choice tangChon = (Choice) tang.getSelectedItem();
            Phong phong = new Phong(ma.getText().trim().toUpperCase(), so.getText().trim(),
                    loaiChon == null ? null : loaiChon.id(),
                    tangChon == null ? null : tangChon.id(),
                    gia.getText().isBlank() ? null : new BigDecimal(gia.getText().trim()),
                    current == null ? TRONG : current.trangThai(),
                    current != null && current.ngungSuDung());
            controller.saveRoom(phong, current != null, () -> {});
        } catch (NumberFormatException ex) {
            thongBao("Gia phong phai la so.");
        }
    }

    private void capNhatPhong() {
        Phong current = phongDangChon();
        if (current == null) {
            thongBao("Chon mot phong de cap nhat.");
            return;
        }
        suaPhong(current);
    }

    private void suaLoai(LoaiPhong current) {
        JTextField ma = new JTextField(current == null ? "" : current.maLoaiPhong());
        JTextField ten = new JTextField(current == null ? "" : current.tenLoai());
        JTextField sucChua = new JTextField(current == null ? "" : "" + current.soNguoiToiDa());
        JTextField gia = new JTextField(current == null ? "" : current.giaTheoNgay().toPlainString());
        ma.setEditable(current == null);
        JPanel form = form(new String[] {"Ma loai", "Ten loai", "Suc chua", "Gia theo ngay"},
                new JComponent[] {ma, ten, sucChua, gia});
        if (JOptionPane.showConfirmDialog(this, form, "Loai phong",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            controller.saveType(new LoaiPhong(ma.getText().trim().toUpperCase(),
                    ten.getText().trim(), Integer.parseInt(sucChua.getText().trim()),
                    new BigDecimal(gia.getText().trim())), current != null, () -> {});
        } catch (NumberFormatException ex) {
            thongBao("Suc chua va gia phai la so.");
        }
    }

    private void capNhatLoai() {
        LoaiPhong current = loaiDangChon();
        if (current == null) {
            thongBao("Chon mot loai phong de cap nhat.");
            return;
        }
        suaLoai(current);
    }

    private void suaTang(Tang current) {
        JTextField ma = new JTextField(current == null ? "" : current.maTang());
        JTextField ten = new JTextField(current == null ? "" : current.tenTang());
        ma.setEditable(current == null);
        JPanel form = form(new String[] {"Ma tang", "Ten tang"}, new JComponent[] {ma, ten});
        if (JOptionPane.showConfirmDialog(this, form, "Tang",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            controller.saveFloor(new Tang(ma.getText().trim().toUpperCase(), ten.getText().trim()),
                    current != null, () -> {});
    }

    private void capNhatTang() {
        Tang current = tangDangChon();
        if (current == null) {
            thongBao("Chon mot tang de cap nhat.");
            return;
        }
        suaTang(current);
    }

    private void ngungPhong() {
        Phong phong = phongDangChon();
        if (phong == null) return;
        if (JOptionPane.showConfirmDialog(this, "Ngung su dung phong " + phong.soPhong() + "?",
                "Xac nhan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            controller.disableRoom(phong.maPhong(), () -> {});
    }

    private void xoaLoai() {
        String ma = id(tblLoai);
        if (ma != null) controller.deleteType(ma, () -> {});
    }

    private void xoaTang() {
        String ma = id(tblTang);
        if (ma != null) controller.deleteFloor(ma, () -> {});
    }

    private Phong phongDangChon() {
        String ma = id(tblPhong);
        return ma == null ? null : controller.room(ma);
    }

    private LoaiPhong loaiDangChon() {
        String ma = id(tblLoai);
        return ma == null ? null : controller.type(ma);
    }

    private Tang tangDangChon() {
        String ma = id(tblTang);
        return ma == null ? null : controller.floor(ma);
    }

    private String id(JTable table) {
        int selected = table.getSelectedRow();
        return selected < 0 ? null : table.getModel().getValueAt(
                table.convertRowIndexToModel(selected), 0).toString();
    }

    private JButton nut(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(event -> action.run());
        if (!label.equals("Tai lai")) actionButtons.add(button);
        return button;
    }

    private void cauHinhBang(JTable table) {
        HotelTheme.table(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private static DefaultTableModel taoModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel form(String[] labels, JComponent[] fields) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            panel.add(fields[i]);
        }
        return panel;
    }

    private void chon(JComboBox<Choice> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++)
            if (combo.getItemAt(i).id().equals(id)) combo.setSelectedIndex(i);
    }

    private String maDangChon(JComboBox<Choice> combo) {
        Choice choice = (Choice) combo.getSelectedItem();
        return choice == null ? "" : choice.id();
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thong tin", JOptionPane.WARNING_MESSAGE);
    }

    private record Choice(String id, String ten) {
        public String toString() {
            return ten;
        }
    }
}

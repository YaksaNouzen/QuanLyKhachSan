package module.baotri.view;

import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import module.baotri.controller.BaoTriController;
import module.baotri.model.MucDoUuTien;
import module.baotri.model.PhieuBaoTri;
import module.baotri.model.TrangThaiBaoTri;
import module.phong.controller.PhongController;
import module.phong.model.Phong;

/**
 * Giao dien Quan ly bao tri & su co thiet bi phong hien dai.
 */
public class BaoTriPanel extends JPanel {
    private final BaoTriController controller;
    private final PhongController phongController;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {"Mã phiếu", "Mã phòng", "Số phòng", "Nội dung hư hỏng", "Mức độ",
                    "Người báo", "Kỹ thuật phụ trách", "Trạng thái", "Ngày báo", "Bắt đầu",
                    "Kết thúc", "Chi phí", "Kết quả xử lý"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JComboBox<Object> cboTrangThai = new JComboBox<>();
    private final JComboBox<String> cboPhong = new JComboBox<>();

    // Nut thao tac
    private final JButton btnBaoHong;
    private final JButton btnBatDau;
    private final JButton btnHoanTat;
    private final JButton btnHuy;
    private final JButton btnTaiLai;

    // The KPI thong ke
    private final JLabel lblKpiTong = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiChoSua = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiDangSua = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiDaXong = new JLabel("0", SwingConstants.LEFT);

    private boolean loading;

    public BaoTriPanel(BaoTriController controller, PhongController phongController) {
        this.controller = controller;
        this.phongController = phongController;

        this.btnBaoHong = HotelTheme.createButton("+ Báo hỏng thiết bị", HotelTheme.ButtonType.SUCCESS, this::baoHong);
        this.btnBatDau = HotelTheme.createButton("🔧 Bắt đầu sửa", HotelTheme.ButtonType.WARNING, this::batDau);
        this.btnHoanTat = HotelTheme.createButton("✓ Hoàn tất sửa chữa", HotelTheme.ButtonType.SUCCESS, this::hoanTat);
        this.btnHuy = HotelTheme.createButton("✕ Hủy báo hỏng", HotelTheme.ButtonType.DANGER, this::huy);
        this.btnTaiLai = HotelTheme.createButton("Tải lại", HotelTheme.ButtonType.SECONDARY, () -> controller.refresh());

        setLayout(new BorderLayout(0, 14));
        setBackground(HotelTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Top Dashboard
        add(taoTopDashboard(), BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);

        // Bo loc va thao tac duoc tach thanh cac the de de quan sat.
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filters.setOpaque(false);

        cboTrangThai.addItem("Tất cả trạng thái");
        for (TrangThaiBaoTri state : TrangThaiBaoTri.values()) {
            cboTrangThai.addItem(state);
        }
        cboTrangThai.setPreferredSize(new Dimension(160, 32));
        cboPhong.setPreferredSize(new Dimension(160, 32));

        filters.add(new JLabel("Trạng thái:"));
        filters.add(cboTrangThai);
        filters.add(new JLabel("Lọc theo phòng:"));
        filters.add(cboPhong);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actions.setOpaque(false);
        actions.add(btnBaoHong);
        actions.add(btnBatDau);
        actions.add(btnHoanTat);
        actions.add(btnHuy);
        actions.add(btnTaiLai);

        centerPanel.add(taoThe(filters), BorderLayout.NORTH);

        // Bang du lieu
        HotelTheme.table(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        centerPanel.add(taoThe(scroll), BorderLayout.CENTER);
        centerPanel.add(taoThe(actions), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Lang nghe su kien
        cboTrangThai.addActionListener(event -> taiBang());
        cboPhong.addActionListener(event -> taiBang());
        table.getSelectionModel().addListSelectionListener(event -> capNhatNut());

        controller.onChange(this::taiBang);
        controller.onError(message -> thongBao(message));
        controller.onBusy(value -> setCursor(Cursor.getPredefinedCursor(
                value ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR)));

        phongController.onChange(this::napPhong);
        napPhong();
        controller.refresh();
    }

    private JPanel taoTopDashboard() {
        JPanel container = new JPanel(new BorderLayout(0, 12));
        container.setOpaque(false);

        // Header Title
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JLabel title = new JLabel("Quản Lý Bảo Trì & Sự Cố Phòng");
        title.setFont(HotelTheme.FONT_HEADER);
        title.setForeground(HotelTheme.TITLE_BLUE);

        JLabel sub = new JLabel("Tiếp nhận phản ánh hư hỏng, phân công kỹ thuật viên, theo dõi chi phí và nghiệm thu sửa chữa");
        sub.setFont(HotelTheme.FONT_SMALL);
        sub.setForeground(HotelTheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(sub);
        titleBar.add(textPanel, BorderLayout.WEST);
        container.add(titleBar, BorderLayout.NORTH);

        // KPI Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setOpaque(false);

        kpiGrid.add(taoTheKpi("Tổng sự cố", lblKpiTong, "Tất cả phiếu yêu cầu bảo trì", HotelTheme.PRIMARY_BLUE));
        kpiGrid.add(taoTheKpi("Chờ sửa chữa", lblKpiChoSua, "Mới ghi nhận, chờ bố trí kỹ thuật", HotelTheme.DANGER_TEXT));
        kpiGrid.add(taoTheKpi("Đang sửa chữa", lblKpiDangSua, "Kỹ thuật viên đang xử lý", HotelTheme.WARNING_TEXT));
        kpiGrid.add(taoTheKpi("Đã hoàn tất", lblKpiDaXong, "Đã sửa xong và nghiệm thu", HotelTheme.SUCCESS_TEXT));

        container.add(kpiGrid, BorderLayout.SOUTH);
        return container;
    }

    private JPanel taoTheKpi(String tieuDe, JLabel lblGiaTri, String moTa, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new HotelTheme.RoundedBorder(HotelTheme.BORDER, 1, 8),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel title = new JLabel(tieuDe);
        title.setFont(HotelTheme.FONT_SMALL);
        title.setForeground(HotelTheme.TEXT_SECONDARY);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGiaTri.setForeground(accentColor);

        JLabel desc = new JLabel(moTa);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(HotelTheme.TEXT_MUTED);

        card.add(title, BorderLayout.NORTH);
        card.add(lblGiaTri, BorderLayout.CENTER);
        card.add(desc, BorderLayout.SOUTH);
        return card;
    }

    private JPanel taoThe(JComponent noiDung) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HotelTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        card.add(noiDung, BorderLayout.CENTER);
        return card;
    }

    private void napPhong() {
        loading = true;
        String cu = (String) cboPhong.getSelectedItem();
        cboPhong.removeAllItems();
        cboPhong.addItem("Tất cả phòng");
        for (Phong phong : phongController.rooms()) {
            cboPhong.addItem(phong.maPhong());
        }
        if (cu != null) {
            cboPhong.setSelectedItem(cu);
        }
        loading = false;
        taiBang();
    }

    private void taiBang() {
        if (loading) return;
        model.setRowCount(0);
        Object state = cboTrangThai.getSelectedItem();
        String maPhong = (String) cboPhong.getSelectedItem();

        int tong = controller.phieus().size();
        long choSua = 0;
        long dangSua = 0;
        long daXong = 0;

        for (PhieuBaoTri phieu : controller.phieus()) {
            if (phieu.trangThai() == TrangThaiBaoTri.BAO_HONG) choSua++;
            if (phieu.trangThai() == TrangThaiBaoTri.DANG_SUA) dangSua++;
            if (phieu.trangThai() == TrangThaiBaoTri.HOAN_TAT) daXong++;

            if (state instanceof TrangThaiBaoTri selected && selected != phieu.trangThai()) continue;
            if (maPhong != null && !maPhong.equals("Tất cả phòng")
                    && !maPhong.equals(phieu.maPhong())) continue;

            Phong phong = phongController.room(phieu.maPhong());
            String soPhong = phong == null ? "" : phong.soPhong();

            model.addRow(new Object[] {
                    phieu.maBaoTri(),
                    phieu.maPhong(),
                    "Phòng " + soPhong,
                    phieu.noiDung(),
                    phieu.mucDo().name(),
                    phieu.nguoiBao(),
                    phieu.nguoiPhuTrach() == null ? "-" : phieu.nguoiPhuTrach(),
                    phieu.trangThai().name(),
                    phieu.ngayBao(),
                    phieu.batDau(),
                    phieu.ketThuc(),
                    phieu.chiPhi(),
                    phieu.ketQua() == null ? "-" : phieu.ketQua()
            });
        }

        lblKpiTong.setText(tong + " phiếu");
        lblKpiChoSua.setText(choSua + " phiếu");
        lblKpiDangSua.setText(dangSua + " phiếu");
        lblKpiDaXong.setText(daXong + " phiếu");

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
        JComboBox<Choice> roomCombo = new JComboBox<>();
        for (Phong phong : phongController.rooms()) {
            roomCombo.addItem(new Choice(phong.maPhong(), phong.maPhong() + " - Phòng " + phong.soPhong()));
        }

        if (roomCombo.getItemCount() == 0) {
            thongBao("Chưa có dữ liệu phòng để ghi nhận sự cố.");
            return;
        }

        JTextField txtContent = new JTextField("Điều hòa không lạnh / rò rỉ nước");
        JTextField txtReporter = new JTextField();
        JComboBox<MucDoUuTien> cboPriority = new JComboBox<>(MucDoUuTien.values());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblRoom = new JLabel("Chọn phòng hư hỏng:");
        lblRoom.setFont(HotelTheme.FONT_BOLD);
        form.add(lblRoom, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        roomCombo.setPreferredSize(new Dimension(240, 32));
        form.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        JLabel lblContent = new JLabel("Mô tả sự cố:");
        lblContent.setFont(HotelTheme.FONT_BOLD);
        form.add(lblContent, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtContent.setPreferredSize(new Dimension(240, 32));
        form.add(txtContent, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        JLabel lblReporter = new JLabel("Người báo sự cố:");
        lblReporter.setFont(HotelTheme.FONT_BOLD);
        form.add(lblReporter, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtReporter.setPreferredSize(new Dimension(240, 32));
        form.add(txtReporter, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        JLabel lblPriority = new JLabel("Mức độ ưu tiên:");
        lblPriority.setFont(HotelTheme.FONT_BOLD);
        form.add(lblPriority, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cboPriority.setPreferredSize(new Dimension(240, 32));
        form.add(cboPriority, gbc);

        if (JOptionPane.showConfirmDialog(this, form, "Ghi Nhận Sự Cố & Báo Hỏng Thiết Bị",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        Choice selected = (Choice) roomCombo.getSelectedItem();
        if (selected == null) {
            thongBao("Chưa chọn phòng.");
            return;
        }
        controller.baoHong(selected.id(), txtContent.getText(), txtReporter.getText(),
                (MucDoUuTien) cboPriority.getSelectedItem());
    }

    private void batDau() {
        PhieuBaoTri phieu = chon();
        if (phieu == null) return;
        String owner = JOptionPane.showInputDialog(this,
                "Nhập tên kỹ thuật viên phụ trách sửa chữa:",
                "Bắt Đầu Sửa Chữa", JOptionPane.QUESTION_MESSAGE);
        if (owner != null && !owner.isBlank()) {
            controller.batDauSua(phieu.maBaoTri(), owner.trim());
        }
    }

    private void hoanTat() {
        PhieuBaoTri phieu = chon();
        if (phieu == null) return;

        JTextField txtCost = new JTextField("0");
        JTextField txtResult = new JTextField("Đã thay thế linh kiện mới, kiểm tra hoạt động tốt");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblCost = new JLabel("Tổng chi phí sửa chữa (VNĐ):");
        lblCost.setFont(HotelTheme.FONT_BOLD);
        form.add(lblCost, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtCost.setPreferredSize(new Dimension(240, 32));
        form.add(txtCost, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        JLabel lblResult = new JLabel("Kết quả / Ghi chú sửa chữa:");
        lblResult.setFont(HotelTheme.FONT_BOLD);
        form.add(lblResult, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtResult.setPreferredSize(new Dimension(240, 32));
        form.add(txtResult, gbc);

        if (JOptionPane.showConfirmDialog(this, form, "Nghiệm Thu & Hoàn Tất Bảo Trì",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            BigDecimal cost = new BigDecimal(txtCost.getText().trim());
            controller.hoanTat(phieu.maBaoTri(), cost, txtResult.getText());
        } catch (NumberFormatException ex) {
            thongBao("Chi phí sửa chữa phải là số hợp lệ.");
        }
    }

    private void huy() {
        PhieuBaoTri phieu = chon();
        if (phieu != null && JOptionPane.showConfirmDialog(this,
                "Xác nhận hủy phiếu báo hỏng " + phieu.maBaoTri() + " của phòng " + phieu.maPhong() + "?",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.huy(phieu.maBaoTri());
        }
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo bảo trì",
                JOptionPane.WARNING_MESSAGE);
    }

    private record Choice(String id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}

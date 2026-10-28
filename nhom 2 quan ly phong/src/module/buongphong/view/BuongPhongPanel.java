package module.buongphong.view;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import module.buongphong.controller.BuongPhongController;
import module.buongphong.model.CongViecVeSinh;
import module.buongphong.model.TrangThaiVeSinh;
import module.phong.controller.PhongController;
import module.phong.model.Phong;
import module.phong.model.TrangThaiPhong;

/**
 * Giao dien Quan ly buong phong & ve sinh hien dai.
 */
public class BuongPhongPanel extends JPanel {
    private final BuongPhongController controller;
    private final PhongController phongController;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {"Mã việc", "Mã phòng", "Số phòng", "Nội dung dọn dẹp", "Phụ trách",
                    "Trạng thái", "Phân công lúc", "Bắt đầu", "Kết thúc"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JComboBox<Object> cboTrangThai = new JComboBox<>();
    private final JComboBox<String> cboPhong = new JComboBox<>();

    // Nut hanh dong
    private final JButton btnPhanCong;
    private final JButton btnBatDau;
    private final JButton btnHoanThanh;
    private final JButton btnHuy;
    private final JButton btnTaiLai;

    // The KPI thong ke
    private final JLabel lblKpiTong = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiChoDon = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiDangDon = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiXong = new JLabel("0", SwingConstants.LEFT);

    private boolean loading;

    public BuongPhongPanel(BuongPhongController controller, PhongController phongController) {
        this.controller = controller;
        this.phongController = phongController;

        this.btnPhanCong = HotelTheme.createButton("+ Phân công dọn", HotelTheme.ButtonType.SUCCESS, this::phanCong);
        this.btnBatDau = HotelTheme.createButton("▶ Bắt đầu dọn", HotelTheme.ButtonType.WARNING, () -> thaoTacChon(0));
        this.btnHoanThanh = HotelTheme.createButton("✓ Xác nhận hoàn thành", HotelTheme.ButtonType.SUCCESS, () -> thaoTacChon(1));
        this.btnHuy = HotelTheme.createButton("✕ Hủy phân công", HotelTheme.ButtonType.DANGER, () -> thaoTacChon(2));
        this.btnTaiLai = HotelTheme.createButton("Tải lại", HotelTheme.ButtonType.SECONDARY, () -> controller.refresh());
        setLayout(new BorderLayout(0, 14));
        setBackground(HotelTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Top Dashboard: Tieu de + 4 KPI Cards
        add(taoTopDashboard(), BorderLayout.NORTH);

        // Center Content: Toolbar + Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);

        // Bo loc va thao tac duoc tach thanh cac the de de quan sat.
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filters.setOpaque(false);

        cboTrangThai.addItem("Tất cả trạng thái");
        for (TrangThaiVeSinh state : TrangThaiVeSinh.values()) {
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
        actions.add(btnPhanCong);
        actions.add(btnBatDau);
        actions.add(btnHoanThanh);
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
        controller.onError(message -> JOptionPane.showMessageDialog(
                this, message, "Thông báo buồng phòng", JOptionPane.WARNING_MESSAGE));
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

        JLabel title = new JLabel("Quản Lý Buồng Phòng & Vệ Sinh");
        title.setFont(HotelTheme.FONT_HEADER);
        title.setForeground(HotelTheme.TITLE_BLUE);

        JLabel sub = new JLabel("Điều phối công tác dọn phòng, phân công nhân viên và theo dõi tiến độ thời gian thực");
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

        kpiGrid.add(taoTheKpi("Tổng công việc", lblKpiTong, "Tất cả phiếu dọn phòng", HotelTheme.PRIMARY_BLUE));
        kpiGrid.add(taoTheKpi("Chờ dọn dẹp", lblKpiChoDon, "Đã giao, chờ nhân viên bắt đầu", HotelTheme.WARNING_TEXT));
        kpiGrid.add(taoTheKpi("Đang dọn dẹp", lblKpiDangDon, "Nhân viên đang tiến hành dọn", HotelTheme.INFO_TEXT));
        kpiGrid.add(taoTheKpi("Đã hoàn tất", lblKpiXong, "Phòng đã sạch sẵn sàng đón khách", HotelTheme.SUCCESS_TEXT));

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
        String maCu = (String) cboPhong.getSelectedItem();
        cboPhong.removeAllItems();
        cboPhong.addItem("Tất cả phòng");
        for (Phong phong : phongController.rooms()) {
            cboPhong.addItem(phong.maPhong());
        }
        if (maCu != null) {
            cboPhong.setSelectedItem(maCu);
        }
        loading = false;
        taiBang();
    }

    private void taiBang() {
        if (loading) return;
        model.setRowCount(0);
        Object state = cboTrangThai.getSelectedItem();
        String room = (String) cboPhong.getSelectedItem();

        int tong = controller.congViecs().size();
        long choDon = 0;
        long dangDon = 0;
        long daXong = 0;

        for (CongViecVeSinh job : controller.congViecs()) {
            if (job.trangThai() == TrangThaiVeSinh.CHO_DON) choDon++;
            if (job.trangThai() == TrangThaiVeSinh.DANG_DON) dangDon++;
            if (job.trangThai() == TrangThaiVeSinh.HOAN_THANH) daXong++;

            if (state instanceof TrangThaiVeSinh selected && selected != job.trangThai()) continue;
            if (room != null && !room.equals("Tất cả phòng") && !room.equals(job.maPhong())) continue;

            Phong phong = phongController.room(job.maPhong());
            String soPhong = phong == null ? "" : phong.soPhong();

            model.addRow(new Object[] {
                    job.maVeSinh(),
                    job.maPhong(),
                    "Phòng " + soPhong,
                    job.noiDung(),
                    job.nguoiPhuTrach(),
                    job.trangThai().name(),
                    job.ngayPhanCong(),
                    job.batDau(),
                    job.ketThuc()
            });
        }

        lblKpiTong.setText(tong + " việc");
        lblKpiChoDon.setText(choDon + " việc");
        lblKpiDangDon.setText(dangDon + " việc");
        lblKpiXong.setText(daXong + " việc");

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
        JComboBox<String> roomCombo = new JComboBox<>();
        for (Phong phong : phongController.rooms()) {
            if (!phong.ngungSuDung()
                    && (phong.trangThai() == TrangThaiPhong.TRONG
                    || phong.trangThai() == TrangThaiPhong.DANG_DON)) {
                roomCombo.addItem(phong.maPhong() + " (Số " + phong.soPhong() + ")");
            }
        }

        if (roomCombo.getItemCount() == 0) {
            thongBao("Hiện không có phòng nào ở trạng thái Trống hoặc Đang dọn để phân công.");
            return;
        }

        JTextField txtContent = new JTextField("Dọn dẹp phòng tiêu chuẩn và thay ga trải giường");
        JTextField txtOwner = new JTextField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblRoom = new JLabel("Chọn phòng:");
        lblRoom.setFont(HotelTheme.FONT_BOLD);
        form.add(lblRoom, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        roomCombo.setPreferredSize(new Dimension(240, 32));
        form.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        JLabel lblContent = new JLabel("Nội dung công việc:");
        lblContent.setFont(HotelTheme.FONT_BOLD);
        form.add(lblContent, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtContent.setPreferredSize(new Dimension(240, 32));
        form.add(txtContent, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        JLabel lblOwner = new JLabel("Nhân viên phụ trách:");
        lblOwner.setFont(HotelTheme.FONT_BOLD);
        form.add(lblOwner, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtOwner.setPreferredSize(new Dimension(240, 32));
        form.add(txtOwner, gbc);

        if (JOptionPane.showConfirmDialog(this, form, "Phân Công Dọn Dẹp Phòng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String item = (String) roomCombo.getSelectedItem();
        if (item == null) return;
        String maPhong = item.split(" ")[0];
        controller.phanCong(maPhong, txtContent.getText(), txtOwner.getText());
    }

    private void thaoTacChon(int action) {
        CongViecVeSinh job = chon();
        if (job == null) return;

        if (action == 0) {
            controller.batDauDon(job.maVeSinh());
        } else if (action == 1) {
            String owner = JOptionPane.showInputDialog(this,
                    "Nhập tên người kiểm tra / xác nhận hoàn thành:",
                    "Xác Nhận Hoàn Thành", JOptionPane.QUESTION_MESSAGE);
            if (owner != null && !owner.isBlank()) {
                controller.xacNhanHoanThanh(job.maVeSinh(), owner.trim());
            }
        } else if (action == 2) {
            if (JOptionPane.showConfirmDialog(this,
                    "Xác nhận hủy phân công việc dọn " + job.maVeSinh() + " của phòng " + job.maPhong() + "?",
                    "Xác nhận hủy", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                controller.huyPhanCong(job.maVeSinh());
            }
        }
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}


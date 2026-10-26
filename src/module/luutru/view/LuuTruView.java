package module.luutru.view;

import common.ui.TableUi;
import module.luutru.controller.LuuTruController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import module.luutru.model.LuuTru;

public class LuuTruView extends JPanel {
    private static final Color NAVY = new Color(27, 56, 94);
    private static final Color BG = new Color(244, 247, 251);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(106, 120, 138);
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final LuuTruController controller = new LuuTruController();
    private final Runnable onChanged;
    private final JTextField txtTim = new JTextField(20);
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{
        "Tất cả", "Chờ nhận", "Đang ở", "Quá hạn trả", "Đã trả"
    });
    private final DefaultTableModel model;
    private final JTable table;
    private List<LuuTru> rows = new ArrayList<>();

    public LuuTruView(Runnable onChanged) {
        this.onChanged = onChanged;
        setLayout(new BorderLayout(0, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(26, 25, 25, 25));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Quản lý nhận / trả phòng");
        title.setForeground(NAVY);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        JLabel description = new JLabel("Theo dõi khách chờ nhận, đang lưu trú và đã trả phòng");
        description.setForeground(MUTED);
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        titles.add(title); titles.add(Box.createVerticalStrut(7)); titles.add(description);
        add(titles, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
            "Mã đặt", "Khách hàng", "Số điện thoại", "Phòng", "Nhận dự kiến",
            "Trả dự kiến", "Nhận thực tế", "Trả thực tế", "Trạng thái", "Cảnh báo"
        }, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        table = new JTable(model);
        TableUi.centerCellsAndHeader(table);
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }
            @Override
            public Component getTableCellRendererComponent(
                    JTable source, Object value, boolean selected, boolean focused,
                    int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        source, value, selected, focused, row, column);
                if (!selected) {
                    int modelRow = source.convertRowIndexToModel(row);
                    boolean overdue = "QUÁ HẠN TRẢ".equals(
                            model.getValueAt(modelRow, 9));
                    component.setBackground(overdue
                            ? new Color(255, 231, 231)
                            : (row % 2 == 0 ? WHITE : new Color(249, 251, 254)));
                    component.setForeground(overdue
                            ? new Color(166, 28, 28)
                            : new Color(28, 46, 70));
                }
                return component;
            }
        });
        int[] widths = {110, 210, 120, 80, 155, 155, 155, 155, 100, 135};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Tìm mã đơn/khách:")); toolbar.add(txtTim); toolbar.add(cboTrangThai);
        JButton btnTim = button("Tìm", true);
        JButton btnNhan = button("Nhận phòng", false);
        JButton btnTra = button("Trả phòng", false);
        JButton btnLamMoi = button("Làm mới", false);
        toolbar.add(btnTim); toolbar.add(btnNhan); toolbar.add(btnTra); toolbar.add(btnLamMoi);
        card.add(toolbar, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224, 231, 240)));
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        btnTim.addActionListener(e -> refreshData());
        txtTim.addActionListener(e -> refreshData());
        cboTrangThai.addActionListener(e -> refreshData());
        btnLamMoi.addActionListener(e -> { txtTim.setText(""); cboTrangThai.setSelectedIndex(0); refreshData(); });
        btnNhan.addActionListener(e -> execute("GIU_PHONG", "nhận phòng"));
        btnTra.addActionListener(e -> execute("DANG_O", "trả phòng"));
    }

    public void refreshData() {
        try {
            rows = controller.timLuuTru(txtTim.getText(), selectedStatus());
            model.setRowCount(0);
            for (LuuTru item : rows) {
                model.addRow(new Object[]{item.getMaDatPhong(),
                    item.getMaKhachHang() + " - " + item.getHoTen(),
                    item.getSoDienThoai() == null ? "—" : item.getSoDienThoai(),
                    item.getSoPhong(), format(item.getNgayNhanDuKien()),
                    format(item.getNgayTraDuKien()), format(item.getNgayNhan()),
                    format(item.getNgayTra()), item.getTrangThai(),
                    item.isQuaHanTraPhong() ? "QUÁ HẠN TRẢ" : "—"});
            }
        } catch (SQLException ex) {
            error("Không thể tải danh sách lưu trú", ex);
        }
    }

    private void execute(String requiredStatus, String action) {
        LuuTru item = selected();
        if (item == null) return;
        if (!requiredStatus.equals(item.getTrangThai())) {
            JOptionPane.showMessageDialog(this,
                    "Không thể " + action + " khi đơn ở trạng thái " + item.getTrangThai() + ".",
                    "Sai trạng thái", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Xác nhận " + action + " cho đơn " + item.getMaDatPhong() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if ("GIU_PHONG".equals(requiredStatus)) controller.nhanPhong(item.getMaDatPhong());
            else controller.traPhong(item.getMaDatPhong());
            JOptionPane.showMessageDialog(this, "Đã " + action + " thành công.");
            refreshData();
            if (onChanged != null) onChanged.run();
        } catch (SQLException ex) {
            error("Không thể " + action, ex);
            refreshData();
        }
    }

    private LuuTru selected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn.");
            return null;
        }
        return rows.get(table.convertRowIndexToModel(viewRow));
    }

    private String selectedStatus() {
        switch (cboTrangThai.getSelectedIndex()) {
            case 1: return "GIU_PHONG";
            case 2: return "DANG_O";
            case 3: return "QUA_HAN_TRA";
            case 4: return "DA_TRA";
            default: return "";
        }
    }
    private String format(Timestamp value) {
        return value == null ? "—" : value.toLocalDateTime().format(DATE_TIME);
    }
    private JButton button(String label, boolean primary) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(primary ? WHITE : NAVY);
        button.setBackground(primary ? NAVY : new Color(234, 240, 247));
        button.setFocusPainted(false);
        return button;
    }
    private void error(String context, SQLException ex) {
        JOptionPane.showMessageDialog(this, context + ":\n" + ex.getMessage(),
                "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
    }
}

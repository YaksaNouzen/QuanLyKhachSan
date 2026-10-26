package module.khachhang.view;

import common.ui.TableUi;
import module.khachhang.controller.KhachHangController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import module.datphong.model.DatPhong;
import module.khachhang.model.KhachHang;

public class KhachHangView extends JPanel {
    private static final Color NAVY = new Color(27, 56, 94);
    private static final Color BG = new Color(244, 247, 251);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(106, 120, 138);
    private final KhachHangController controller = new KhachHangController();
    private final JTextField txtTim = new JTextField(22);
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{
        "Tất cả", "HOAT_DONG", "NGUNG_HOAT_DONG"
    });
    private final DefaultTableModel model;
    private final JTable table;
    private List<KhachHang> rows = new ArrayList<>();

    public KhachHangView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(26, 25, 25, 25));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Quản lý khách hàng");
        title.setForeground(NAVY);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        JLabel description = new JLabel("Hồ sơ, tra cứu và lịch sử đặt/lưu trú");
        description.setForeground(MUTED);
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        titles.add(title);
        titles.add(Box.createVerticalStrut(7));
        titles.add(description);
        add(titles, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
            "Mã khách", "Họ tên", "Số điện thoại", "Email", "Giấy tờ",
            "Quốc tịch", "Ngày sinh", "Giới tính", "Trạng thái"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        TableUi.centerCellsAndHeader(table);
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        JPanel toolbar = new JPanel();
        toolbar.setOpaque(false);
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setOpaque(false);
        searchRow.add(new JLabel("Tìm mã/tên/SĐT:"));
        searchRow.add(txtTim);
        searchRow.add(cboTrangThai);
        JButton btnTim = button("Tìm", true);
        searchRow.add(btnTim);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionRow.setOpaque(false);
        JButton btnThem = button("Thêm", true);
        JButton btnSua = button("Sửa", false);
        JButton btnXoa = button("Ngừng hoạt động", false);
        JButton btnKichHoat = button("Kích hoạt lại", false);
        JButton btnLichSu = button("Lịch sử", false);
        JButton btnLamMoi = button("Làm mới", false);
        for (JButton item : new JButton[]{
            btnThem, btnSua, btnXoa, btnKichHoat, btnLichSu, btnLamMoi
        }) {
            actionRow.add(item);
        }
        toolbar.add(searchRow);
        toolbar.add(actionRow);
        card.add(toolbar, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224, 231, 240)));
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        btnTim.addActionListener(e -> refreshData());
        txtTim.addActionListener(e -> refreshData());
        cboTrangThai.addActionListener(e -> refreshData());
        btnLamMoi.addActionListener(e -> { txtTim.setText(""); cboTrangThai.setSelectedIndex(0); refreshData(); });
        btnThem.addActionListener(e -> openForm(null));
        btnSua.addActionListener(e -> { KhachHang item = selected(); if (item != null) openForm(item); });
        btnXoa.addActionListener(e -> deactivateSelected());
        btnKichHoat.addActionListener(e -> reactivateSelected());
        btnLichSu.addActionListener(e -> showHistory());

        Runnable updateActions = () -> {
            KhachHang item = selectedWithoutMessage();
            boolean active = item != null && "HOAT_DONG".equals(item.getTrangThai());
            boolean inactive = item != null
                    && "NGUNG_HOAT_DONG".equals(item.getTrangThai());
            btnSua.setEnabled(active);
            btnXoa.setEnabled(active);
            btnKichHoat.setEnabled(inactive);
            btnLichSu.setEnabled(item != null);
        };
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateActions.run();
        });
        updateActions.run();
    }

    public void refreshData() {
        try {
            String status = cboTrangThai.getSelectedIndex() == 0
                    ? "" : String.valueOf(cboTrangThai.getSelectedItem());
            rows = controller.timKhachHang(txtTim.getText(), status);
            model.setRowCount(0);
            for (KhachHang item : rows) {
                String document = item.getSoGiayTo() == null ? "—"
                        : text(item.getLoaiGiayTo()) + " " + item.getSoGiayTo();
                model.addRow(new Object[]{
                    item.getMaKhachHang(), item.getHoTen(), text(item.getSoDienThoai()),
                    text(item.getEmail()), document, text(item.getQuocTich()),
                    item.getNgaySinh() == null ? "—" : item.getNgaySinh(),
                    item.getGioiTinh(), item.getTrangThai()
                });
            }
        } catch (SQLException ex) {
            error("Không thể tải danh sách khách hàng", ex);
        }
    }

    private void openForm(KhachHang existing) {
        JTextField hoTen = new JTextField(existing == null ? "" : text(existing.getHoTen()));
        JTextField loaiGiayTo = new JTextField(existing == null ? "" : text(existing.getLoaiGiayTo()));
        JTextField soGiayTo = new JTextField(existing == null ? "" : text(existing.getSoGiayTo()));
        JTextField quocGiaCap = new JTextField(existing == null ? "VN" : text(existing.getQuocGiaCap()));
        JTextField soDienThoai = new JTextField(existing == null ? "" : text(existing.getSoDienThoai()));
        JTextField email = new JTextField(existing == null ? "" : text(existing.getEmail()));
        JTextField ngaySinh = new JTextField(existing == null || existing.getNgaySinh() == null
                ? "" : existing.getNgaySinh().toString());
        JComboBox<String> gioiTinh = new JComboBox<>(new String[]{"KHONG_CUNG_CAP", "NAM", "NU", "KHAC"});
        if (existing != null) gioiTinh.setSelectedItem(existing.getGioiTinh());
        JTextField quocTich = new JTextField(existing == null ? "VN" : text(existing.getQuocTich()));
        JTextField diaChi = new JTextField(existing == null ? "" : text(existing.getDiaChi()));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        addField(form, "Họ tên (*)", hoTen);
        addField(form, "Loại giấy tờ", loaiGiayTo);
        addField(form, "Số giấy tờ", soGiayTo);
        addField(form, "Quốc gia cấp (2 ký tự)", quocGiaCap);
        addField(form, "Số điện thoại", soDienThoai);
        addField(form, "Email", email);
        addField(form, "Ngày sinh (yyyy-MM-dd)", ngaySinh);
        addField(form, "Giới tính", gioiTinh);
        addField(form, "Quốc tịch (2 ký tự)", quocTich);
        addField(form, "Địa chỉ", diaChi);
        int choice = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Thêm khách hàng" : "Sửa khách hàng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;
        try {
            KhachHang value = new KhachHang();
            if (existing != null) value.setMaKhachHang(existing.getMaKhachHang());
            value.setHoTen(hoTen.getText());
            value.setLoaiGiayTo(loaiGiayTo.getText());
            value.setSoGiayTo(soGiayTo.getText());
            value.setQuocGiaCap(quocGiaCap.getText());
            value.setSoDienThoai(soDienThoai.getText());
            value.setEmail(email.getText());
            value.setNgaySinh(ngaySinh.getText().isBlank() ? null : LocalDate.parse(ngaySinh.getText().trim()));
            value.setGioiTinh(String.valueOf(gioiTinh.getSelectedItem()));
            value.setQuocTich(quocTich.getText());
            value.setDiaChi(diaChi.getText());
            if (existing == null) {
                String id = controller.themKhachHang(value);
                JOptionPane.showMessageDialog(this, "Đã tạo khách hàng " + id + ".");
            } else {
                controller.capNhatKhachHang(value);
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công.");
            }
            refreshData();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải theo định dạng yyyy-MM-dd.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            error(existing == null ? "Không thể thêm khách hàng" : "Không thể cập nhật khách hàng", ex);
        }
    }

    private void deactivateSelected() {
        KhachHang item = selected();
        if (item == null) return;
        if (!"HOAT_DONG".equals(item.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Khách hàng đã ngừng hoạt động.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Ngừng hoạt động khách hàng " + item.getMaKhachHang()
                + "?\nLịch sử đặt và lưu trú vẫn được bảo toàn.",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            controller.ngungHoatDong(item.getMaKhachHang());
            refreshData();
        } catch (SQLException ex) {
            error("Không thể ngừng hoạt động khách hàng", ex);
        }
    }

    private void reactivateSelected() {
        KhachHang item = selected();
        if (item == null) return;
        if (!"NGUNG_HOAT_DONG".equals(item.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Khách hàng đang hoạt động.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Kích hoạt lại khách hàng " + item.getMaKhachHang()
                + "?\nKhách hàng sẽ có thể tạo đơn đặt phòng mới.",
                "Xác nhận kích hoạt", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            controller.kichHoatLai(item.getMaKhachHang());
            JOptionPane.showMessageDialog(this, "Kích hoạt lại khách hàng thành công.");
            refreshData();
        } catch (SQLException ex) {
            error("Không thể kích hoạt lại khách hàng", ex);
            refreshData();
        }
    }

    private void showHistory() {
        KhachHang customer = selected();
        if (customer == null) return;
        try {
            List<DatPhong> history = controller.layLichSu(customer.getMaKhachHang());
            DefaultTableModel bookingModel = new DefaultTableModel(new String[]{
                "Mã đặt", "Phòng", "Nhận dự kiến", "Trả dự kiến", "Trạng thái"
            }, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
            DefaultTableModel stayModel = new DefaultTableModel(new String[]{
                "Mã đặt", "Phòng", "Nhận thực tế", "Trả thực tế", "Trạng thái"
            }, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
            for (DatPhong item : history) {
                bookingModel.addRow(new Object[]{item.getMaDatPhong(), item.getMaPhong(),
                    item.getNgayNhanDuKien(), item.getNgayTraDuKien(), item.getTrangThai()});
                if (item.getNgayNhan() != null || item.getNgayTra() != null) {
                    stayModel.addRow(new Object[]{item.getMaDatPhong(), item.getMaPhong(),
                        item.getNgayNhan(), item.getNgayTra(), item.getTrangThai()});
                }
            }
            JTabbedPane tabs = new JTabbedPane();
            JTable bookingTable = new JTable(bookingModel);
            JTable stayTable = new JTable(stayModel);
            TableUi.centerCellsAndHeader(bookingTable);
            TableUi.centerCellsAndHeader(stayTable);
            tabs.addTab("Lịch sử đặt phòng", new JScrollPane(bookingTable));
            tabs.addTab("Lịch sử lưu trú", new JScrollPane(stayTable));
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                    "Lịch sử - " + customer.getHoTen());
            dialog.add(tabs);
            dialog.setSize(850, 420);
            dialog.setLocationRelativeTo(this);
            dialog.setModal(true);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            error("Không thể tải lịch sử khách hàng", ex);
        }
    }

    private KhachHang selected() {
        KhachHang item = selectedWithoutMessage();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng.");
            return null;
        }
        return item;
    }

    private KhachHang selectedWithoutMessage() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return modelRow >= 0 && modelRow < rows.size() ? rows.get(modelRow) : null;
    }

    private JButton button(String label, boolean primary) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(primary ? WHITE : NAVY);
        button.setBackground(primary ? NAVY : new Color(234, 240, 247));
        button.setFocusPainted(false);
        return button;
    }
    private void addField(JPanel panel, String label, java.awt.Component field) {
        panel.add(new JLabel(label)); panel.add(field);
    }
    private String text(String value) { return value == null || value.isBlank() ? "—" : value; }
    private void error(String context, SQLException ex) {
        JOptionPane.showMessageDialog(this, context + ":\n" + ex.getMessage(),
                "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
    }
}

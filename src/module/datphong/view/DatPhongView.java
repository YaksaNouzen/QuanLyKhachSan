
package module.datphong.view;

import common.ui.TableUi;
import module.datphong.controller.DatPhongController;
import module.datphong.model.DatPhong;
import module.khachhang.view.KhachHangView;
import module.luutru.view.LuuTruView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DatPhongView extends JFrame {
    private static final Color NAVY = new Color(27, 56, 94);
    private static final Color NAVY_HOVER = new Color(39, 77, 124);
    private static final Color BG = new Color(244, 247, 251);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(28, 46, 70);
    private static final Color MUTED = new Color(106, 120, 138);

    private final DatPhongController controller;
    private final KhachHangView khachHangView;
    private final LuuTruView luuTruView;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JPanel contentPanel = new JPanel(new CardLayout());
    private JButton activeMenu;

    public DatPhongView() {
        controller = new DatPhongController();
        khachHangView = new KhachHangView();
        luuTruView = new LuuTruView(this::taiDanhSachDatPhong);
        setTitle("HỆ THỐNG QUẢN LÝ KHÁCH SẠN");
        setSize(1180, 730);
        setMinimumSize(new Dimension(960, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        String[] columns = {
            "Mã đặt phòng", "Mã khách hàng", "Mã phòng", "Giá phòng chốt",
            "Ngày đặt", "Nhận dự kiến", "Nhận thực tế",
            "Trả dự kiến", "Trả thực tế", "Trạng thái"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        configureTable();

        contentPanel.setBackground(BG);
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(khachHangView, "CUSTOMERS");
        contentPanel.add(createBookingPanel(), "BOOKINGS");
        contentPanel.add(luuTruView, "STAYS");
        contentPanel.add(createAvailableRoomsPanel(), "AVAILABLE_ROOMS");
        contentPanel.add(createRoomTransferPanel(), "ROOM_TRANSFER");
        
        getContentPane().add(createSidebar(), BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        showPage("HOME");
        taiDanhSachDatPhong();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(215, 0));
        sidebar.setBackground(NAVY);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(20, 14, 10, 14));

        JLabel brand = new JLabel("HOTEL MANAGER");
        brand.setForeground(WHITE);
        brand.setFont(new Font("Arial", Font.BOLD, 19));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(brand);
        top.add(Box.createVerticalStrut(7));

        JLabel subtitle = new JLabel("QUẢN LÝ KHÁCH SẠN");
        subtitle.setForeground(new Color(192, 211, 232));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(subtitle);
        top.add(Box.createVerticalStrut(40));

        JButton home = menuButton("Trang chủ", "HOME");
        JButton bookings = menuButton("Quản lý đặt phòng", "BOOKINGS");
        top.add(home);
        top.add(Box.createVerticalStrut(5));
        top.add(menuButton("Quản lý khách hàng", "CUSTOMERS"));
        top.add(Box.createVerticalStrut(5));
        top.add(bookings);
        top.add(Box.createVerticalStrut(5));
        top.add(menuButton("Nhận / trả phòng", "STAYS"));
        top.add(Box.createVerticalStrut(5));
        top.add(menuButton("Tìm phòng trống", "AVAILABLE_ROOMS"));
        top.add(Box.createVerticalStrut(5));
        top.add(menuButton("Đổi phòng", "ROOM_TRANSFER"));

        JLabel version = new JLabel("Phiên bản 1.0", SwingConstants.CENTER);
        version.setForeground(new Color(174, 196, 219));
        version.setFont(new Font("Arial", Font.PLAIN, 11));
        version.setBorder(new EmptyBorder(0, 0, 19, 0));

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(version, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton menuButton(String text, String page) {
        JButton button = baseMenuButton(text);
        button.addActionListener(e -> {
            if (activeMenu != null) activeMenu.setBackground(NAVY);
            activeMenu = button;
            activeMenu.setBackground(NAVY_HOVER);
            showPage(page);
            if ("BOOKINGS".equals(page)) taiDanhSachDatPhong();
            if ("CUSTOMERS".equals(page)) khachHangView.refreshData();
            if ("STAYS".equals(page)) luuTruView.refreshData();
        });
        if ("HOME".equals(page)) {
            activeMenu = button;
            button.setBackground(NAVY_HOVER);
        }
        return button;
    }

    private JButton unavailableMenuButton(String text) {
        JButton button = baseMenuButton(text);
        button.addActionListener(e -> JOptionPane.showMessageDialog(
                this, "Chức năng này sẽ được kết nối sau khi hoàn thiện DAO và Controller.",
                text, JOptionPane.INFORMATION_MESSAGE));
        return button;
    }

    private JButton baseMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(WHITE);
        button.setBackground(NAVY);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(13, 10, 13, 10));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showPage(String name) {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, name);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Trang chủ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel description = new JLabel("Chào mừng đến hệ thống quản lý khách sạn.");
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(TEXT);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(15));
        card.add(description);
        panel.add(card);
        return panel;
    }

    private JPanel createBookingPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(26, 25, 25, 25));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Quản lý đặt phòng");
        title.setForeground(NAVY);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        JLabel description = new JLabel("Danh sách đặt phòng và thao tác nghiệp vụ");
        description.setForeground(MUTED);
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(title);
        titles.add(Box.createVerticalStrut(7));
        titles.add(description);
        heading.add(titles, BorderLayout.WEST);
        root.add(heading, BorderLayout.NORTH);

        JPanel whiteCard = new JPanel(new BorderLayout(0, 15));
        whiteCard.setBackground(WHITE);
        whiteCard.setBorder(new EmptyBorder(18, 16, 16, 16));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setOpaque(false);
        JButton btnThem = actionButton("Thêm", true);
        JButton btnSua = actionButton("Sửa lịch", false);
        JButton btnHuy = actionButton("Hủy đặt phòng", false);
        JButton btnXoa = actionButton("Xóa", false);
        JButton btnLamMoi = actionButton("Làm mới", false);
        for (JButton b : new JButton[]{btnThem, btnSua, btnHuy, btnXoa, btnLamMoi}) {
            toolbar.add(b);
        }
        whiteCard.add(toolbar, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224, 231, 240)));
        scroll.getViewport().setBackground(WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        whiteCard.add(scroll, BorderLayout.CENTER);
        root.add(whiteCard, BorderLayout.CENTER);

        btnThem.addActionListener(e -> {
            ThemDatPhongDialog dialog = new ThemDatPhongDialog(this, controller);
            dialog.setVisible(true);
            if (dialog.isTaoThanhCong()) taiDanhSachDatPhong();
        });

        btnSua.addActionListener(e -> {
            String ma = selectedBookingId("sửa");
            if (ma == null) return;
            try {
                DatPhong selected = null;
                for (DatPhong dp : controller.layDanhSachDatPhong()) {
                    if (ma.equals(dp.getMaDatPhong())) {
                        selected = dp;
                        break;
                    }
                }
                if (selected == null) {
                    JOptionPane.showMessageDialog(this, "Đơn đặt phòng không còn tồn tại.");
                    taiDanhSachDatPhong();
                    return;
                }
                SuaDatPhongDialog dialog = new SuaDatPhongDialog(this, controller, selected);
                dialog.setVisible(true);
                if (dialog.isCapNhatThanhCong()) taiDanhSachDatPhong();
            } catch (SQLException ex) {
                showDatabaseError("Không thể lấy thông tin đơn đặt phòng", ex);
            }
        });

        btnHuy.addActionListener(e -> runBookingAction("hủy", "Hủy đặt phòng", "Hủy đặt phòng thành công.",
                id -> controller.huyDatPhong(id)));
        btnXoa.addActionListener(e -> runBookingAction("xóa", "Xóa đặt phòng",
                "Xóa đặt phòng thành công.", id -> controller.xoaDatPhong(id)));
        btnLamMoi.addActionListener(e -> taiDanhSachDatPhong());
        
Runnable capNhatNut = () -> {
    
int selectedRow = table.getSelectedRow();

if (selectedRow >= table.getRowCount()) {
    selectedRow = -1;
}


    // Thêm và Làm mới luôn được phép sử dụng.
    btnThem.setEnabled(true);
    btnLamMoi.setEnabled(true);

    if (selectedRow < 0) {
        btnSua.setEnabled(false);
        btnHuy.setEnabled(false);
        btnXoa.setEnabled(false);
        return;
    }

    
int modelRow = table.convertRowIndexToModel(selectedRow);

if (modelRow < 0 || modelRow >= tableModel.getRowCount()) {
    btnSua.setEnabled(false);
    btnHuy.setEnabled(false);
    btnXoa.setEnabled(false);
    return;
}

String trangThai = String.valueOf(
        tableModel.getValueAt(modelRow, 9)
);


    boolean giuPhong = "GIU_PHONG".equals(trangThai);
    boolean daHuy = "DA_HUY".equals(trangThai);

    btnSua.setEnabled(giuPhong);
    btnHuy.setEnabled(giuPhong);
    btnXoa.setEnabled(daHuy);
};

table.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        capNhatNut.run();
    }
});


// Trạng thái ban đầu khi chưa chọn đơn.
capNhatNut.run();

        return root;
    }

    private JButton actionButton(String label, boolean primary) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(primary ? WHITE : NAVY);
        button.setBackground(primary ? NAVY : new Color(234, 240, 247));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(11, 13, 11, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void configureTable() {
        TableUi.centerCellsAndHeader(table);
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(218, 231, 247));
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 239, 245));
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(232, 239, 248));
        header.setForeground(NAVY);
        int[] widths = {210, 145, 105, 145, 175, 175, 175, 175, 175, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                if (!selected) setBackground(row % 2 == 0 ? WHITE : new Color(249, 251, 254));
                setBorder(new EmptyBorder(0, 9, 0, 9));
                return this;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

private JPanel createAvailableRoomsPanel() {
    JPanel root = new JPanel(new BorderLayout(0, 18));
    root.setBackground(BG);
    root.setBorder(new EmptyBorder(26, 25, 25, 25));

    JLabel title = new JLabel("Tìm phòng trống");
    title.setFont(new Font("Arial", Font.BOLD, 25));
    title.setForeground(NAVY);
    root.add(title, BorderLayout.NORTH);

    JPanel card = new JPanel(new BorderLayout(0, 18));
    card.setBackground(WHITE);
    card.setBorder(new EmptyBorder(22, 20, 20, 20));

    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
    form.setOpaque(false);

    JTextField txtNgayNhan = new JTextField(16);
    JTextField txtNgayTra = new JTextField(16);

    txtNgayNhan.setToolTipText("dd/MM/yyyy HH:mm - Ví dụ: 20/11/2026 14:00");
    txtNgayTra.setToolTipText("dd/MM/yyyy HH:mm - Ví dụ: 22/11/2026 12:00");

    JButton btnTim = actionButton("Tìm phòng", true);

    form.add(new JLabel("Ngày nhận:"));
    form.add(txtNgayNhan);
    form.add(new JLabel("Ngày trả:"));
    form.add(txtNgayTra);
    form.add(btnTim);

    card.add(form, BorderLayout.NORTH);

    DefaultTableModel modelPhong = new DefaultTableModel(
            new String[]{"Phòng khả dụng"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JTable bangPhong = new JTable(modelPhong);
    TableUi.centerCellsAndHeader(bangPhong);
    bangPhong.setRowHeight(34);
    bangPhong.setFont(new Font("Arial", Font.PLAIN, 13));
    bangPhong.setFillsViewportHeight(true);

    JScrollPane scroll = new JScrollPane(bangPhong);
    scroll.setBorder(BorderFactory.createLineBorder(
            new Color(224, 231, 240)));

    card.add(scroll, BorderLayout.CENTER);

    JLabel status = new JLabel(
            "Nhập thời gian theo định dạng Việt Nam: dd/MM/yyyy HH:mm");
    status.setForeground(MUTED);
    card.add(status, BorderLayout.SOUTH);

    btnTim.addActionListener(e -> {
        try {
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern(
                            "dd/MM/uuuu HH:mm")
                    .withResolverStyle(
                            java.time.format.ResolverStyle.STRICT);

            java.time.LocalDateTime nhan =
                    java.time.LocalDateTime.parse(
                            txtNgayNhan.getText().trim(), formatter);

            java.time.LocalDateTime tra =
                    java.time.LocalDateTime.parse(
                            txtNgayTra.getText().trim(), formatter);

            if (!tra.isAfter(nhan)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ngày trả phải sau ngày nhận.",
                        "Thời gian không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.sql.Timestamp ngayNhan =
                    java.sql.Timestamp.valueOf(nhan);
            java.sql.Timestamp ngayTra =
                    java.sql.Timestamp.valueOf(tra);

            List<String> danhSach =
                    controller.timPhongTrong(ngayNhan, ngayTra);

            modelPhong.setRowCount(0);

            for (String phong : danhSach) {
                modelPhong.addRow(new Object[]{phong});
            }

            status.setText("Tìm thấy " + danhSach.size()
                    + " phòng khả dụng.");

        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập ngày giờ theo định dạng:\n"
                    + "dd/MM/yyyy HH:mm\n"
                    + "Ví dụ: 20/11/2026 14:00",
                    "Sai định dạng",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException ex) {
            showDatabaseError("Không thể tìm phòng trống", ex);
        }
    });

    root.add(card, BorderLayout.CENTER);
    return root;
}


private JPanel createRoomTransferPanel() {
    JPanel root = new JPanel(new BorderLayout(0, 14));
    root.setBackground(BG);
    root.setBorder(new EmptyBorder(20, 18, 20, 18));

    JLabel title = new JLabel("Đổi phòng");
    title.setFont(new Font("Arial", Font.BOLD, 25));
    title.setForeground(NAVY);
    root.add(title, BorderLayout.NORTH);

    JPanel card = new JPanel(new BorderLayout(0, 14));
    card.setBackground(WHITE);
    card.setBorder(new EmptyBorder(18, 22, 18, 22));

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(7, 6, 7, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    JTextField txtMaDatPhongCu = new JTextField(18);
    JTextField txtMaPhongMoi = new JTextField(18);
    JTextField txtMaDatPhongMoi = new JTextField(18);
    JTextField txtMaChuyenPhong = new JTextField(18);
    JTextField txtNguoiThucHien = new JTextField(18);
    JTextArea txtLyDo = new JTextArea(3, 18);
    JButton btnGoiYPhong = actionButton("Gợi ý", false);
    txtLyDo.setLineWrap(true);
    txtLyDo.setWrapStyleWord(true);

    JPanel roomPicker = new JPanel(new BorderLayout(8, 0));
    roomPicker.setOpaque(false);
    roomPicker.add(txtMaPhongMoi, BorderLayout.CENTER);
    roomPicker.add(btnGoiYPhong, BorderLayout.EAST);

    String[] labels = {
        "Mã đặt phòng cũ:",
        "Mã phòng mới:",
        "Mã đặt phòng mới:",
        "Mã chuyển phòng:",
        "Người thực hiện:",
        "Lý do đổi phòng:"
    };

    JComponent[] fields = {
        txtMaDatPhongCu,
        roomPicker,
        txtMaDatPhongMoi,
        txtMaChuyenPhong,
        txtNguoiThucHien,
        new JScrollPane(txtLyDo)
    };

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel(labels[i]), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(fields[i], gbc);
    }

    JButton btnDoiPhong = actionButton("Xác nhận đổi phòng", true);

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.setOpaque(false);
    buttons.add(btnDoiPhong);

    card.add(form, BorderLayout.CENTER);

    JLabel note = new JLabel(
        "<html><div style='width: 520px'>Áp dụng cho đơn GIU_PHONG hoặc DANG_O. "
        + "Phòng mới phải khác phòng cũ và không trùng lịch. "
        + "Đơn đang ở sẽ được tách chặng tại thời điểm chuyển. "
        + "Giá chốt lấy theo phòng mới.</div></html>"
    );
    note.setForeground(MUTED);

    JPanel footer = new JPanel(new BorderLayout(0, 8));
    footer.setOpaque(false);
    footer.add(note, BorderLayout.CENTER);
    footer.add(buttons, BorderLayout.SOUTH);
    card.add(footer, BorderLayout.SOUTH);

    btnGoiYPhong.addActionListener(e -> {
        String maCu = txtMaDatPhongCu.getText().trim();
        if (maCu.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập mã đặt phòng cũ trước khi lấy gợi ý.",
                    "Thiếu mã đặt phòng",
                    JOptionPane.WARNING_MESSAGE);
            txtMaDatPhongCu.requestFocusInWindow();
            return;
        }
        try {
            List<String> suggestions = controller.timPhongTrongDeDoi(maCu);
            if (suggestions.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không có phòng khả dụng và không bị đặt trước "
                        + "trong lịch của đơn " + maCu + ".",
                        "Không tìm thấy phòng trống",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object selected = JOptionPane.showInputDialog(
                    this,
                    "Chọn phòng khả dụng cho toàn bộ khoảng lưu trú của đơn "
                    + maCu + ":",
                    "Gợi ý phòng trống",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    suggestions.toArray(),
                    suggestions.get(0));
            if (selected != null) {
                String value = selected.toString();
                int separator = value.indexOf(" - ");
                txtMaPhongMoi.setText(
                        separator < 0 ? value : value.substring(0, separator));
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Dữ liệu không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showDatabaseError("Không thể gợi ý phòng trống", ex);
        }
    });

    root.add(card, BorderLayout.CENTER);

    btnDoiPhong.addActionListener(e -> {
        String maCu = txtMaDatPhongCu.getText().trim();
        String phongMoi = txtMaPhongMoi.getText().trim();
        String maMoi = txtMaDatPhongMoi.getText().trim();
        String maChuyen = txtMaChuyenPhong.getText().trim();
        String nguoiThucHien = txtNguoiThucHien.getText().trim();
        String lyDo = txtLyDo.getText().trim();

        if (maCu.isEmpty() || phongMoi.isEmpty()
                || maMoi.isEmpty() || maChuyen.isEmpty()
                || lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng nhập đầy đủ các trường bắt buộc.",
                "Thiếu thông tin",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (maCu.length() > 20 || phongMoi.length() > 20
                || maMoi.length() > 20 || maChuyen.length() > 20
                || lyDo.length() > 1000
                || nguoiThucHien.length() > 100) {
            JOptionPane.showMessageDialog(
                this,
                "Thông tin vượt quá độ dài cho phép.",
                "Dữ liệu không hợp lệ",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Xác nhận đổi phòng cho đơn " + maCu
            + " sang phòng " + phongMoi + "?\n"
            + "Đơn mới: " + maMoi + "\n"
            + "Mã chuyển phòng: " + maChuyen,
            "Xác nhận đổi phòng",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.doiPhong(
                maCu,
                phongMoi,
                lyDo,
                nguoiThucHien.isEmpty() ? null : nguoiThucHien,
                maMoi,
                maChuyen
            );

            JOptionPane.showMessageDialog(
                this,
                "Đổi phòng thành công.\n"
                + "Chặng phòng cũ đã kết thúc phù hợp và đơn mới đã được tạo.",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
            );

            taiDanhSachDatPhong();
            luuTruView.refreshData();

            txtMaDatPhongCu.setText("");
            txtMaPhongMoi.setText("");
            txtMaDatPhongMoi.setText("");
            txtMaChuyenPhong.setText("");
            txtNguoiThucHien.setText("");
            txtLyDo.setText("");

        } catch (SQLException ex) {
            showDatabaseError("Không thể đổi phòng", ex);
        }
    });

    return root;
}


private JPanel createStayHistoryPanel() {
    JPanel root = new JPanel(new BorderLayout(0, 18));
    root.setBackground(BG);
    root.setBorder(new EmptyBorder(26, 25, 25, 25));

    JLabel title = new JLabel("Lịch sử lưu trú");
    title.setFont(new Font("Arial", Font.BOLD, 25));
    title.setForeground(NAVY);
    root.add(title, BorderLayout.NORTH);

    JPanel card = new JPanel(new BorderLayout(0, 15));
    card.setBackground(WHITE);
    card.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
    toolbar.setOpaque(false);

    JLabel lblLoc = new JLabel("Trạng thái:");
    JComboBox<String> cboTrangThai = new JComboBox<>(
        new String[]{
            "Tất cả",
            "Đang ở",
            "Đã trả phòng"
        }
    );

    JButton btnLamMoi = actionButton("Làm mới", true);

    toolbar.add(lblLoc);
    toolbar.add(cboTrangThai);
    toolbar.add(btnLamMoi);

    card.add(toolbar, BorderLayout.NORTH);

    String[] columns = {
        "Mã đặt phòng",
        "Mã khách hàng",
        "Mã phòng",
        "Ngày nhận thực tế",
        "Ngày trả thực tế",
        "Trạng thái"
    };

    DefaultTableModel historyModel =
            new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JTable historyTable = new JTable(historyModel);
    TableUi.centerCellsAndHeader(historyTable);
    historyTable.setRowHeight(34);
    historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
    historyTable.setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION
    );
    historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    historyTable.setFillsViewportHeight(true);

    int[] widths = {220, 145, 110, 190, 190, 135};

    for (int i = 0; i < widths.length; i++) {
        historyTable.getColumnModel()
                .getColumn(i).setPreferredWidth(widths[i]);
    }

    JScrollPane scroll = new JScrollPane(historyTable);
    scroll.setBorder(BorderFactory.createLineBorder(
        new Color(224, 231, 240)
    ));

    card.add(scroll, BorderLayout.CENTER);

    JLabel status = new JLabel("Chưa tải dữ liệu.");
    status.setForeground(MUTED);
    card.add(status, BorderLayout.SOUTH);

    Runnable taiLichSu = () -> {
        try {
            List<DatPhong> danhSach =
                    controller.layDanhSachDatPhong();

            String boLoc =
                    cboTrangThai.getSelectedItem().toString();

            historyModel.setRowCount(0);
            int soLuong = 0;

            for (DatPhong dp : danhSach) {
                String trangThai = dp.getTrangThai();

                if (!"DANG_O".equals(trangThai)
                        && !"DA_TRA".equals(trangThai)) {
                    continue;
                }

                if ("Đang ở".equals(boLoc)
                        && !"DANG_O".equals(trangThai)) {
                    continue;
                }

                if ("Đã trả phòng".equals(boLoc)
                        && !"DA_TRA".equals(trangThai)) {
                    continue;
                }

                java.time.format.DateTimeFormatter dinhDang =
        java.time.format.DateTimeFormatter.ofPattern(
                "dd/MM/yyyy HH:mm:ss"
        );

String ngayNhan = dp.getNgayNhan() == null
        ? "Chưa nhận phòng"
        : dp.getNgayNhan().toLocalDateTime().format(dinhDang);

String ngayTra = dp.getNgayTra() == null
        ? "Chưa trả phòng"
        : dp.getNgayTra().toLocalDateTime().format(dinhDang);

historyModel.addRow(new Object[]{
    dp.getMaDatPhong(),
    dp.getMaKhachHang(),
    dp.getMaPhong(),
    ngayNhan,
    ngayTra,
    trangThai
});

                soLuong++;
            }

            status.setText(
                "Hiển thị " + soLuong + " lượt lưu trú."
            );

        } catch (SQLException ex) {
            historyModel.setRowCount(0);
            status.setText("Không thể tải lịch sử lưu trú.");
            showDatabaseError(
                "Không thể tải lịch sử lưu trú", ex
            );
        }
    };

    cboTrangThai.addActionListener(e -> taiLichSu.run());
    btnLamMoi.addActionListener(e -> taiLichSu.run());

    // Tải dữ liệu lần đầu khi tạo màn hình.
    taiLichSu.run();

    root.add(card, BorderLayout.CENTER);

    return root;
}



    private String selectedBookingId(String action) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đơn đặt phòng cần " + action + ".",
                    "Chưa chọn đơn", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        return tableModel.getValueAt(modelRow, 0).toString();
    }

    @FunctionalInterface
    private interface BookingOperation {
        void execute(String id) throws SQLException;
    }

    
private boolean kiemTraTrangThaiMoiNhat(
        String maDatPhong,
        String trangThaiYeuCau,
        String tenThaoTac) {

    try {
        List<DatPhong> danhSach =
                controller.layDanhSachDatPhong();

        DatPhong donHienTai = null;

        for (DatPhong dp : danhSach) {
            if (maDatPhong.equals(dp.getMaDatPhong())) {
                donHienTai = dp;
                break;
            }
        }

        if (donHienTai == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đơn đặt phòng không còn tồn tại.\n"
                    + "Danh sách sẽ được cập nhật.",
                    "Dữ liệu đã thay đổi",
                    JOptionPane.WARNING_MESSAGE
            );

            taiDanhSachDatPhong();
            return false;
        }

        if (!trangThaiYeuCau.equals(
                donHienTai.getTrangThai())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể " + tenThaoTac + ".\n"
                    + "Trạng thái hiện tại của đơn là: "
                    + donHienTai.getTrangThai() + "\n"
                    + "Danh sách sẽ được cập nhật.",
                    "Trạng thái đã thay đổi",
                    JOptionPane.WARNING_MESSAGE
            );

            taiDanhSachDatPhong();
            return false;
        }

        return true;

    } catch (SQLException ex) {
        showDatabaseError(
                "Không thể kiểm tra trạng thái đơn đặt phòng",
                ex
        );
        return false;
    }
}

    
   
private void runBookingAction(
        String verb,
        String title,
        String success,
        BookingOperation operation) {

    String ma = selectedBookingId(verb);

    if (ma == null) {
        return;
    }

    String trangThaiYeuCau;

    switch (verb) {
        case "hủy":
            trangThaiYeuCau = "GIU_PHONG";
            break;

        case "xóa":
            trangThaiYeuCau = "DA_HUY";
            break;

        default:
            JOptionPane.showMessageDialog(
                    this,
                    "Thao tác chưa được hỗ trợ kiểm tra trạng thái.",
                    "Thao tác không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
    }

    // Kiểm tra dữ liệu mới nhất trước khi xác nhận.
    if (!kiemTraTrangThaiMoiNhat(
            ma, trangThaiYeuCau, verb)) {
        return;
    }

    String question =
            "Xác nhận " + verb + " cho đơn " + ma + "?";

    if ("xóa".equals(verb)) {
        question =
                "CẢNH BÁO: Xóa đơn đặt phòng " + ma + "?\n\n"
                + "Chỉ đơn DA_HUY không có dữ liệu liên quan "
                + "mới được xóa.\n"
                + "Thao tác này không thể hoàn tác.";
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            question,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        operation.execute(ma);

        JOptionPane.showMessageDialog(
                this,
                success,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );

        taiDanhSachDatPhong();

    } catch (SQLException ex) {
        showDatabaseError("Không thể " + verb, ex);

        // Đồng bộ giao diện sau khi SQL từ chối thao tác.
        taiDanhSachDatPhong();
    }
}


    private void showDatabaseError(String context, SQLException ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) message = "Không xác định được lỗi cơ sở dữ liệu.";
        JOptionPane.showMessageDialog(this, context + ":\n" + message,
                "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
    }

    
private String dinhDangNgayGio(java.sql.Timestamp thoiGian) {
    if (thoiGian == null) {
        return "—";
    }

    java.time.format.DateTimeFormatter formatter =
            java.time.format.DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    return thoiGian.toLocalDateTime().format(formatter);
}

private String dinhDangTien(java.math.BigDecimal soTien) {
    if (soTien == null) {
        return "—";
    }

    java.text.DecimalFormatSymbols symbols =
            new java.text.DecimalFormatSymbols(
                    java.util.Locale.of("vi", "VN")
            );

    java.text.DecimalFormat formatter =
            new java.text.DecimalFormat("#,##0.##", symbols);

    return formatter.format(soTien) + " VNĐ";
}

    
    private void taiDanhSachDatPhong() {
        try {
            List<DatPhong> danhSach = controller.layDanhSachDatPhong();
            tableModel.setRowCount(0);
            for (DatPhong dp : danhSach) {
                tableModel.addRow(new Object[]{
    dp.getMaDatPhong(),
    dp.getMaKhachHang(),
    dp.getMaPhong(),
    dinhDangTien(dp.getGiaPhongChot()),
    dinhDangNgayGio(dp.getNgayDat()),
    dinhDangNgayGio(dp.getNgayNhanDuKien()),
    dinhDangNgayGio(dp.getNgayNhan()),
    dinhDangNgayGio(dp.getNgayTraDuKien()),
    dinhDangNgayGio(dp.getNgayTra()),
    dp.getTrangThai()
});
            }
        } catch (SQLException ex) {
            showDatabaseError("Không thể tải danh sách đặt phòng", ex);
        }
    }

}

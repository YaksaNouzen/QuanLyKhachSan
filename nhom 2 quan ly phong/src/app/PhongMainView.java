package app;

import common.database.ConnectionFactory;
import common.database.DatabaseConnection;
import common.database.SchemaCheckNhom2;
import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import module.baotri.controller.BaoTriController;
import module.baotri.dao.BaoTriDAO;
import module.baotri.dao.MemoryBaoTriDAO;
import module.baotri.dao.SqlServerBaoTriDAO;
import module.baotri.service.BaoTriService;
import module.baotri.view.BaoTriPanel;
import module.buongphong.controller.BuongPhongController;
import module.buongphong.dao.MemoryVeSinhDAO;
import module.buongphong.dao.SqlServerVeSinhDAO;
import module.buongphong.dao.VeSinhDAO;
import module.buongphong.service.BuongPhongService;
import module.buongphong.view.BuongPhongPanel;
import module.phong.controller.PhongController;
import module.phong.dao.MemoryPhongDAO;
import module.phong.dao.PhongDAO;
import module.phong.dao.SqlServerPhongDAO;
import module.phong.service.PhongService;
import module.phong.view.PhongPanel;

public class PhongMainView extends JFrame {
    private final PhongController phongController;
    private final BuongPhongController buongPhongController;
    private final BaoTriController baoTriController;

    public PhongMainView(boolean demo) {
        super("Hệ thống Quản lý phòng & Dịch vụ Buồng - Khách sạn (Nhóm 2)");
        ConnectionFactory connections = DatabaseConnection::getConnection;
        PhongDAO phongDAO;
        VeSinhDAO veSinhDAO;
        BaoTriDAO baoTriDAO;
        PhongService phongService;
        if (demo) {
            MemoryPhongDAO memoryPhongDAO = new MemoryPhongDAO();
            phongDAO = memoryPhongDAO;
            phongService = PhongService.demo(memoryPhongDAO);
            MemoryVeSinhDAO memVeSinhDAO = new MemoryVeSinhDAO(memoryPhongDAO);
            veSinhDAO = memVeSinhDAO;
            MemoryBaoTriDAO memBaoTriDAO = new MemoryBaoTriDAO(memoryPhongDAO);
            baoTriDAO = memBaoTriDAO;

            // Tao du lieu mau buong phong & bao tri cho che do Demo
            BuongPhongService demoVs = new BuongPhongService(memVeSinhDAO);
            demoVs.phanCong("P102", "Dọn vệ sinh phòng tiêu chuẩn và thay ga giường", "Nguyễn Thị Lan");
            demoVs.phanCong("P201", "Dọn phòng trả theo yêu cầu lễ tân", "Trần Văn Bình");
            String firstJob = memVeSinhDAO.giaoDich(s -> s.congViecs().get(0).maVeSinh());
            demoVs.batDauDon(firstJob);

            BaoTriService demoBt = new BaoTriService(memBaoTriDAO);
            demoBt.baoHong("P103", "Điều hòa nhiệt độ kém lạnh, rò rỉ nước", "Lê Văn Nam", module.baotri.model.MucDoUuTien.CAO);
            demoBt.baoHong("P301", "Bóng đèn phòng tắm bị cháy cần thay mới", "Phạm Thu Hương", module.baotri.model.MucDoUuTien.TRUNG_BINH);
        } else {
            SqlServerPhongDAO sqlPhongDAO = new SqlServerPhongDAO(connections);
            phongDAO = sqlPhongDAO;
            phongService = new PhongService(phongDAO);
            veSinhDAO = new SqlServerVeSinhDAO(connections, sqlPhongDAO);
            baoTriDAO = new SqlServerBaoTriDAO(connections, sqlPhongDAO);
        }

        BuongPhongService buongPhongService = new BuongPhongService(veSinhDAO);
        BaoTriService baoTriService = new BaoTriService(baoTriDAO);
        phongController = new PhongController(phongService);
        buongPhongController = new BuongPhongController(buongPhongService);
        baoTriController = new BaoTriController(baoTriService);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(HotelTheme.FONT_BOLD);
        tabs.setBackground(HotelTheme.BACKGROUND);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int placement, int runs, int maxHeight) {
                return 0;
            }

            @Override
            protected int calculateTabAreaWidth(int placement, int runs, int maxWidth) {
                return 0;
            }

            @Override
            protected void paintTabArea(java.awt.Graphics g, int placement, int selectedIndex) {
                // Thanh tab duoc thay bang menu ben trai.
            }
        });
        tabs.addTab("Quản lý phòng", new PhongPanel(phongController));
        tabs.addTab("Buồng phòng & Vệ sinh", new BuongPhongPanel(buongPhongController, phongController));
        tabs.addTab("Bảo trì thiết bị", new BaoTriPanel(baoTriController, phongController));
        tabs.addChangeListener(event -> {
            if (tabs.getSelectedIndex() == 0) phongController.refresh();
            if (tabs.getSelectedIndex() == 1) buongPhongController.refresh();
            if (tabs.getSelectedIndex() == 2) baoTriController.refresh();
        });

        setLayout(new BorderLayout());
        add(taoSidebar(tabs, demo), BorderLayout.WEST);
        add(tabs, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1330, 820);
        setMinimumSize(new Dimension(1180, 680));
        setLocationRelativeTo(null);
    }

    private JPanel taoSidebar(JTabbedPane tabs, boolean demo) {
        JPanel sidebar = new JPanel(new BorderLayout(0, 28));
        sidebar.setBackground(HotelTheme.SIDEBAR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 18, 18, 18));
        sidebar.setPreferredSize(new Dimension(246, 0));

        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 5));
        brand.setOpaque(false);
        JLabel title = new JLabel("HOTEL MANAGER", javax.swing.SwingConstants.CENTER);
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("QUẢN LÝ KHÁCH SẠN", javax.swing.SwingConstants.CENTER);
        subtitle.setFont(HotelTheme.FONT_SMALL);
        subtitle.setForeground(new Color(218, 228, 239));
        brand.add(title);
        brand.add(subtitle);

        JToggleButton btnPhong = taoNutMenu("Quản lý phòng", 0, tabs);
        JToggleButton btnBuong = taoNutMenu("Buồng phòng & Vệ sinh", 1, tabs);
        JToggleButton btnBaoTri = taoNutMenu("Bảo trì thiết bị", 2, tabs);
        ButtonGroup group = new ButtonGroup();
        group.add(btnPhong);
        group.add(btnBuong);
        group.add(btnBaoTri);
        btnPhong.setSelected(true);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new javax.swing.BoxLayout(menu, javax.swing.BoxLayout.Y_AXIS));
        menu.add(Box.createVerticalStrut(10));
        menu.add(btnPhong);
        menu.add(Box.createVerticalStrut(8));
        menu.add(btnBuong);
        menu.add(Box.createVerticalStrut(8));
        menu.add(btnBaoTri);

        JPanel footer = new JPanel(new GridLayout(0, 1, 0, 10));
        footer.setOpaque(false);
        JLabel mode = new JLabel(demo ? "● Chế độ Demo" : "● SQL Server", javax.swing.SwingConstants.CENTER);
        mode.setFont(HotelTheme.FONT_SMALL);
        mode.setForeground(demo ? new Color(148, 224, 188) : new Color(165, 217, 241));
        JButton reload = HotelTheme.createButton("Tải lại tất cả", HotelTheme.ButtonType.SECONDARY, () -> {
            phongController.refresh();
            buongPhongController.refresh();
            baoTriController.refresh();
        });
        JLabel version = new JLabel("Phiên bản 1.0", javax.swing.SwingConstants.CENTER);
        version.setFont(HotelTheme.FONT_SMALL);
        version.setForeground(new Color(218, 228, 239));
        footer.add(mode);
        footer.add(reload);
        footer.add(version);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JToggleButton taoNutMenu(String text, int index, JTabbedPane tabs) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(HotelTheme.FONT_REGULAR);
        button.setForeground(Color.WHITE);
        button.setBackground(HotelTheme.SIDEBAR);
        button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 10));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.addActionListener(event -> tabs.setSelectedIndex(index));
        button.addChangeListener(event -> button.setBackground(
                button.isSelected() ? HotelTheme.SIDEBAR_ACTIVE : HotelTheme.SIDEBAR));
        return button;
    }

    public static void main(String[] args) {
        boolean demo = Arrays.asList(args).contains("--demo");
        SwingUtilities.invokeLater(() -> {
            HotelTheme.install();
            if (demo || prepareDatabaseConnection()) {
                if (!demo) {
                    try {
                        SchemaCheckNhom2.kiemTra(DatabaseConnection::getConnection);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(),
                                "Schema chua san sang", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                new PhongMainView(demo).setVisible(true);
            }
        });
    }

    private static boolean prepareDatabaseConnection() {
        if (DatabaseConnection.hasConfiguredCredentials()) {
            try (Connection ignored = DatabaseConnection.getConnection()) {
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Cau hinh ket noi hien tai khong dung duoc:\n" + ex.getMessage(),
                        "Loi ket noi SQL Server", JOptionPane.ERROR_MESSAGE);
            }
        }

        JTextField url = new JTextField(DatabaseConnection.getDefaultUrl(), 42);
        JTextField username = new JTextField(DatabaseConnection.getDefaultUsername(), 24);
        while (true) {
            JPasswordField password = new JPasswordField(24);
            JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
            form.add(new JLabel("JDBC URL:"));
            form.add(url);
            form.add(new JLabel("Ten dang nhap:"));
            form.add(username);
            form.add(new JLabel("Mat khau:"));
            form.add(password);
            int result = JOptionPane.showConfirmDialog(null, form,
                    "Ket noi SQL Server", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return false;
            char[] value = password.getPassword();
            try {
                DatabaseConnection.configureForCurrentRun(url.getText(), username.getText(), value);
                try (Connection ignored = DatabaseConnection.getConnection()) {
                    return true;
                }
            } catch (SQLException ex) {
                DatabaseConnection.clearRuntimeCredentials();
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Khong the ket noi", JOptionPane.ERROR_MESSAGE);
            } finally {
                Arrays.fill(value, '\0');
            }
        }
    }
}

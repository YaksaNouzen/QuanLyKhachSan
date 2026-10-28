package app;

import common.database.ConnectionFactory;
import common.database.DatabaseConnection;
import common.database.SchemaCheckNhom2;
import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
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

    private PhongMainView(boolean demo) {
        super("Quan ly phong - Nhom 2");
        ConnectionFactory connections = DatabaseConnection::getConnection;
        PhongDAO phongDAO;
        VeSinhDAO veSinhDAO;
        BaoTriDAO baoTriDAO;
        if (demo) {
            MemoryPhongDAO memoryPhongDAO = new MemoryPhongDAO();
            phongDAO = memoryPhongDAO;
            veSinhDAO = new MemoryVeSinhDAO(memoryPhongDAO);
            baoTriDAO = new MemoryBaoTriDAO(memoryPhongDAO);
        } else {
            SqlServerPhongDAO sqlPhongDAO = new SqlServerPhongDAO(connections);
            phongDAO = sqlPhongDAO;
            veSinhDAO = new SqlServerVeSinhDAO(connections, sqlPhongDAO);
            baoTriDAO = new SqlServerBaoTriDAO(connections, sqlPhongDAO);
        }

        PhongService phongService = demo
                ? PhongService.demo((MemoryPhongDAO) phongDAO)
                : new PhongService(phongDAO);
        BuongPhongService buongPhongService = new BuongPhongService(veSinhDAO);
        BaoTriService baoTriService = new BaoTriService(baoTriDAO);
        phongController = new PhongController(phongService);
        buongPhongController = new BuongPhongController(buongPhongService);
        baoTriController = new BaoTriController(baoTriService);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Phong", new PhongPanel(phongController));
        tabs.addTab("Buong phong", new BuongPhongPanel(buongPhongController, phongController));
        tabs.addTab("Bao tri", new BaoTriPanel(baoTriController, phongController));
        tabs.addChangeListener(event -> {
            if (tabs.getSelectedIndex() == 0) phongController.refresh();
            if (tabs.getSelectedIndex() == 1) buongPhongController.refresh();
            if (tabs.getSelectedIndex() == 2) baoTriController.refresh();
        });

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
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

package app;

import common.database.DatabaseConnection;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import module.datphong.view.DatPhongView;

/** Điểm khởi chạy duy nhất của dashboard ba module. */
public final class MainView {
    private MainView() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (prepareDatabaseConnection()) {
                new DatPhongView().setVisible(true);
            }
        });
    }

    private static boolean prepareDatabaseConnection() {
        if (DatabaseConnection.hasConfiguredCredentials()) {
            try (Connection ignored = DatabaseConnection.getConnection()) {
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Cấu hình kết nối hiện tại không sử dụng được:\n" + ex.getMessage(),
                        "Không thể kết nối cơ sở dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        JTextField url = new JTextField(DatabaseConnection.getDefaultUrl(), 42);
        JTextField username = new JTextField(DatabaseConnection.getDefaultUsername(), 24);

        while (true) {
            JPasswordField password = new JPasswordField(24);
            JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
            form.add(new JLabel("JDBC URL:"));
            form.add(url);
            form.add(new JLabel("Tên đăng nhập:"));
            form.add(username);
            form.add(new JLabel("Mật khẩu:"));
            form.add(password);

            int result = JOptionPane.showConfirmDialog(
                    null,
                    form,
                    "Kết nối SQL Server",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return false;

            char[] passwordValue = password.getPassword();
            try {
                DatabaseConnection.configureForCurrentRun(
                        url.getText(), username.getText(), passwordValue);
                try (Connection ignored = DatabaseConnection.getConnection()) {
                    return true;
                }
            } catch (SQLException ex) {
                DatabaseConnection.clearRuntimeCredentials();
                JOptionPane.showMessageDialog(
                        null,
                        "Không thể kết nối:\n" + ex.getMessage(),
                        "Sai cấu hình cơ sở dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                Arrays.fill(passwordValue, '\0');
            }
        }
    }
}

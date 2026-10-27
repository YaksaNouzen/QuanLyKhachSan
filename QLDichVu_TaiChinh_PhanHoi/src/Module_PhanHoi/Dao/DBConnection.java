package Module_PhanHoi.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        String host = System.getenv("QLPHANHOI_DB_HOST");
        String user = System.getenv("QLPHANHOI_DB_USER");
        String password = System.getenv("QLPHANHOI_DB_PASSWORD");
        if (host == null || host.isBlank() || user == null || user.isBlank()
                || password == null || password.isEmpty()) {
            throw new SQLException("Chưa cấu hình kết nối QLPhanHoi. Xem README-PHANHOI.md.");
        }
        Properties p = new Properties();
        // Properties keep credentials/server values out of URL concatenation and logs.
        p.setProperty("serverName", host);
        p.setProperty("portNumber", env("QLPHANHOI_DB_PORT", "1433"));
        p.setProperty("databaseName", "QLPhanHoi");
        p.setProperty("user", user);
        p.setProperty("password", password);
        p.setProperty("encrypt", "true");
        p.setProperty("trustServerCertificate", env("QLPHANHOI_DB_TRUST_CERT", "false"));
        p.setProperty("loginTimeout", "8");
        p.setProperty("socketTimeout", "15000");
        return DriverManager.getConnection("jdbc:sqlserver://", p);
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}

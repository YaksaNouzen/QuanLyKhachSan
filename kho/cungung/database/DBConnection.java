package kho.cungung.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Điểm kết nối duy nhất của module. Khi gộp vào project chính chỉ cần
 * thay lớp này hoặc đổi import sang DBConnection dùng chung của nhóm.
 */
public final class DBConnection {
    private static final String URL =
            "jdbc:sqlserver://YAKSANOUZEN:1433;"
            + "databaseName=QLHDKhachSan;"
            + "encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "08032006";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

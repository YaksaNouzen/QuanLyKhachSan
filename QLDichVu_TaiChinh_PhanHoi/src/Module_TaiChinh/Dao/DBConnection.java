package Module_TaiChinh.dao;

import common.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/** Cầu nối tương thích cho các DAO tài chính cũ. */
public final class DBConnection {
    private DBConnection() { }

    public static Connection getConnection() {
        try {
            return DatabaseConnection.getConnection();
        } catch (SQLException ex) {
            throw new IllegalStateException("Không kết nối được database QuanLyKhachSan.", ex);
        }
    }
}

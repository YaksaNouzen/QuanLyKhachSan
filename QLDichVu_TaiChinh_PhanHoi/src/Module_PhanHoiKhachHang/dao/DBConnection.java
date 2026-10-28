package Module_PhanHoiKhachHang.dao;

import common.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/** Cầu nối tương thích cho service phản hồi cũ. */
public final class DBConnection {
    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}

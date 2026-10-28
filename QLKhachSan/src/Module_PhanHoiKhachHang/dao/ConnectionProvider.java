package Module_PhanHoiKhachHang.dao;

import java.sql.Connection;
import java.sql.SQLException;

/** Allows an isolated test connection without changing either existing module. */
@FunctionalInterface
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;
}

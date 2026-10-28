package integration;

import common.database.DatabaseConnection;
import common.database.SchemaCheckNhom2;
import java.sql.SQLException;

public final class Nhom2ReadOnlySmokeTest {
    public static void main(String[] args) throws SQLException {
        if (Boolean.getBoolean("hotel.n2.skipSqlSmoke")) {
            System.out.println("Nhom2ReadOnlySmokeTest SKIP (chua co JDBC driver).");
            return;
        }
        if (!DatabaseConnection.hasConfiguredCredentials()) {
            System.out.println("Nhom2ReadOnlySmokeTest SKIP (chua cau hinh SQL Server).");
            return;
        }
        SchemaCheckNhom2.kiemTra(DatabaseConnection::getConnection);
        System.out.println("Nhom2ReadOnlySmokeTest PASS (chi SELECT TOP 0).");
    }
}

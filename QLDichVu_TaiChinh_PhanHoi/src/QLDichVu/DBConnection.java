
package QLDichVu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://127.0.0.1:1434;"
            + "databaseName=QLDichVu;"
            + "encrypt=false;";

    private static final String USER = "sa";
    private static final String PASSWORD = "12345";

    private DBConnection() {
    }

    public static Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(
                URL, USER, PASSWORD
        );
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("KET NOI SQL SERVER THANH CONG!");
            System.out.println("Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("KET NOI THAT BAI!");
            e.printStackTrace();
        }
    }
}

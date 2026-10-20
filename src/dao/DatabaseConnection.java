
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:sqlserver://Maryne_laptop\\MSSQLSERVER04;"
        + "databaseName=HotelManagementDB_LocalSimple;"
        + "encrypt=true;"
        + "trustServerCertificate=true;";

    private static final String USERNAME = "hotelbooking_app";
    private static final String PASSWORD = "214006";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

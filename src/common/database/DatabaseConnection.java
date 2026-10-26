
package common.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:sqlserver://localhost\\MSSQLSERVER04;"
            + "databaseName=HotelManagementDB_LocalSimple;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=10;";
    private static volatile String runtimeUrl;
    private static volatile String runtimeUsername;
    private static volatile String runtimePassword;

    public static Connection getConnection() throws SQLException {
        String url = firstNonBlank(
                runtimeUrl,
                config("hotel.db.url", "HOTEL_DB_URL", DEFAULT_URL));
        String username = firstNonBlank(
                runtimeUsername,
                config("hotel.db.username", "HOTEL_DB_USERNAME", null));
        String password = firstNonBlank(
                runtimePassword,
                config("hotel.db.password", "HOTEL_DB_PASSWORD", null));
        if (username == null || password == null) {
            throw new SQLException(
                    "Chưa cấu hình kết nối cơ sở dữ liệu. "
                    + "Hãy khởi chạy ứng dụng bằng app.MainView.");
        }
        Connection connection = DriverManager.getConnection(url, username, password);
        try {
            configureSession(connection);
            return connection;
        } catch (SQLException ex) {
            connection.close();
            throw ex;
        }
    }

    public static boolean hasConfiguredCredentials() {
        return firstNonBlank(
                runtimeUsername,
                config("hotel.db.username", "HOTEL_DB_USERNAME", null)) != null
                && firstNonBlank(
                        runtimePassword,
                        config("hotel.db.password", "HOTEL_DB_PASSWORD", null)) != null;
    }

    public static void configureForCurrentRun(
            String url,
            String username,
            char[] password) {
        runtimeUrl = firstNonBlank(url, DEFAULT_URL);
        runtimeUsername = firstNonBlank(username, null);
        runtimePassword = password == null ? null : new String(password);
    }

    public static void clearRuntimeCredentials() {
        runtimeUsername = null;
        runtimePassword = null;
    }

    public static String getDefaultUrl() {
        return config("hotel.db.url", "HOTEL_DB_URL", DEFAULT_URL);
    }

    public static String getDefaultUsername() {
        return config("hotel.db.username", "HOTEL_DB_USERNAME", "hotelbooking_app");
    }

    private static String config(
            String propertyName,
            String environmentName,
            String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentName);
        }
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) return first.trim();
        if (second != null && !second.isBlank()) return second.trim();
        return null;
    }

    private static void configureSession(Connection connection) throws SQLException {
        String sql = "SET ANSI_NULLS ON; "
                + "SET QUOTED_IDENTIFIER ON; "
                + "SET ANSI_PADDING ON; "
                + "SET ANSI_WARNINGS ON; "
                + "SET CONCAT_NULL_YIELDS_NULL ON; "
                + "SET ARITHABORT ON; "
                + "SET NUMERIC_ROUNDABORT OFF;";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}

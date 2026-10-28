import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Có thể cấu hình riêng trên từng máy bằng biến môi trường.
    private static final String SERVER = env("HOTEL_DB_SERVER", "localhost");

    // Tên database
    private static final String DATABASE = env("HOTEL_DB_NAME", "QLNhanVien");

    // Tài khoản SQL Server
    private static final String USER = env("HOTEL_DB_USER", "sa");

    // Không ghi mật khẩu trực tiếp trong source/Git.
    private static final String PASSWORD = env("HOTEL_DB_PASSWORD", "");

    private static final String URL =
            "jdbc:sqlserver://" + SERVER + ":1433;"
            + "databaseName=" + DATABASE + ";"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static Connection getConnection() {

        try {
            if (PASSWORD.isBlank()) {
                throw new SQLException(
                        "Chưa cấu hình biến môi trường HOTEL_DB_PASSWORD.");
            }
            Connection conn = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Ket noi SQL Server thanh cong!");

            return conn;

        } catch (SQLException e) {

            System.out.println("Ket noi SQL Server that bai!");
            e.printStackTrace();

            return null;
        }
    }
}

package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Lớp kết nối SQL Server.
 * Cần thêm thư viện mssql-jdbc-xx.jar vào Libraries của project.
 * Nếu nhóm đã có lớp kết nối chung thì dùng lớp đó, bỏ file này.
 *
 * CHẾ ĐỘ CHẠY THỬ: máy chưa có SQL Server thì dùng H2 (database nằm trong
 * bộ nhớ, thư viện h2-xx.jar) với dữ liệu mẫu trong file du_lieu_chay_thu.sql.
 * Các lớp DAO không cần sửa gì vì vẫn dùng chung hàm getConnection().
 */
public class DBConnection {

    private static final String SERVER = env("HOTEL_DB_SERVER", "localhost");
    private static final String DATABASE = env("HOTEL_DB_NAME", "QuanLyKhachSan");
    private static final String USER = env("HOTEL_DB_USER", "sa");
    private static final String PASSWORD = env("HOTEL_DB_PASSWORD", "");

    private static final String URL = "jdbc:sqlserver://" + SERVER + ":1433;"
            + "databaseName=" + DATABASE + ";"
            + "encrypt=true;trustServerCertificate=true;"
            + "loginTimeout=5";   // quá 5 giây không kết nối được thì báo lỗi luôn

    // H2 giả lập SQL Server, DB_CLOSE_DELAY=-1 để dữ liệu còn đến khi tắt chương trình
    private static final String URL_CHAY_THU = "jdbc:h2:mem:QuanLyKhachSan;MODE=MSSQLServer;DB_CLOSE_DELAY=-1";

    private static boolean cheDoChayThu = false;

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static Connection getConnection() throws SQLException {
        if (cheDoChayThu) {
            return DriverManager.getConnection(URL_CHAY_THU, "sa", "");
        }
        if (PASSWORD.isBlank()) {
            throw new SQLException("Chưa cấu hình biến môi trường HOTEL_DB_PASSWORD.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Thử mở kết nối, dùng ở màn đăng nhập để báo lỗi rõ ràng khi chưa bật SQL Server
    public static boolean kiemTraKetNoi() {
        try (Connection conn = getConnection()) {
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
            return false;
        }
    }

    // Chuyển sang chạy thử: tạo bảng và dữ liệu mẫu trong H2 (chỉ làm 1 lần)
    public static boolean batCheDoChayThu() {
        if (cheDoChayThu) {
            return true;
        }
        try (Connection conn = DriverManager.getConnection(URL_CHAY_THU, "sa", "");
                Statement st = conn.createStatement()) {
            st.execute("RUNSCRIPT FROM 'classpath:util/du_lieu_chay_thu.sql' CHARSET 'UTF-8'");
            cheDoChayThu = true;
            return true;
        } catch (SQLException e) {
            System.out.println("Không bật được chế độ chạy thử: " + e.getMessage());
            return false;
        }
    }

    public static boolean laCheDoChayThu() {
        return cheDoChayThu;
    }
}

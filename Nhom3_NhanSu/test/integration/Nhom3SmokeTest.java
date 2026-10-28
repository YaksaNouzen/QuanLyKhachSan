package integration;

import dao.CaLamDAO;
import dao.NhanVienDAO;
import dao.PhanCaDAO;
import dao.TaiKhoanDAO;
import dao.VaiTroDAO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import javax.swing.SwingUtilities;
import model.CaLam;
import model.PhanCa;
import model.TaiKhoan;
import util.DBConnection;
import util.PhienDangNhap;
import view.CaLamPanel;
import view.NhanVienPanel;
import view.PhanCaPanel;
import view.TaiKhoanPanel;
import view.TrangChuPanel;

public final class Nhom3SmokeTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static int count(Connection connection, String table) throws Exception {
        try (Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            result.next();
            return result.getInt(1);
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        check(DBConnection.batCheDoChayThu(), "Không bật được H2 chạy thử");

        try (Connection connection = DBConnection.getConnection()) {
            for (String table : new String[]{"VaiTro", "NhanVien", "TaiKhoan", "CaLam", "PhanCa"}) {
                check(count(connection, table) > 0, "Bảng " + table + " không có dữ liệu mẫu");
            }
        }

        NhanVienDAO nhanVienDAO = new NhanVienDAO();
        CaLamDAO caLamDAO = new CaLamDAO();
        PhanCaDAO phanCaDAO = new PhanCaDAO();
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        VaiTroDAO vaiTroDAO = new VaiTroDAO();
        check(!nhanVienDAO.getAll().isEmpty(), "Không tải được nhân viên");
        check(!caLamDAO.getAll().isEmpty(), "Không tải được ca làm");
        check(!vaiTroDAO.getAll().isEmpty(), "Không tải được vai trò");
        check(!taiKhoanDAO.getAll().isEmpty(), "Không tải được tài khoản");

        LocalDate today = LocalDate.now();
        check(!phanCaDAO.getLichTruc(
                Date.valueOf(today.minusDays(1)), Date.valueOf(today.plusDays(2)), "").isEmpty(),
                "Không tải được lịch trực");

        TaiKhoan admin = taiKhoanDAO.dangNhap("admin", "123456");
        check(admin != null, "Admin không đăng nhập được");
        check(taiKhoanDAO.dangNhap("admin", "sai-mat-khau") == null,
                "Mật khẩu sai vẫn đăng nhập được");
        PhienDangNhap.setTaiKhoan(admin);

        check(phanCaDAO.them(new PhanCa("NV005", "CA3", Date.valueOf(today.minusDays(1)))),
                "Không tạo được ca đêm ngày trước");
        CaLam caSom = new CaLam("TEST1", "Ca sớm", "05:00", "07:00");
        check(phanCaDAO.kiemTraTrungCa("NV005", caSom, Date.valueOf(today)) != null,
                "Không phát hiện ca đêm hôm trước trùng ca sáng hôm nay");

        check(phanCaDAO.them(new PhanCa("NV005", "CA1", Date.valueOf(today.plusDays(1)))),
                "Không tạo được ca sáng ngày sau");
        CaLam caDemMoi = new CaLam("TEST2", "Ca đêm mới", "22:00", "07:00");
        check(phanCaDAO.kiemTraTrungCa("NV005", caDemMoi, Date.valueOf(today)) != null,
                "Không phát hiện ca đêm hôm nay trùng ca sáng ngày sau");

        SwingUtilities.invokeAndWait(() -> {
            new TrangChuPanel();
            new NhanVienPanel(false);
            new CaLamPanel();
            new PhanCaPanel();
            new TaiKhoanPanel();
        });

        System.out.println("NHOM3_SMOKE_TEST_OK");
    }
}

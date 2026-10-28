package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import util.DBConnection;

/**
 * Các câu thống kê nhân sự hiện trên Trang chủ.
 * Dùng COUNT và GROUP BY để database tự đếm, không phải lấy hết dữ liệu về Java.
 */
public class ThongKeDAO {

    // Đếm nhân viên theo trạng thái. VD: {Đang làm=4, Nghỉ phép=1}
    public Map<String, Integer> demTheoTrangThai() {
        return demNhom("SELECT TrangThai, COUNT(*) AS SoLuong FROM NhanVien "
                + "GROUP BY TrangThai ORDER BY SoLuong DESC, TrangThai");
    }

    // Đếm nhân viên theo bộ phận (bỏ qua người đã nghỉ việc)
    public Map<String, Integer> demTheoBoPhan() {
        return demNhom("SELECT BoPhan, COUNT(*) AS SoLuong FROM NhanVien "
                + "WHERE TrangThai <> N'Đã nghỉ việc' "
                + "GROUP BY BoPhan ORDER BY SoLuong DESC, BoPhan");
    }

    // Số ca được phân trong ngày hôm nay
    public int demCaHomNay() {
        return demSo("SELECT COUNT(*) FROM PhanCa WHERE NgayLam = ?");
    }

    // Số ca từ hôm nay trở về trước mà chưa bàn giao (ca ngày mai chưa tính)
    public int demCaChuaBanGiao() {
        return demSo("SELECT COUNT(*) FROM PhanCa WHERE TrangThai = N'Chưa bàn giao' AND NgayLam <= ?");
    }

    // Chạy câu GROUP BY, cột 1 là tên nhóm, cột 2 là số lượng
    private Map<String, Integer> demNhom(String sql) {
        Map<String, Integer> ketQua = new LinkedHashMap<>();   // giữ đúng thứ tự ORDER BY
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ketQua.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // Chạy câu COUNT có 1 tham số là ngày hôm nay
    private int demSo(String sql) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

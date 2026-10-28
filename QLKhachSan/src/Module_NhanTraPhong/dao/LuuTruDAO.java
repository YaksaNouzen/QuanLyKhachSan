package Module_NhanTraPhong.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import common.database.DatabaseConnection;
import Module_TaiChinh.dao.HoaDonLuuTruDAO;
import Module_NhanTraPhong.model.LuuTru;

public class LuuTruDAO {
    public List<LuuTru> timLuuTru(String tuKhoa, String trangThai)
            throws SQLException {
        String keyword = tuKhoa == null ? "" : tuKhoa.trim();
        String status = trangThai == null ? "" : trangThai.trim();
        String like = "%" + keyword + "%";
        String sql = "SELECT dp.MaDatPhong, dp.MaKhachHang, kh.HoTen, "
                + "kh.SoDienThoai, dp.MaPhong, p.SoPhong, "
                + "dp.NgayNhanDuKien, dp.NgayTraDuKien, "
                + "dp.NgayNhan, dp.NgayTra, dp.TrangThai, "
                + "CASE WHEN dp.TrangThai='DANG_O' "
                + "AND CONVERT(datetime2(0), SYSDATETIME()) > dp.NgayTraDuKien "
                + "THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END AS QuaHanTraPhong "
                + "FROM dbo.DatPhong dp "
                + "JOIN dbo.KhachHang kh ON kh.MaKhachHang=dp.MaKhachHang "
                + "JOIN dbo.Phong p ON p.MaPhong=dp.MaPhong "
                + "WHERE dp.TrangThai IN ('GIU_PHONG','DANG_O','DA_TRA') "
                + "AND (?='' OR dp.MaDatPhong LIKE ? OR dp.MaKhachHang LIKE ? "
                + "OR kh.HoTen LIKE ? OR kh.SoDienThoai LIKE ?) ";
        if ("QUA_HAN_TRA".equals(status)) {
            sql += "AND dp.TrangThai='DANG_O' "
                    + "AND CONVERT(datetime2(0), SYSDATETIME()) > dp.NgayTraDuKien ";
        } else if (!status.isEmpty()) {
            sql += "AND dp.TrangThai=? ";
        }
        sql += "ORDER BY CASE WHEN dp.TrangThai='DANG_O' "
                + "AND CONVERT(datetime2(0), SYSDATETIME()) > dp.NgayTraDuKien "
                + "THEN 0 ELSE 1 END, dp.NgayNhanDuKien DESC";
        List<LuuTru> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyword);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            statement.setString(5, like);
            if (!status.isEmpty() && !"QUA_HAN_TRA".equals(status)) {
                statement.setString(6, status);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    LuuTru item = new LuuTru();
                    item.setMaDatPhong(rs.getString("MaDatPhong"));
                    item.setMaKhachHang(rs.getString("MaKhachHang"));
                    item.setHoTen(rs.getString("HoTen"));
                    item.setSoDienThoai(rs.getString("SoDienThoai"));
                    item.setMaPhong(rs.getString("MaPhong"));
                    item.setSoPhong(rs.getString("SoPhong"));
                    item.setNgayNhanDuKien(rs.getTimestamp("NgayNhanDuKien"));
                    item.setNgayTraDuKien(rs.getTimestamp("NgayTraDuKien"));
                    item.setNgayNhan(rs.getTimestamp("NgayNhan"));
                    item.setNgayTra(rs.getTimestamp("NgayTra"));
                    item.setTrangThai(rs.getString("TrangThai"));
                    item.setQuaHanTraPhong(rs.getBoolean("QuaHanTraPhong"));
                    result.add(item);
                }
            }
        }
        return result;
    }

    public void nhanPhong(String maDatPhong) throws SQLException {
        execute("{CALL dbo.sp_DatPhong_NhanPhong(?)}", maDatPhong);
    }

    public void traPhong(String maDatPhong) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (CallableStatement statement = connection.prepareCall(
                        "{CALL dbo.sp_DatPhong_TraPhong(?)}")) {
                    statement.setString(1, maDatPhong);
                    statement.execute();
                }
                new HoaDonLuuTruDAO().taoChoDonDaTra(connection, maDatPhong);
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    private void execute(String sql, String maDatPhong) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.setString(1, maDatPhong);
            statement.execute();
        }
    }
}

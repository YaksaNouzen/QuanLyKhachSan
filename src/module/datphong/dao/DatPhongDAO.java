
package module.datphong.dao;

import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.math.BigDecimal;
import common.database.DatabaseConnection;
import module.datphong.model.DatPhong;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatPhongDAO {

    
public List<DatPhong> layDanhSachDatPhong() throws SQLException {
    List<DatPhong> danhSach = new ArrayList<>();

    String sql =
        "SELECT MaDatPhong, MaKhachHang, MaPhong, GiaPhongChot, "
        + "NgayDat, NgayNhan, NgayTra, "
        + "NgayNhanDuKien, NgayTraDuKien, TrangThai "
        + "FROM dbo.DatPhong "
        + "ORDER BY NgayNhanDuKien DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            DatPhong datPhong = new DatPhong();

            datPhong.setMaDatPhong(rs.getString("MaDatPhong"));
            datPhong.setMaKhachHang(rs.getString("MaKhachHang"));
            datPhong.setMaPhong(rs.getString("MaPhong"));
            datPhong.setGiaPhongChot(rs.getBigDecimal("GiaPhongChot"));

            datPhong.setNgayDat(rs.getTimestamp("NgayDat"));
            datPhong.setNgayNhan(rs.getTimestamp("NgayNhan"));
            datPhong.setNgayTra(rs.getTimestamp("NgayTra"));

            datPhong.setNgayNhanDuKien(
                rs.getTimestamp("NgayNhanDuKien"));
            datPhong.setNgayTraDuKien(
                rs.getTimestamp("NgayTraDuKien"));

            datPhong.setTrangThai(rs.getString("TrangThai"));

            danhSach.add(datPhong);
        }
    }

    return danhSach;
}

    public void taoDatPhong(DatPhong dp) throws SQLException {

    String sql = "{CALL dbo.sp_DatPhong_TaoMoi(?, ?, ?, ?, ?, ?, ?)}";

    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        cs.setString(1, dp.getMaDatPhong());
        cs.setString(2, dp.getMaKhachHang());
        cs.setString(3, dp.getMaPhong());
        cs.setBigDecimal(4, dp.getGiaPhongChot());

        cs.setTimestamp(5, dp.getNgayNhanDuKien());
        cs.setTimestamp(6, dp.getNgayTraDuKien());

        // Chưa có lượt lưu trú tại thời điểm tạo đặt phòng.
        cs.setNull(7, java.sql.Types.VARCHAR);

        cs.execute();
    }

    }
    

public void capNhatLichDatPhong(
        DatPhong dp,
        Timestamp ngayNhanGoc,
        Timestamp ngayTraGoc) throws SQLException {

    String sql =
            "{CALL dbo.sp_DatPhong_CapNhatLich(?, ?, ?, ?, ?)}";

    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        cs.setString(1, dp.getMaDatPhong());
        cs.setTimestamp(2, dp.getNgayNhanDuKien());
        cs.setTimestamp(3, dp.getNgayTraDuKien());
        cs.setTimestamp(4, ngayNhanGoc);
        cs.setTimestamp(5, ngayTraGoc);

        cs.execute();
    }
}


    
public void huyDatPhong(String maDatPhong) throws SQLException {
    String sql = "{CALL dbo.sp_DatPhong_Huy(?)}";

    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        cs.setString(1, maDatPhong);
        cs.execute();
    }
}
public void xoaDatPhong(String maDatPhong) throws SQLException {
String sql = "{CALL dbo.sp_DatPhong_Xoa(?)}";

try (Connection conn = DatabaseConnection.getConnection();
     CallableStatement cs = conn.prepareCall(sql)) {

    cs.setString(1, maDatPhong);
    cs.execute();
}

}


public List<String> timPhongTrong(
        Timestamp ngayNhanDuKien,
        Timestamp ngayTraDuKien) throws SQLException {

    if (ngayNhanDuKien == null || ngayTraDuKien == null
            || !ngayTraDuKien.after(ngayNhanDuKien)) {
        throw new IllegalArgumentException(
                "Thoi gian nhan va tra du kien khong hop le.");
    }

    List<String> danhSachPhong = new ArrayList<>();

    String sql =
        "SELECT p.MaPhong, p.SoPhong " +
        "FROM dbo.Phong p " +
        "WHERE p.TrangThai = 'TRONG' " +
        "AND p.NgungSuDung = 0 " +
        "AND NOT EXISTS ( " +
        "    SELECT 1 FROM dbo.DatPhong dp " +
        "    WHERE dp.MaPhong = p.MaPhong " +
        "      AND dp.TrangThai IN ('GIU_PHONG', 'DANG_O') " +
        "      AND ? < dp.NgayTraDuKien " +
        "      AND ? > dp.NgayNhanDuKien " +
        ") " +
        "ORDER BY p.SoPhong";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setTimestamp(1, ngayNhanDuKien);
        ps.setTimestamp(2, ngayTraDuKien);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String maPhong = rs.getString("MaPhong");
                String soPhong = rs.getString("SoPhong");

                danhSachPhong.add(
                        maPhong + " - Phong " + soPhong
                );
            }
        }
    }

    return danhSachPhong;
}

public List<String> timPhongTrongDeDoi(String maDatPhongCu) throws SQLException {
    String sourceSql = "SELECT MaPhong, NgayTraDuKien, TrangThai, "
            + "CASE WHEN TrangThai='DANG_O' "
            + "THEN CONVERT(datetime2(0),SYSDATETIME()) "
            + "ELSE NgayNhanDuKien END AS KhoangBatDau "
            + "FROM dbo.DatPhong WHERE MaDatPhong=?";
    String availableSql =
            "SELECT p.MaPhong, p.SoPhong, lp.TenLoai, "
            + "COALESCE(p.GiaPhong, lp.GiaTheoNgay) AS GiaThamKhao "
            + "FROM dbo.Phong p "
            + "JOIN dbo.LoaiPhong lp ON lp.MaLoaiPhong=p.MaLoaiPhong "
            + "WHERE p.MaPhong<>? AND p.TrangThai='TRONG' "
            + "AND p.NgungSuDung=0 "
            + "AND NOT EXISTS (SELECT 1 FROM dbo.DatPhong dp "
            + "WHERE dp.MaPhong=p.MaPhong "
            + "AND (dp.TrangThai='DANG_O' OR (dp.TrangThai='GIU_PHONG' "
            + "AND ? < dp.NgayTraDuKien AND ? > dp.NgayNhanDuKien))) "
            + "ORDER BY p.SoPhong";

    try (Connection connection = DatabaseConnection.getConnection()) {
        String oldRoom;
        Timestamp arrival;
        Timestamp departure;
        String status;
        try (PreparedStatement statement = connection.prepareStatement(sourceSql)) {
            statement.setString(1, maDatPhongCu);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Đơn đặt phòng cũ không tồn tại.");
                }
                oldRoom = rs.getString("MaPhong");
                arrival = rs.getTimestamp("KhoangBatDau");
                departure = rs.getTimestamp("NgayTraDuKien");
                status = rs.getString("TrangThai");
            }
        }
        if (!"GIU_PHONG".equals(status) && !"DANG_O".equals(status)) {
            throw new SQLException(
                    "Chỉ được gợi ý đổi phòng cho đơn GIU_PHONG hoặc DANG_O.");
        }
        if (!departure.after(arrival)) {
            throw new SQLException(
                    "Đơn đã quá hạn trả. Hãy gia hạn trước khi đổi phòng.");
        }

        List<String> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(availableSql)) {
            statement.setString(1, oldRoom);
            statement.setTimestamp(2, arrival);
            statement.setTimestamp(3, departure);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("MaPhong")
                            + " - Phòng " + rs.getString("SoPhong")
                            + " - " + rs.getString("TenLoai")
                            + " - " + rs.getBigDecimal("GiaThamKhao") + " VNĐ/ngày");
                }
            }
        }
        return result;
    }
}


public void doiPhong(
        String maDatPhongCu,
        String maPhongMoi,
        String lyDo,
        String nguoiThucHien,
        String maDatPhongMoi,
        String maChuyenPhong) throws SQLException {

    String sql = "{CALL dbo.sp_DatPhong_DoiPhong(?, ?, ?, ?, ?, ?)}";

    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        cs.setString(1, maDatPhongCu);
        cs.setString(2, maPhongMoi);
        cs.setString(3, lyDo);

        if (nguoiThucHien == null
                || nguoiThucHien.trim().isEmpty()) {
            cs.setNull(4, java.sql.Types.NVARCHAR);
        } else {
            cs.setString(4, nguoiThucHien);
        }

        cs.setString(5, maDatPhongMoi);
        cs.setString(6, maChuyenPhong);

        cs.execute();
    }
}


}

package Module_DichVu.dao;

import Module_DichVu.DBConnection;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Ghi nhận dịch vụ cho đơn đang ở, chốt đơn giá ngay lúc sử dụng. */
public class SuDungDichVuDAO {
    public String ghiNhan(String maDatPhong, int maDichVu, int soLuong) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT TrangThai FROM dbo.DatPhong WITH (UPDLOCK,HOLDLOCK) WHERE MaDatPhong=?")) {
                    ps.setString(1, maDatPhong);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !"DANG_O".equals(rs.getString(1))) {
                            throw new SQLException("Chỉ ghi dịch vụ cho đơn đang ở.");
                        }
                    }
                }
                BigDecimal donGia;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT DonGia,TrangThai FROM dbo.DichVu WHERE MaDV=?")) {
                    ps.setInt(1, maDichVu);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("TrangThai")) {
                            throw new SQLException("Dịch vụ không tồn tại hoặc đã ngừng cung cấp.");
                        }
                        donGia = rs.getBigDecimal("DonGia");
                    }
                }
                String maSuDung;
                try (CallableStatement cs = con.prepareCall("{CALL dbo.sp_LayMaTuDong(?)}")) {
                    cs.setString(1, "SU_DUNG_DICH_VU");
                    try (ResultSet rs = cs.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Không cấp được mã sử dụng dịch vụ.");
                        maSuDung = rs.getString("MaMoi");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO dbo.SuDungDichVu "
                        + "(MaSuDung,MaDatPhong,MaDichVu,SoLuong,DonGia) VALUES (?,?,?,?,?)")) {
                    ps.setString(1, maSuDung);
                    ps.setString(2, maDatPhong);
                    ps.setInt(3, maDichVu);
                    ps.setInt(4, soLuong);
                    ps.setBigDecimal(5, donGia);
                    ps.executeUpdate();
                }
                con.commit();
                return maSuDung;
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }
}

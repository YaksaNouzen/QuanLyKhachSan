package common.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaCheckNhom2 {
    private SchemaCheckNhom2() {}

    public static void kiemTra(ConnectionFactory connections) throws SQLException {
        try (Connection connection = connections.open()) {
            kiemTraBang(connection,
                    "Phong",
                    "SELECT TOP 0 MaPhong,SoPhong,MaLoaiPhong,MaTang,GiaPhong,TrangThai,NgungSuDung "
                            + "FROM dbo.Phong");
            kiemTraBang(connection,
                    "LoaiPhong",
                    "SELECT TOP 0 MaLoaiPhong,TenLoai,GiaTheoNgay,SoNguoiToiDa FROM dbo.LoaiPhong");
            kiemTraBang(connection,
                    "Tang",
                    "SELECT TOP 0 MaTang,TenTang FROM dbo.Tang");
            kiemTraBang(connection,
                    "DatPhong",
                    "SELECT TOP 0 MaDatPhong,MaPhong,TrangThai,NgayTraDuKien FROM dbo.DatPhong");
            kiemTraBang(connection,
                    "VeSinh",
                    "SELECT TOP 0 MaVeSinh,MaPhong,NoiDung,NguoiPhuTrach,BatDau,KetThuc,"
                            + "TrangThai,NgayPhanCong,NguoiXacNhan,XacNhanLuc FROM dbo.VeSinh");
            kiemTraBang(connection,
                    "BaoTri",
                    "SELECT TOP 0 MaBaoTri,MaPhong,NoiDung,NguoiPhuTrach,BatDau,KetThuc,"
                            + "TrangThai,MucDo,NguoiBao,NgayBao,ChiPhi,KetQua FROM dbo.BaoTri");
        }
    }

    private static void kiemTraBang(Connection connection, String tenBang, String sql)
            throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeQuery(sql).close();
        } catch (SQLException ex) {
            String goiY = tenBang.equals("VeSinh") || tenBang.equals("BaoTri")
                    ? " Hay chay sql/patches/N2_001_buong_phong_va_bao_tri.sql."
                    : " Hay doi chieu database voi schema cua nhanh QuanLyDatPhong.";
            throw new SQLException(
                    "Schema thieu bang/cot cua dbo." + tenBang + "." + goiY,
                    ex.getSQLState(), ex.getErrorCode(), ex);
        }
    }
}

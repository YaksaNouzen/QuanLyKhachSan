package Module_TaiChinh.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Lập hóa đơn cho một chặng lưu trú, dùng chung giao dịch với trả/đổi phòng. */
public final class HoaDonLuuTruDAO {
    public String taoChoDonDaTra(Connection con, String maDatPhong) throws SQLException {
        String sql = "SELECT dp.MaPhong, dp.GiaPhongChot, dp.NgayNhanDuKien, "
                + "dp.NgayTraDuKien, dp.TrangThai, cp.ThoiGian AS LucDoiPhong "
                + "FROM dbo.DatPhong dp OUTER APPLY (SELECT TOP (1) ThoiGian "
                + "FROM dbo.ChuyenPhong WHERE MaDatPhongCu=dp.MaDatPhong "
                + "ORDER BY ThoiGian DESC) cp WHERE dp.MaDatPhong=?";
        String maPhong;
        BigDecimal giaPhong;
        LocalDate batDau;
        LocalDate ketThuc;
        boolean doiPhongDi;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !"DA_TRA".equals(rs.getString("TrangThai"))) {
                    throw new SQLException("Chỉ lập hóa đơn cho đơn đã trả phòng.");
                }
                maPhong = rs.getString("MaPhong");
                giaPhong = rs.getBigDecimal("GiaPhongChot");
                batDau = rs.getTimestamp("NgayNhanDuKien").toLocalDateTime().toLocalDate();
                Timestamp lucDoi = rs.getTimestamp("LucDoiPhong");
                doiPhongDi = lucDoi != null;
                ketThuc = (lucDoi == null ? rs.getTimestamp("NgayTraDuKien") : lucDoi)
                        .toLocalDateTime().toLocalDate();
            }
        }

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT MaHoaDon FROM dbo.HoaDon WITH (UPDLOCK,HOLDLOCK) WHERE MaDatPhong=?")) {
            ps.setString(1, maDatPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }

        long soDem = tinhSoDem(batDau, ketThuc, doiPhongDi);
        if (soDem > Integer.MAX_VALUE) throw new SQLException("Số đêm vượt giới hạn.");
        BigDecimal tong = BigDecimal.ZERO;
        String maHoaDon = layMa(con, "HOA_DON");
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO dbo.HoaDon (MaHoaDon,MaDatPhong,NgayLap,TienGiam,TongTien,TrangThai) "
                + "VALUES (?,?,CONVERT(date,GETDATE()),0,0,N'Chưa thanh toán')")) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maDatPhong);
            ps.executeUpdate();
        }

        // Ngày đổi phòng thuộc chặng mới; chặng cũ có thể có 0 đêm nhưng vẫn
        // cần hóa đơn để ghi các dịch vụ đã sử dụng trước khi đổi phòng.
        if (soDem > 0) {
            BigDecimal thanhTien = giaPhong.multiply(BigDecimal.valueOf(soDem));
            themChiTiet(con, maHoaDon, maPhong, null, "Phòng",
                    "Tiền phòng " + maPhong + " - " + soDem + " đêm", (int) soDem,
                    giaPhong, thanhTien);
            tong = tong.add(thanhTien);
        }

        String dichVuSql = "SELECT sd.MaDichVu,dv.TenDV,sd.SoLuong,sd.DonGia,sd.ThanhTien "
                + "FROM dbo.SuDungDichVu sd JOIN dbo.DichVu dv ON dv.MaDV=sd.MaDichVu "
                + "WHERE sd.MaDatPhong=? ORDER BY sd.ThoiGianSuDung,sd.MaSuDung";
        List<DongDichVu> dichVu = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(dichVuSql)) {
            ps.setString(1, maDatPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dichVu.add(new DongDichVu(rs.getInt("MaDichVu"),
                            rs.getString("TenDV"), rs.getInt("SoLuong"),
                            rs.getBigDecimal("DonGia"), rs.getBigDecimal("ThanhTien")));
                }
            }
        }
        for (DongDichVu dong : dichVu) {
            themChiTiet(con, maHoaDon, null, dong.maDichVu(), "Dịch vụ",
                    dong.ten(), dong.soLuong(), dong.donGia(), dong.thanhTien());
            tong = tong.add(dong.thanhTien());
        }
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE dbo.HoaDon SET TongTien=? WHERE MaHoaDon=?")) {
            ps.setBigDecimal(1, tong);
            ps.setString(2, maHoaDon);
            ps.executeUpdate();
        }
        return maHoaDon;
    }

    private void themChiTiet(Connection con, String maHoaDon, String maPhong,
            Integer maDichVu, String loai, String moTa, int soLuong,
            BigDecimal donGia, BigDecimal thanhTien) throws SQLException {
        String sql = "INSERT INTO dbo.ChiTietHoaDon "
                + "(MaChiTiet,MaHoaDon,MaPhong,MaDichVu,LoaiChiPhi,MoTa,SoLuong,DonGia,ThanhTien) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, layMa(con, "CHI_TIET_HOA_DON"));
            ps.setString(2, maHoaDon);
            if (maPhong == null) ps.setNull(3, java.sql.Types.VARCHAR);
            else ps.setString(3, maPhong);
            if (maDichVu == null) ps.setNull(4, java.sql.Types.INTEGER);
            else ps.setInt(4, maDichVu);
            ps.setString(5, loai);
            ps.setString(6, moTa);
            ps.setInt(7, soLuong);
            ps.setBigDecimal(8, donGia);
            ps.setBigDecimal(9, thanhTien);
            ps.executeUpdate();
        }
    }

    private String layMa(Connection con, String loai) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{CALL dbo.sp_LayMaTuDong(?)}")) {
            cs.setString(1, loai);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return rs.getString("MaMoi");
                throw new SQLException("Không cấp được mã " + loai + ".");
            }
        }
    }

    static long tinhSoDem(LocalDate batDau, LocalDate ketThuc, boolean doiPhongDi) {
        return Math.max(doiPhongDi ? 0 : 1,
                ChronoUnit.DAYS.between(batDau, ketThuc));
    }

    private record DongDichVu(int maDichVu, String ten, int soLuong,
            BigDecimal donGia, BigDecimal thanhTien) { }
}

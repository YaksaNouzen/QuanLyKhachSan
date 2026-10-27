package Module_TaiChinh.Dao;

import Module_TaiChinh.model.ThongKeTaiChinh;
import Module_TaiChinh.model.ThongKeTheoNgay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeTaiChinhDAO {

    public ThongKeTaiChinh thongKeTongQuan(
            String tuNgay, String denNgay) {

        StringBuilder sql = new StringBuilder("""
            WITH ThanhToanThanhCong AS (
                SELECT MaHoaDon, SUM(SoTien) AS DaTra
                FROM ThanhToan
                WHERE TrangThai IN (N'Thành công', N'Đã thanh toán')
                GROUP BY MaHoaDon
            )
            SELECT
                COUNT(*) AS SoHoaDon,
                COALESCE(SUM(CASE
                    WHEN hd.TongTien > 0
                     AND COALESCE(tt.DaTra, 0) >= hd.TongTien
                    THEN 1 ELSE 0 END), 0) AS SoDaThanhToan,
                COALESCE(SUM(CASE
                    WHEN hd.TongTien <= 0
                      OR COALESCE(tt.DaTra, 0) < hd.TongTien
                    THEN 1 ELSE 0 END), 0) AS SoChuaThanhToan,
                COALESCE(SUM(hd.TongTien), 0) AS TongDoanhThu,
                COALESCE(SUM(CASE
                    WHEN COALESCE(tt.DaTra, 0) > hd.TongTien
                    THEN hd.TongTien ELSE COALESCE(tt.DaTra, 0)
                END), 0) AS TongThucThu,
                COALESCE(SUM(CASE
                    WHEN hd.TongTien > COALESCE(tt.DaTra, 0)
                    THEN hd.TongTien - COALESCE(tt.DaTra, 0)
                    ELSE 0 END), 0) AS TongConNo
            FROM HoaDon hd
            LEFT JOIN ThanhToanThanhCong tt
                ON hd.MaHoaDon = tt.MaHoaDon
            WHERE 1 = 1
            """);

        themDieuKienNgay(sql, tuNgay, denNgay);

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            ganThamSoNgay(ps, tuNgay, denNgay);

            try (ResultSet rs = ps.executeQuery()) {
                ThongKeTaiChinh thongKe = new ThongKeTaiChinh();

                if (rs.next()) {
                    thongKe.setSoHoaDon(rs.getInt("SoHoaDon"));
                    thongKe.setSoDaThanhToan(
                            rs.getInt("SoDaThanhToan"));
                    thongKe.setSoChuaThanhToan(
                            rs.getInt("SoChuaThanhToan"));
                    thongKe.setTongDoanhThu(
                            rs.getDouble("TongDoanhThu"));
                    thongKe.setTongThucThu(
                            rs.getDouble("TongThucThu"));
                    thongKe.setTongConNo(
                            rs.getDouble("TongConNo"));
                }

                return thongKe;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Loi khi thong ke tong quan: "
                            + e.getMessage(), e);
        }
    }

    public List<ThongKeTheoNgay> thongKeTheoNgay(
            String tuNgay, String denNgay) {

        List<ThongKeTheoNgay> danhSach = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            WITH ThanhToanThanhCong AS (
                SELECT MaHoaDon, SUM(SoTien) AS DaTra
                FROM ThanhToan
                WHERE TrangThai IN (N'Thành công', N'Đã thanh toán')
                GROUP BY MaHoaDon
            )
            SELECT
                hd.NgayLap,
                COUNT(*) AS SoHoaDon,
                COALESCE(SUM(hd.TongTien), 0) AS DoanhThu,
                COALESCE(SUM(CASE
                    WHEN COALESCE(tt.DaTra, 0) > hd.TongTien
                    THEN hd.TongTien ELSE COALESCE(tt.DaTra, 0)
                END), 0) AS ThucThu,
                COALESCE(SUM(CASE
                    WHEN hd.TongTien > COALESCE(tt.DaTra, 0)
                    THEN hd.TongTien - COALESCE(tt.DaTra, 0)
                    ELSE 0 END), 0) AS ConNo
            FROM HoaDon hd
            LEFT JOIN ThanhToanThanhCong tt
                ON hd.MaHoaDon = tt.MaHoaDon
            WHERE 1 = 1
            """);

        themDieuKienNgay(sql, tuNgay, denNgay);
        sql.append(" GROUP BY hd.NgayLap ORDER BY hd.NgayLap DESC");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            ganThamSoNgay(ps, tuNgay, denNgay);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeTheoNgay dong = new ThongKeTheoNgay();
                    java.sql.Date ngayLap = rs.getDate("NgayLap");
                    dong.setNgayLap(
                            ngayLap == null ? "" : ngayLap.toString());
                    dong.setSoHoaDon(rs.getInt("SoHoaDon"));
                    dong.setDoanhThu(rs.getDouble("DoanhThu"));
                    dong.setThucThu(rs.getDouble("ThucThu"));
                    dong.setConNo(rs.getDouble("ConNo"));
                    danhSach.add(dong);
                }
            }

            return danhSach;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Loi khi thong ke theo ngay: "
                            + e.getMessage(), e);
        }
    }

    private void themDieuKienNgay(
            StringBuilder sql, String tuNgay, String denNgay) {
        if (coGiaTri(tuNgay)) {
            sql.append(" AND hd.NgayLap >= ?");
        }

        if (coGiaTri(denNgay)) {
            sql.append(" AND hd.NgayLap <= ?");
        }
    }

    private void ganThamSoNgay(
            PreparedStatement ps, String tuNgay, String denNgay)
            throws Exception {
        int viTri = 1;

        if (coGiaTri(tuNgay)) {
            ps.setDate(viTri++, java.sql.Date.valueOf(tuNgay.trim()));
        }

        if (coGiaTri(denNgay)) {
            ps.setDate(viTri, java.sql.Date.valueOf(denNgay.trim()));
        }
    }

    private boolean coGiaTri(String giaTri) {
        return giaTri != null && !giaTri.trim().isEmpty();
    }
}

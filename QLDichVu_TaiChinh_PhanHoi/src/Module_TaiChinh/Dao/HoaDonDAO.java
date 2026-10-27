package Module_TaiChinh.Dao;

import Module_TaiChinh.model.HoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // =========================
    // 1. LẤY TẤT CẢ HÓA ĐƠN
    // =========================
    public List<HoaDon> getAll() {

        List<HoaDon> list = new ArrayList<>();

        String sql = "SELECT * FROM HoaDon ORDER BY MaHoaDon";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                HoaDon hd = new HoaDon();

                hd.setMaHoaDon(rs.getString("MaHoaDon"));
                hd.setMaDatPhong(rs.getString("MaDatPhong"));
                hd.setNgayLap(rs.getDate("NgayLap").toString());
                hd.setTienGiam(rs.getDouble("TienGiam"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getString("TrangThai"));

                list.add(hd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // =========================
    // 2. TÌM HÓA ĐƠN THEO MÃ
    // =========================
    
    // TÌM KIẾM HÓA ĐƠN THEO NHIỀU ĐIỀU KIỆN
    public List<HoaDon> search(
            String maHoaDon,
            String maDatPhong,
            String maKhachHang,
            String tuNgay,
            String denNgay) {

        List<HoaDon> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT hd.* "
                + "FROM HoaDon hd "
                + "JOIN DatPhong dp "
                + "ON hd.MaDatPhong = dp.MaDatPhong "
                + "WHERE 1 = 1 "
        );

        List<Object> params = new ArrayList<>();

        // Lọc theo mã hóa đơn
        if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
            sql.append(" AND hd.MaHoaDon LIKE ?");
            params.add("%" + maHoaDon.trim() + "%");
        }

        // Lọc theo mã đặt phòng
        if (maDatPhong != null && !maDatPhong.trim().isEmpty()) {
            sql.append(" AND hd.MaDatPhong LIKE ?");
            params.add("%" + maDatPhong.trim() + "%");
        }

        // Lọc theo mã khách hàng
        if (maKhachHang != null && !maKhachHang.trim().isEmpty()) {
            sql.append(" AND dp.MaKhachHang LIKE ?");
            params.add("%" + maKhachHang.trim() + "%");
        }

        // Lọc từ ngày
        if (tuNgay != null && !tuNgay.trim().isEmpty()) {
            sql.append(" AND hd.NgayLap >= ?");
            params.add(java.sql.Date.valueOf(tuNgay.trim()));
        }

        // Lọc đến ngày
        if (denNgay != null && !denNgay.trim().isEmpty()) {
            sql.append(" AND hd.NgayLap <= ?");
            params.add(java.sql.Date.valueOf(denNgay.trim()));
        }

        sql.append(" ORDER BY hd.NgayLap DESC");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {

            // Gán các tham số theo đúng thứ tự
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    HoaDon hd = new HoaDon();

                    hd.setMaHoaDon(rs.getString("MaHoaDon"));
                    hd.setMaDatPhong(rs.getString("MaDatPhong"));
                    hd.setNgayLap(rs.getDate("NgayLap").toString());
                    hd.setTienGiam(rs.getDouble("TienGiam"));
                    hd.setTongTien(rs.getDouble("TongTien"));
                    hd.setTrangThai(rs.getString("TrangThai"));

                    list.add(hd);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }



    // =========================
    // 3. THÊM HÓA ĐƠN
    // =========================
    public boolean insert(HoaDon hd) {

        String sql = """
                INSERT INTO HoaDon
                (MaHoaDon, MaDatPhong, NgayLap, TienGiam, TongTien, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, hd.getMaHoaDon());
            ps.setString(2, hd.getMaDatPhong());
            ps.setString(3, hd.getNgayLap());
            ps.setDouble(4, hd.getTienGiam());
            ps.setDouble(5, hd.getTongTien());
            ps.setString(6, hd.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // =========================
    // 4. SỬA HÓA ĐƠN
    // =========================
    public boolean update(HoaDon hd) {

        String sql = """
                UPDATE HoaDon
                SET MaDatPhong = ?,
                    NgayLap = ?,
                    TienGiam = ?,
                    TongTien = ?,
                    TrangThai = ?
                WHERE MaHoaDon = ?
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, hd.getMaDatPhong());
            ps.setString(2, hd.getNgayLap());
            ps.setDouble(3, hd.getTienGiam());
            ps.setDouble(4, hd.getTongTien());
            ps.setString(5, hd.getTrangThai());
            ps.setString(6, hd.getMaHoaDon());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // =========================
    // 5. XÓA HÓA ĐƠN
    // =========================
    public boolean delete(String maHoaDon) {

        Connection con = null;

        try {

            con = DBConnection.getConnection();

            // Tắt tự động commit
            con.setAutoCommit(false);

            // Xóa thanh toán trước
            String sqlThanhToan =
                    "DELETE FROM ThanhToan WHERE MaHoaDon = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlThanhToan)) {

                ps.setString(1, maHoaDon);
                ps.executeUpdate();
            }


            // Xóa chi tiết hóa đơn
            String sqlChiTiet =
                    "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlChiTiet)) {

                ps.setString(1, maHoaDon);
                ps.executeUpdate();
            }


            // Cuối cùng mới xóa hóa đơn
            String sqlHoaDon =
                    "DELETE FROM HoaDon WHERE MaHoaDon = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlHoaDon)) {

                ps.setString(1, maHoaDon);

                int result = ps.executeUpdate();

                if (result > 0) {
                    con.commit();
                    return true;
                } else {
                    con.rollback();
                    return false;
                }
            }

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
            return false;

        } finally {

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // =========================
    // 6. TÌM KIẾM HÓA ĐƠN
    // =========================
    public List<HoaDon> search(String keyword) {

        List<HoaDon> list = new ArrayList<>();

        String sql = """
                SELECT * FROM HoaDon
                WHERE MaHoaDon LIKE ?
                   OR MaDatPhong LIKE ?
                   OR TrangThai LIKE ?
                ORDER BY MaHoaDon
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            String value = "%" + keyword + "%";

            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                HoaDon hd = new HoaDon();

                hd.setMaHoaDon(rs.getString("MaHoaDon"));
                hd.setMaDatPhong(rs.getString("MaDatPhong"));
                hd.setNgayLap(rs.getDate("NgayLap").toString());
                hd.setTienGiam(rs.getDouble("TienGiam"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getString("TrangThai"));

                list.add(hd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    
public HoaDon findById(String maHoaDon) {

    String sql = """
            SELECT *
            FROM HoaDon
            WHERE MaHoaDon = ?
            """;

    try (
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, maHoaDon.trim());

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                HoaDon hd = new HoaDon();

                hd.setMaHoaDon(rs.getString("MaHoaDon"));
                hd.setMaDatPhong(rs.getString("MaDatPhong"));

                java.sql.Date ngayLap = rs.getDate("NgayLap");
                hd.setNgayLap(
                    ngayLap != null ? ngayLap.toString() : null
                );

                hd.setTienGiam(rs.getDouble("TienGiam"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getString("TrangThai"));

                return hd;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(
            "Loi khi tim hoa don: " + e.getMessage(), e
        );
    }

    // Khong tim thay hoa don
    return null;
}

}
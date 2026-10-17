
package Module_TaiChinh.Dao;

import Module_TaiChinh.model.ThanhToan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThanhToanDAO {

    // TIM MOT GIAO DICH THEO MA
    public ThanhToan findById(String maThanhToan) {
        String sql = "SELECT * FROM ThanhToan WHERE MaThanhToan = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maThanhToan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ThanhToan tt = new ThanhToan();
                    tt.setMaThanhToan(rs.getString("MaThanhToan"));
                    tt.setMaHoaDon(rs.getString("MaHoaDon"));

                    java.sql.Date ngayThanhToan
                            = rs.getDate("NgayThanhToan");
                    tt.setNgayThanhToan(
                            ngayThanhToan == null
                                    ? null : ngayThanhToan.toString());

                    tt.setPhuongThuc(rs.getString("PhuongThuc"));
                    tt.setSoTien(rs.getDouble("SoTien"));
                    tt.setTrangThai(rs.getString("TrangThai"));
                    return tt;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Loi khi tim giao dich thanh toan: "
                            + e.getMessage(), e);
        }

        return null;
    }

    // 1. LẤY TẤT CẢ THANH TOÁN
    public List<ThanhToan> getAll() {

        List<ThanhToan> list = new ArrayList<>();

        String sql = "SELECT * FROM ThanhToan ORDER BY NgayThanhToan DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                ThanhToan tt = new ThanhToan();

                tt.setMaThanhToan(rs.getString("MaThanhToan"));
                tt.setMaHoaDon(rs.getString("MaHoaDon"));
                tt.setNgayThanhToan(
                        rs.getDate("NgayThanhToan").toString()
                );
                tt.setPhuongThuc(rs.getString("PhuongThuc"));
                tt.setSoTien(rs.getDouble("SoTien"));
                tt.setTrangThai(rs.getString("TrangThai"));

                list.add(tt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // 2. LẤY CÁC LẦN THANH TOÁN CỦA MỘT HÓA ĐƠN
    public List<ThanhToan> getByMaHoaDon(String maHoaDon) {

        List<ThanhToan> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM ThanhToan
                WHERE MaHoaDon = ?
                ORDER BY NgayThanhToan DESC
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, maHoaDon);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    ThanhToan tt = new ThanhToan();

                    tt.setMaThanhToan(rs.getString("MaThanhToan"));
                    tt.setMaHoaDon(rs.getString("MaHoaDon"));
                    tt.setNgayThanhToan(
                            rs.getDate("NgayThanhToan").toString()
                    );
                    tt.setPhuongThuc(rs.getString("PhuongThuc"));
                    tt.setSoTien(rs.getDouble("SoTien"));
                    tt.setTrangThai(rs.getString("TrangThai"));

                    list.add(tt);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // 3. THÊM THANH TOÁN
    public boolean insert(ThanhToan tt) {

        String sql = """
                INSERT INTO ThanhToan
                (
                    MaThanhToan, MaHoaDon, NgayThanhToan,
                    PhuongThuc, SoTien, TrangThai
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, tt.getMaThanhToan());
            ps.setString(2, tt.getMaHoaDon());
            ps.setString(3, tt.getNgayThanhToan());
            ps.setString(4, tt.getPhuongThuc());
            ps.setDouble(5, tt.getSoTien());
            ps.setString(6, tt.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 4. SỬA THANH TOÁN
    public boolean update(ThanhToan tt) {

        String sql = """
                UPDATE ThanhToan
                SET MaHoaDon = ?,
                    NgayThanhToan = ?,
                    PhuongThuc = ?,
                    SoTien = ?,
                    TrangThai = ?
                WHERE MaThanhToan = ?
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, tt.getMaHoaDon());
            ps.setString(2, tt.getNgayThanhToan());
            ps.setString(3, tt.getPhuongThuc());
            ps.setDouble(4, tt.getSoTien());
            ps.setString(5, tt.getTrangThai());
            ps.setString(6, tt.getMaThanhToan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 5. XÓA THANH TOÁN
    public boolean delete(String maThanhToan) {

        String sql =
                "DELETE FROM ThanhToan WHERE MaThanhToan = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, maThanhToan);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 6. TÌM KIẾM THANH TOÁN
    public List<ThanhToan> search(
            String maThanhToan,
            String maHoaDon,
            String phuongThuc,
            String trangThai) {

        List<ThanhToan> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM ThanhToan WHERE 1 = 1"
        );

        List<String> params = new ArrayList<>();

        if (maThanhToan != null && !maThanhToan.trim().isEmpty()) {
            sql.append(" AND MaThanhToan LIKE ?");
            params.add("%" + maThanhToan.trim() + "%");
        }

        if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
            sql.append(" AND MaHoaDon LIKE ?");
            params.add("%" + maHoaDon.trim() + "%");
        }

        if (phuongThuc != null && !phuongThuc.trim().isEmpty()) {
            sql.append(" AND PhuongThuc LIKE ?");
            params.add("%" + phuongThuc.trim() + "%");
        }

        if (trangThai != null && !trangThai.trim().isEmpty()) {
            sql.append(" AND TrangThai LIKE ?");
            params.add("%" + trangThai.trim() + "%");
        }

        sql.append(" ORDER BY NgayThanhToan DESC");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    ThanhToan tt = new ThanhToan();

                    tt.setMaThanhToan(rs.getString("MaThanhToan"));
                    tt.setMaHoaDon(rs.getString("MaHoaDon"));
                    tt.setNgayThanhToan(
                            rs.getDate("NgayThanhToan").toString()
                    );
                    tt.setPhuongThuc(rs.getString("PhuongThuc"));
                    tt.setSoTien(rs.getDouble("SoTien"));
                    tt.setTrangThai(rs.getString("TrangThai"));

                    list.add(tt);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}

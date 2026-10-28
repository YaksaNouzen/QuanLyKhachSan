package Module_PhanHoiKhachHang.dao;

import Module_PhanHoiKhachHang.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LichSuXuLyPhanHoiDAO {
    public void insert(Connection con, long id, TrangThaiPhanHoi before,
            TrangThaiPhanHoi after, String note, String actor) throws SQLException {
        String sql = "INSERT INTO dbo.LichSuXuLyPhanHoi"
                + " (MaPhanHoi,TrangThaiTruoc,TrangThaiSau,NoiDungXuLy,NguoiThucHien) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setQueryTimeout(15);
            ps.setLong(1, id);
            if (before == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, before.name());
            ps.setString(3, after.name());
            ps.setNString(4, note);
            if (actor == null) ps.setNull(5, Types.NVARCHAR); else ps.setNString(5, actor);
            if (ps.executeUpdate() != 1) throw new SQLException("Không lưu được lịch sử xử lý.");
        }
    }

    public List<LichSuXuLyPhanHoi> getByMaPhanHoi(Connection con, long id) throws SQLException {
        String sql = "SELECT MaLichSu,MaPhanHoi,ThoiGianGhiNhan,TrangThaiTruoc,TrangThaiSau,"
                + "NoiDungXuLy,NguoiThucHien FROM dbo.LichSuXuLyPhanHoi"
                + " WHERE MaPhanHoi=? ORDER BY ThoiGianGhiNhan,MaLichSu";
        List<LichSuXuLyPhanHoi> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setQueryTimeout(15);
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichSuXuLyPhanHoi h = new LichSuXuLyPhanHoi();
                    h.setMaLichSu(rs.getLong("MaLichSu")); h.setMaPhanHoi(rs.getLong("MaPhanHoi"));
                    h.setThoiGianGhiNhan(rs.getTimestamp("ThoiGianGhiNhan").toLocalDateTime());
                    String before = rs.getString("TrangThaiTruoc");
                    h.setTrangThaiTruoc(before == null ? null : TrangThaiPhanHoi.valueOf(before));
                    h.setTrangThaiSau(TrangThaiPhanHoi.valueOf(rs.getString("TrangThaiSau")));
                    h.setNoiDungXuLy(rs.getNString("NoiDungXuLy"));
                    h.setNguoiThucHien(rs.getNString("NguoiThucHien")); list.add(h);
                }
            }
        }
        return list;
    }
}

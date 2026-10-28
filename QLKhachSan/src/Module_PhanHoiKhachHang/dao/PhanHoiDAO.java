package Module_PhanHoiKhachHang.dao;

import Module_PhanHoiKhachHang.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhanHoiDAO {
    private static final String COLUMNS = "MaPhanHoi, MaKhachHang, LoaiPhanHoi, NoiDung, "
            + "ThoiGianTiepNhan, TrangThai, ThoiGianCapNhat, PhienBan";

    public List<PhanHoi> search(Connection con, String keyword,
            LoaiPhanHoi loai, TrangThaiPhanHoi trangThai) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM dbo.PhanHoi WHERE 1=1");
        if (!keyword.isEmpty()) sql.append(" AND (CONVERT(VARCHAR(20),MaPhanHoi) = ?"
                + " OR MaKhachHang LIKE ? ESCAPE '~' OR NoiDung LIKE ? ESCAPE '~')");
        if (loai != null) sql.append(" AND LoaiPhanHoi = ?");
        if (trangThai != null) sql.append(" AND TrangThai = ?");
        sql.append(" ORDER BY ThoiGianTiepNhan DESC, MaPhanHoi DESC");
        List<PhanHoi> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setQueryTimeout(15);
            int i = 1;
            if (!keyword.isEmpty()) {
                String value = "%" + keyword.replace("~", "~~").replace("%", "~%")
                        .replace("_", "~_").replace("[", "~[") + "%";
                ps.setString(i++, keyword);
                ps.setString(i++, value);
                ps.setNString(i++, value);
            }
            if (loai != null) ps.setString(i++, loai.name());
            if (trangThai != null) ps.setString(i, trangThai.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public PhanHoi findById(Connection con, long id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT " + COLUMNS
                + " FROM dbo.PhanHoi WHERE MaPhanHoi = ?")) {
            ps.setQueryTimeout(15);
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public long insert(Connection con, PhanHoi p) throws SQLException {
        String sql = "INSERT INTO dbo.PhanHoi (MaKhachHang,LoaiPhanHoi,NoiDung,ThoiGianTiepNhan)"
                + " VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setQueryTimeout(15);
            ps.setString(1, p.getMaKhachHang());
            ps.setString(2, p.getLoaiPhanHoi().name());
            ps.setNString(3, p.getNoiDung());
            ps.setTimestamp(4, Timestamp.valueOf(p.getThoiGianTiepNhan()));
            if (ps.executeUpdate() != 1) throw new SQLException("Không tạo được phản hồi.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("Không nhận được mã phản hồi.");
                return rs.getLong(1);
            }
        }
    }

    public boolean updateStatus(Connection con, long id, byte[] version,
            TrangThaiPhanHoi state) throws SQLException {
        String sql = "UPDATE dbo.PhanHoi SET TrangThai=?, ThoiGianCapNhat=SYSDATETIME()"
                + " WHERE MaPhanHoi=? AND PhienBan=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setQueryTimeout(15);
            ps.setString(1, state.name());
            ps.setLong(2, id);
            ps.setBytes(3, version);
            return ps.executeUpdate() == 1;
        }
    }

    private PhanHoi map(ResultSet rs) throws SQLException {
        PhanHoi p = new PhanHoi();
        p.setMaPhanHoi(rs.getLong("MaPhanHoi"));
        p.setMaKhachHang(rs.getString("MaKhachHang"));
        p.setLoaiPhanHoi(LoaiPhanHoi.valueOf(rs.getString("LoaiPhanHoi")));
        p.setNoiDung(rs.getNString("NoiDung"));
        p.setThoiGianTiepNhan(rs.getTimestamp("ThoiGianTiepNhan").toLocalDateTime());
        p.setTrangThai(TrangThaiPhanHoi.valueOf(rs.getString("TrangThai")));
        p.setThoiGianCapNhat(rs.getTimestamp("ThoiGianCapNhat").toLocalDateTime());
        p.setPhienBan(rs.getBytes("PhienBan"));
        return p;
    }
}

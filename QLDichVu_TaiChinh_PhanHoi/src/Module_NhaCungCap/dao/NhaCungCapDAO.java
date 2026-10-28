package Module_NhaCungCap.dao;

import Module_NhaCungCap.model.NhaCungCap;
import common.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    public List<NhaCungCap> timKiem(String tuKhoa, String trangThai)
            throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai,
                       Email, DiaChi, TrangThai, GhiChu
                FROM NhaCungCap
                WHERE (MaNhaCungCap LIKE ? OR TenNhaCungCap LIKE ?
                       OR SoDienThoai LIKE ?)
                """);
        boolean locTrangThai = trangThai != null && !trangThai.isBlank()
                && !"Tất cả".equals(trangThai);
        if (locTrangThai) {
            sql.append(" AND TrangThai = ?");
        }
        sql.append(" ORDER BY TenNhaCungCap");

        List<NhaCungCap> ketQua = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            String mau = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";
            ps.setString(1, mau);
            ps.setString(2, mau);
            ps.setString(3, mau);
            if (locTrangThai) {
                ps.setString(4, trangThai);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(docNhaCungCap(rs));
                }
            }
        }
        return ketQua;
    }

    public List<NhaCungCap> layDangHoatDong() throws SQLException {
        return timKiem("", "Đang hoạt động");
    }

    public NhaCungCap timTheoMa(String ma) throws SQLException {
        String sql = """
                SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai,
                       Email, DiaChi, TrangThai, GhiChu
                FROM NhaCungCap WHERE MaNhaCungCap = ?
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docNhaCungCap(rs) : null;
            }
        }
    }

    public void them(NhaCungCap ncc) throws SQLException {
        String sql = """
                INSERT INTO NhaCungCap
                    (MaNhaCungCap, TenNhaCungCap, SoDienThoai,
                     Email, DiaChi, TrangThai, GhiChu)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, ncc);
            ps.executeUpdate();
        }
    }

    public boolean capNhat(NhaCungCap ncc) throws SQLException {
        String sql = """
                UPDATE NhaCungCap
                SET TenNhaCungCap = ?, SoDienThoai = ?, Email = ?,
                    DiaChi = ?, TrangThai = ?, GhiChu = ?
                WHERE MaNhaCungCap = ?
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNhaCungCap());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getEmail());
            ps.setString(4, ncc.getDiaChi());
            ps.setString(5, ncc.getTrangThai());
            ps.setString(6, ncc.getGhiChu());
            ps.setString(7, ncc.getMaNhaCungCap());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String ma, String trangThai)
            throws SQLException {
        String sql = "UPDATE NhaCungCap SET TrangThai = ? WHERE MaNhaCungCap = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, ma);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Object[]> layLichSuNhap(String maNhaCungCap)
            throws SQLException {
        String sql = """
                SELECT MaPhieuNhap, NgayNhap, TongTien, TrangThai
                FROM PhieuNhap WHERE MaNhaCungCap = ?
                ORDER BY NgayNhap DESC
                """;
        List<Object[]> ds = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhaCungCap);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{rs.getString(1), rs.getTimestamp(2),
                        rs.getBigDecimal(3), rs.getString(4)});
                }
            }
        }
        return ds;
    }

    public List<Object[]> layVatTuDaCungCap(String maNhaCungCap)
            throws SQLException {
        String sql = """
                SELECT vt.MaVatTu, vt.TenVatTu, SUM(ct.SoLuong) TongSoLuong
                FROM PhieuNhap pn
                JOIN ChiTietPhieuNhap ct ON ct.MaPhieuNhap = pn.MaPhieuNhap
                JOIN VatTu vt ON vt.MaVatTu = ct.MaVatTu
                WHERE pn.MaNhaCungCap = ? AND pn.TrangThai = N'Đã xác nhận'
                GROUP BY vt.MaVatTu, vt.TenVatTu
                ORDER BY vt.TenVatTu
                """;
        List<Object[]> ds = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhaCungCap);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{rs.getString(1), rs.getString(2),
                        rs.getInt(3)});
                }
            }
        }
        return ds;
    }

    private void ganThamSo(PreparedStatement ps, NhaCungCap ncc)
            throws SQLException {
        ps.setString(1, ncc.getMaNhaCungCap());
        ps.setString(2, ncc.getTenNhaCungCap());
        ps.setString(3, ncc.getSoDienThoai());
        ps.setString(4, ncc.getEmail());
        ps.setString(5, ncc.getDiaChi());
        ps.setString(6, ncc.getTrangThai());
        ps.setString(7, ncc.getGhiChu());
    }

    private NhaCungCap docNhaCungCap(ResultSet rs) throws SQLException {
        return new NhaCungCap(rs.getString("MaNhaCungCap"),
                rs.getString("TenNhaCungCap"), rs.getString("SoDienThoai"),
                rs.getString("Email"), rs.getString("DiaChi"),
                rs.getString("TrangThai"), rs.getString("GhiChu"));
    }
}

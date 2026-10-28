package Module_QuanLyVatTu.Dao;

import Module_QuanLyVatTu.model.VatTu;
import common.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VatTuDAO {

    public List<VatTu> timKiem(String tuKhoa, String trangThai,
                               boolean chiSapHet) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT MaVatTu, TenVatTu, DonViTinh, SoLuongTon,
                       MucTonToiThieu, TrangThai, MoTa
                FROM VatTu
                WHERE (MaVatTu LIKE ? OR TenVatTu LIKE ?)
                """);
        if (trangThai != null && !trangThai.isBlank()
                && !"Tất cả".equals(trangThai)) {
            sql.append(" AND TrangThai = ?");
        }
        if (chiSapHet) {
            sql.append(" AND SoLuongTon <= MucTonToiThieu");
        }
        sql.append(" ORDER BY TenVatTu");

        List<VatTu> ketQua = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            String mau = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";
            ps.setString(1, mau);
            ps.setString(2, mau);
            if (sql.indexOf("TrangThai = ?") >= 0) {
                ps.setString(3, trangThai);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(docVatTu(rs));
                }
            }
        }
        return ketQua;
    }

    public List<VatTu> layVatTuDangSuDung() throws SQLException {
        return timKiem("", "Đang sử dụng", false);
    }

    public VatTu timTheoMa(String ma) throws SQLException {
        String sql = """
                SELECT MaVatTu, TenVatTu, DonViTinh, SoLuongTon,
                       MucTonToiThieu, TrangThai, MoTa
                FROM VatTu WHERE MaVatTu = ?
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docVatTu(rs) : null;
            }
        }
    }

    public void them(VatTu vatTu) throws SQLException {
        String sql = """
                INSERT INTO VatTu
                    (MaVatTu, TenVatTu, DonViTinh, SoLuongTon,
                     MucTonToiThieu, TrangThai, MoTa)
                VALUES (?, ?, ?, 0, ?, ?, ?)
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vatTu.getMaVatTu());
            ps.setString(2, vatTu.getTenVatTu());
            ps.setString(3, vatTu.getDonViTinh());
            ps.setInt(4, vatTu.getMucTonToiThieu());
            ps.setString(5, vatTu.getTrangThai());
            ps.setString(6, vatTu.getMoTa());
            ps.executeUpdate();
        }
    }

    public boolean capNhat(VatTu vatTu) throws SQLException {
        String sql = """
                UPDATE VatTu SET TenVatTu = ?, DonViTinh = ?,
                       MucTonToiThieu = ?, TrangThai = ?, MoTa = ?
                WHERE MaVatTu = ?
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vatTu.getTenVatTu());
            ps.setString(2, vatTu.getDonViTinh());
            ps.setInt(3, vatTu.getMucTonToiThieu());
            ps.setString(4, vatTu.getTrangThai());
            ps.setString(5, vatTu.getMoTa());
            ps.setString(6, vatTu.getMaVatTu());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThai(String maVatTu, String trangThai)
            throws SQLException {
        String sql = "UPDATE VatTu SET TrangThai = ? WHERE MaVatTu = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maVatTu);
            return ps.executeUpdate() > 0;
        }
    }

    /** Trừ tồn bằng một lệnh có điều kiện để không bao giờ âm kho. */
    public boolean xuatKho(String maVatTu, int soLuong) throws SQLException {
        String sql = """
                UPDATE VatTu
                SET SoLuongTon = SoLuongTon - ?
                WHERE MaVatTu = ? AND TrangThai = N'Đang sử dụng'
                      AND SoLuongTon >= ?
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maVatTu);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        }
    }

    private VatTu docVatTu(ResultSet rs) throws SQLException {
        return new VatTu(
                rs.getString("MaVatTu"),
                rs.getString("TenVatTu"),
                rs.getString("DonViTinh"),
                rs.getInt("SoLuongTon"),
                rs.getInt("MucTonToiThieu"),
                rs.getString("TrangThai"),
                rs.getString("MoTa"));
    }
}

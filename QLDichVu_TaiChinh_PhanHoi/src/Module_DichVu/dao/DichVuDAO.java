
package Module_DichVu.dao;

import Module_DichVu.DBConnection;
import Module_DichVu.model.DichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    // Chuyen mot dong ResultSet thanh doi tuong DichVu
    private DichVu mapResultSet(ResultSet rs)
            throws SQLException {

        DichVu dv = new DichVu();

        dv.setMaDV(rs.getInt("MaDV"));
        dv.setMaLoaiDV(rs.getInt("MaLoaiDV"));
        dv.setTenDV(rs.getString("TenDV"));
        dv.setDonGia(rs.getBigDecimal("DonGia"));
        dv.setDonViTinh(rs.getString("DonViTinh"));
        dv.setMoTa(rs.getString("MoTa"));
        dv.setTrangThai(rs.getBoolean("TrangThai"));

        return dv;
    }

    // 1. Lay tat ca dich vu
    public List<DichVu> getAll() throws SQLException {

        List<DichVu> danhSach = new ArrayList<>();

        String sql = "SELECT MaDV, MaLoaiDV, TenDV, DonGia, "
                   + "DonViTinh, MoTa, TrangThai "
                   + "FROM DichVu ORDER BY MaDV DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSet(rs));
            }
        }

        return danhSach;
    }

    // 2. Tim dich vu theo ma
    public DichVu getById(int maDV) throws SQLException {

        String sql = "SELECT MaDV, MaLoaiDV, TenDV, DonGia, "
                   + "DonViTinh, MoTa, TrangThai "
                   + "FROM DichVu WHERE MaDV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    // 3. Tim kiem dich vu theo ten
    public List<DichVu> searchByName(String tuKhoa)
            throws SQLException {

        List<DichVu> danhSach = new ArrayList<>();

        String sql = "SELECT MaDV, MaLoaiDV, TenDV, DonGia, "
                   + "DonViTinh, MoTa, TrangThai "
                   + "FROM DichVu "
                   + "WHERE TenDV LIKE ? "
                   + "ORDER BY TenDV";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tuKhoa + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSet(rs));
                }
            }
        }

        return danhSach;
    }

    // 4. Them dich vu
    // MaDV tu tang nen khong can truyen vao INSERT
    public boolean insert(DichVu dv) throws SQLException {

        String sql = "INSERT INTO DichVu "
                   + "(MaLoaiDV, TenDV, DonGia, DonViTinh, "
                   + "MoTa, TrangThai) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dv.getMaLoaiDV());
            ps.setString(2, dv.getTenDV());
            ps.setBigDecimal(3, dv.getDonGia());
            ps.setString(4, dv.getDonViTinh());
            ps.setString(5, dv.getMoTa());
            ps.setBoolean(6, dv.isTrangThai());

            return ps.executeUpdate() > 0;
        }
    }

    // 5. Cap nhat dich vu
    public boolean update(DichVu dv) throws SQLException {

        String sql = "UPDATE DichVu SET "
                   + "MaLoaiDV = ?, TenDV = ?, DonGia = ?, "
                   + "DonViTinh = ?, MoTa = ?, TrangThai = ? "
                   + "WHERE MaDV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dv.getMaLoaiDV());
            ps.setString(2, dv.getTenDV());
            ps.setBigDecimal(3, dv.getDonGia());
            ps.setString(4, dv.getDonViTinh());
            ps.setString(5, dv.getMoTa());
            ps.setBoolean(6, dv.isTrangThai());
            ps.setInt(7, dv.getMaDV());

            return ps.executeUpdate() > 0;
        }
    }

    // 6. Xoa mem dich vu: khong xoa vat ly khoi database
    public boolean delete(int maDV) throws SQLException {

        String sql = "UPDATE DichVu SET TrangThai = 0 "
                   + "WHERE MaDV = ? AND TrangThai = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDV);

            return ps.executeUpdate() > 0;
        }
    }
    
    // Khoi phuc dich vu da ngung hoat dong
    public boolean restore(int maDV) throws SQLException {

        String sql = "UPDATE DichVu SET TrangThai = 1 "
                   + "WHERE MaDV = ? AND TrangThai = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDV);

            return ps.executeUpdate() > 0;
        }
    }

}

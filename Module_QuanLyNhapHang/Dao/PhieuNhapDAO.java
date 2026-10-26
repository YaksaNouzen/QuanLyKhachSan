package Module_QuanLyNhapHang.Dao;

import Module_QuanLyNhapHang.model.ChiTietPhieuNhap;
import Module_QuanLyNhapHang.model.PhieuNhap;
import kho.cungung.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDAO {

    public List<PhieuNhap> timKiem(String tuKhoa, String trangThai,
                                  LocalDate tuNgay, LocalDate denNgay)
            throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT pn.MaPhieuNhap, pn.MaNhaCungCap, ncc.TenNhaCungCap,
                       pn.NgayNhap, pn.TongTien, pn.TrangThai,
                       pn.NguoiLap, pn.GhiChu
                FROM PhieuNhap pn
                JOIN NhaCungCap ncc ON ncc.MaNhaCungCap = pn.MaNhaCungCap
                WHERE (pn.MaPhieuNhap LIKE ? OR pn.MaNhaCungCap LIKE ?
                       OR ncc.TenNhaCungCap LIKE ?)
                """);
        List<Object> thamSo = new ArrayList<>();
        String mau = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";
        thamSo.add(mau);
        thamSo.add(mau);
        thamSo.add(mau);
        if (trangThai != null && !trangThai.isBlank()
                && !"Tất cả".equals(trangThai)) {
            sql.append(" AND pn.TrangThai = ?");
            thamSo.add(trangThai);
        }
        if (tuNgay != null) {
            sql.append(" AND pn.NgayNhap >= ?");
            thamSo.add(Timestamp.valueOf(tuNgay.atStartOfDay()));
        }
        if (denNgay != null) {
            sql.append(" AND pn.NgayNhap < ?");
            thamSo.add(Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
        }
        sql.append(" ORDER BY pn.NgayNhap DESC, pn.MaPhieuNhap DESC");

        List<PhieuNhap> ketQua = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < thamSo.size(); i++) {
                ps.setObject(i + 1, thamSo.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(docPhieuNhap(rs));
                }
            }
        }
        return ketQua;
    }

    public PhieuNhap timTheoMa(String ma) throws SQLException {
        String sql = """
                SELECT pn.MaPhieuNhap, pn.MaNhaCungCap, ncc.TenNhaCungCap,
                       pn.NgayNhap, pn.TongTien, pn.TrangThai,
                       pn.NguoiLap, pn.GhiChu
                FROM PhieuNhap pn
                JOIN NhaCungCap ncc ON ncc.MaNhaCungCap = pn.MaNhaCungCap
                WHERE pn.MaPhieuNhap = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                PhieuNhap phieu = docPhieuNhap(rs);
                phieu.getChiTiet().addAll(layChiTiet(con, ma));
                return phieu;
            }
        }
    }

    /** Lưu cả phần đầu và chi tiết trong cùng transaction. */
    public void luuPhieuNhap(PhieuNhap phieu) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                kiemTraNhaCungCapDangHoatDong(con,
                        phieu.getMaNhaCungCap());
                String trangThaiCu = layTrangThaiCoKhoa(con,
                        phieu.getMaPhieuNhap());
                if (trangThaiCu == null) {
                    themPhanDau(con, phieu);
                } else {
                    if (!"Nháp".equals(trangThaiCu)) {
                        throw new SQLException(
                                "Chỉ được sửa phiếu nhập đang ở trạng thái Nháp.");
                    }
                    capNhatPhanDau(con, phieu);
                    xoaChiTiet(con, phieu.getMaPhieuNhap());
                }
                themChiTiet(con, phieu);
                con.commit();
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException("Không thể lưu phiếu nhập.", e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Xác nhận phiếu và cộng tồn kho nguyên tử. Khóa dòng phiếu ngăn hai
     * người xác nhận cùng lúc; lỗi ở bất kỳ bước nào sẽ rollback toàn bộ.
     */
    public void xacNhan(String maPhieuNhap) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String trangThai = layTrangThaiCoKhoa(con, maPhieuNhap);
                if (trangThai == null) {
                    throw new SQLException("Không tìm thấy phiếu nhập.");
                }
                if (!"Nháp".equals(trangThai)) {
                    throw new SQLException("Phiếu nhập đã được xử lý, không thể xác nhận lại.");
                }

                List<ChiTietPhieuNhap> chiTiet = layChiTiet(con, maPhieuNhap);
                if (chiTiet.isEmpty()) {
                    throw new SQLException("Phiếu nhập chưa có vật tư.");
                }

                String sqlCongTon = """
                        UPDATE VatTu SET SoLuongTon = SoLuongTon + ?
                        WHERE MaVatTu = ? AND TrangThai = N'Đang sử dụng'
                        """;
                try (PreparedStatement ps = con.prepareStatement(sqlCongTon)) {
                    for (ChiTietPhieuNhap dong : chiTiet) {
                        ps.setInt(1, dong.getSoLuong());
                        ps.setString(2, dong.getMaVatTu());
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("Vật tư " + dong.getMaVatTu()
                                    + " không tồn tại hoặc đã ngừng sử dụng.");
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement("""
                        UPDATE PhieuNhap SET TrangThai = N'Đã xác nhận'
                        WHERE MaPhieuNhap = ? AND TrangThai = N'Nháp'
                        """)) {
                    ps.setString(1, maPhieuNhap);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Không thể đổi trạng thái phiếu nhập.");
                    }
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException("Không thể xác nhận phiếu nhập.", e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean huyPhieu(String ma) throws SQLException {
        String sql = """
                UPDATE PhieuNhap SET TrangThai = N'Đã hủy'
                WHERE MaPhieuNhap = ? AND TrangThai = N'Nháp'
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() == 1;
        }
    }

    private String layTrangThaiCoKhoa(Connection con, String ma)
            throws SQLException {
        String sql = "SELECT TrangThai FROM PhieuNhap WITH (UPDLOCK, HOLDLOCK) WHERE MaPhieuNhap = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private void kiemTraNhaCungCapDangHoatDong(Connection con, String ma)
            throws SQLException {
        String sql = """
                SELECT TrangThai
                FROM NhaCungCap WITH (UPDLOCK, HOLDLOCK)
                WHERE MaNhaCungCap = ?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !"Đang hoạt động".equals(rs.getString(1))) {
                    throw new SQLException(
                            "Nhà cung cấp không tồn tại hoặc đã ngừng hoạt động.");
                }
            }
        }
    }

    private void themPhanDau(Connection con, PhieuNhap phieu)
            throws SQLException {
        String sql = """
                INSERT INTO PhieuNhap
                    (MaPhieuNhap, MaNhaCungCap, NgayNhap, TongTien,
                     TrangThai, NguoiLap, GhiChu)
                VALUES (?, ?, ?, ?, N'Nháp', ?, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ganPhanDau(ps, phieu, false);
            ps.executeUpdate();
        }
    }

    private void capNhatPhanDau(Connection con, PhieuNhap phieu)
            throws SQLException {
        String sql = """
                UPDATE PhieuNhap SET MaNhaCungCap = ?, NgayNhap = ?,
                       TongTien = ?, NguoiLap = ?, GhiChu = ?
                WHERE MaPhieuNhap = ? AND TrangThai = N'Nháp'
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ganPhanDau(ps, phieu, true);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Không thể cập nhật phiếu nhập.");
            }
        }
    }

    private void ganPhanDau(PreparedStatement ps, PhieuNhap phieu,
                            boolean capNhat) throws SQLException {
        if (!capNhat) {
            ps.setString(1, phieu.getMaPhieuNhap());
            ps.setString(2, phieu.getMaNhaCungCap());
            ps.setTimestamp(3, Timestamp.valueOf(phieu.getNgayNhap()));
            ps.setBigDecimal(4, phieu.getTongTien());
            ps.setString(5, phieu.getNguoiLap());
            ps.setString(6, phieu.getGhiChu());
        } else {
            ps.setString(1, phieu.getMaNhaCungCap());
            ps.setTimestamp(2, Timestamp.valueOf(phieu.getNgayNhap()));
            ps.setBigDecimal(3, phieu.getTongTien());
            ps.setString(4, phieu.getNguoiLap());
            ps.setString(5, phieu.getGhiChu());
            ps.setString(6, phieu.getMaPhieuNhap());
        }
    }

    private void themChiTiet(Connection con, PhieuNhap phieu)
            throws SQLException {
        String sql = """
                INSERT INTO ChiTietPhieuNhap
                    (MaPhieuNhap, MaVatTu, SoLuong, DonGia)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (ChiTietPhieuNhap dong : phieu.getChiTiet()) {
                ps.setString(1, phieu.getMaPhieuNhap());
                ps.setString(2, dong.getMaVatTu());
                ps.setInt(3, dong.getSoLuong());
                ps.setBigDecimal(4, dong.getDonGia());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void xoaChiTiet(Connection con, String ma) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
        }
    }

    private List<ChiTietPhieuNhap> layChiTiet(Connection con, String ma)
            throws SQLException {
        String sql = """
                SELECT ct.MaPhieuNhap, ct.MaVatTu, vt.TenVatTu,
                       ct.SoLuong, ct.DonGia
                FROM ChiTietPhieuNhap ct
                JOIN VatTu vt ON vt.MaVatTu = ct.MaVatTu
                WHERE ct.MaPhieuNhap = ? ORDER BY vt.TenVatTu
                """;
        List<ChiTietPhieuNhap> ds = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new ChiTietPhieuNhap(rs.getString("MaPhieuNhap"),
                            rs.getString("MaVatTu"), rs.getString("TenVatTu"),
                            rs.getInt("SoLuong"), rs.getBigDecimal("DonGia")));
                }
            }
        }
        return ds;
    }

    private PhieuNhap docPhieuNhap(ResultSet rs) throws SQLException {
        PhieuNhap phieu = new PhieuNhap();
        phieu.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        phieu.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
        phieu.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
        phieu.setNgayNhap(rs.getTimestamp("NgayNhap").toLocalDateTime());
        phieu.setTongTien(rs.getBigDecimal("TongTien"));
        phieu.setTrangThai(rs.getString("TrangThai"));
        phieu.setNguoiLap(rs.getString("NguoiLap"));
        phieu.setGhiChu(rs.getString("GhiChu"));
        return phieu;
    }
}

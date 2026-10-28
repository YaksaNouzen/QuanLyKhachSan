package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.NhanVien;
import util.DBConnection;

public class NhanVienDAO {

    // Lấy toàn bộ nhân viên
    public List<NhanVien> getAll() {
        return timKiem("", "Tất cả", "Tất cả");
    }

    /**
     * Tìm kiếm theo từ khóa (mã, tên, SĐT) + lọc bộ phận + lọc trạng thái.
     * Nếu chọn "Tất cả" thì không lọc theo điều kiện đó.
     */
    public List<NhanVien> timKiem(String tuKhoa, String boPhan, String trangThai) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien "
                + "WHERE (MaNV LIKE ? OR HoTen LIKE ? OR SDT LIKE ?) "
                + "AND (? = N'Tất cả' OR BoPhan = ?) "
                + "AND (? = N'Tất cả' OR TrangThai = ?) "
                + "ORDER BY MaNV";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            String tk = "%" + tuKhoa + "%";
            ps.setString(1, tk);
            ps.setString(2, tk);
            ps.setString(3, tk);
            ps.setString(4, boPhan);
            ps.setString(5, boPhan);
            ps.setString(6, trangThai);
            ps.setString(7, trangThai);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(docDong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy những nhân viên còn đang làm (dùng cho phân ca, tạo tài khoản)
    public List<NhanVien> getNhanVienDangLam() {
        return timKiem("", "Tất cả", "Đang làm");
    }

    public NhanVien getById(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return docDong(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(NhanVien nv) {
        String sql = "INSERT INTO NhanVien VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getGioiTinh());
            ps.setDate(4, nv.getNgaySinh());
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getEmail());
            ps.setString(7, nv.getDiaChi());
            ps.setString(8, nv.getBoPhan());
            ps.setString(9, nv.getChucVu());
            ps.setDate(10, nv.getNgayVaoLam());
            ps.setString(11, nv.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sua(NhanVien nv) {
        String sql = "UPDATE NhanVien SET HoTen = ?, GioiTinh = ?, NgaySinh = ?, SDT = ?, Email = ?, "
                + "DiaChi = ?, BoPhan = ?, ChucVu = ?, NgayVaoLam = ?, TrangThai = ? WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getGioiTinh());
            ps.setDate(3, nv.getNgaySinh());
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getEmail());
            ps.setString(6, nv.getDiaChi());
            ps.setString(7, nv.getBoPhan());
            ps.setString(8, nv.getChucVu());
            ps.setDate(9, nv.getNgayVaoLam());
            ps.setString(10, nv.getTrangThai());
            ps.setString(11, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa nhân viên. Nếu nhân viên đã có tài khoản hoặc đã được phân ca
     * thì SQL sẽ báo lỗi khóa ngoại -> trả về false.
     */
    public boolean xoa(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Chuyển 1 dòng ResultSet thành đối tượng NhanVien
    private NhanVien docDong(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("MaNV"),
                rs.getString("HoTen"),
                rs.getString("GioiTinh"),
                rs.getDate("NgaySinh"),
                rs.getString("SDT"),
                rs.getString("Email"),
                rs.getString("DiaChi"),
                rs.getString("BoPhan"),
                rs.getString("ChucVu"),
                rs.getDate("NgayVaoLam"),
                rs.getString("TrangThai"));
    }
}

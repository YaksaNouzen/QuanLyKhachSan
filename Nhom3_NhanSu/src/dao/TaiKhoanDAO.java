package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.TaiKhoan;
import util.DBConnection;
import util.MaHoa;

public class TaiKhoanDAO {

    private static final String SQL_SELECT = "SELECT tk.*, nv.HoTen, vt.TenVaiTro "
            + "FROM TaiKhoan tk "
            + "JOIN NhanVien nv ON tk.MaNV = nv.MaNV "
            + "JOIN VaiTro vt ON tk.MaVaiTro = vt.MaVaiTro ";

    /**
     * ĐĂNG NHẬP
     * Mã hóa mật khẩu người dùng gõ rồi so với mật khẩu đã mã hóa trong DB.
     * Đúng thì trả về TaiKhoan, sai thì trả về null.
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        String sql = SQL_SELECT + "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, MaHoa.sha256(matKhau));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return docDong(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = SQL_SELECT + "ORDER BY tk.MaVaiTro, tk.TenDangNhap";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(docDong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean kiemTraTenDangNhap(String tenDangNhap) {
        return demSoDong("SELECT COUNT(*) FROM TaiKhoan WHERE TenDangNhap = ?", tenDangNhap) > 0;
    }

    public boolean nhanVienDaCoTaiKhoan(String maNV) {
        return demSoDong("SELECT COUNT(*) FROM TaiKhoan WHERE MaNV = ?", maNV) > 0;
    }

    // tk.getMatKhau() ở đây là mật khẩu thường, hàm sẽ tự mã hóa
    public boolean them(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, MaHoa.sha256(tk.getMatKhau()));
            ps.setString(3, tk.getMaNV());
            ps.setString(4, tk.getMaVaiTro());
            ps.setBoolean(5, tk.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Admin sửa vai trò và trạng thái (khóa / mở khóa), không sửa mật khẩu ở đây
    public boolean sua(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET MaVaiTro = ?, TrangThai = ? WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getMaVaiTro());
            ps.setBoolean(2, tk.isTrangThai());
            ps.setString(3, tk.getTenDangNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String tenDangNhap) {
        String sql = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Dùng cho cả "Đổi mật khẩu" và "Đặt lại mật khẩu"
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, MaHoa.sha256(matKhauMoi));
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int demSoDong(String sql, String giaTri) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, giaTri);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private TaiKhoan docDong(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan(
                rs.getString("TenDangNhap"),
                rs.getString("MatKhau"),
                rs.getString("MaNV"),
                rs.getString("MaVaiTro"),
                rs.getBoolean("TrangThai"));
        tk.setTenNV(rs.getString("HoTen"));
        tk.setTenVaiTro(rs.getString("TenVaiTro"));
        return tk;
    }
}

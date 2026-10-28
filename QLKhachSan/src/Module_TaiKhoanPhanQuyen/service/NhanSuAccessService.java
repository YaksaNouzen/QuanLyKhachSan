package Module_TaiKhoanPhanQuyen.service;

import common.database.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import Module_TaiKhoanPhanQuyen.util.MaHoa;

/** Đăng nhập Nhóm 3 và tạo tài khoản quản trị đầu tiên trên schema rỗng. */
public final class NhanSuAccessService {

    public boolean daCoTaiKhoan() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM dbo.TaiKhoan")) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void taoQuanTriDauTien(String hoTen, String tenDangNhap, char[] matKhau)
            throws SQLException {
        String ten = hoTen == null ? "" : hoTen.trim();
        String user = tenDangNhap == null ? "" : tenDangNhap.trim();
        if (ten.isEmpty() || ten.length() > 100 || user.isEmpty()
                || user.length() > 50 || matKhau == null || matKhau.length < 6) {
            throw new IllegalArgumentException(
                    "Họ tên, tên đăng nhập và mật khẩu từ 6 ký tự phải hợp lệ.");
        }
        String hash = MaHoa.sha256(new String(matKhau));
        Arrays.fill(matKhau, '\0');

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            con.setAutoCommit(false);
            try {
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                             "SELECT COUNT(*) FROM dbo.TaiKhoan WITH (UPDLOCK, HOLDLOCK)")) {
                    rs.next();
                    if (rs.getInt(1) != 0) {
                        throw new SQLException("Đã có tài khoản; không thể tạo Admin đầu tiên nữa.");
                    }
                }
                String[][] roles = {
                    {"ADMIN", "Admin"}, {"LETAN", "Lễ tân"},
                    {"KETOAN", "Kế toán"}, {"NV", "Nhân viên"}
                };
                for (String[] role : roles) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = ?) "
                            + "INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro) VALUES (?, ?)")) {
                        ps.setString(1, role[0]);
                        ps.setString(2, role[0]);
                        ps.setString(3, role[1]);
                        ps.executeUpdate();
                    }
                }
                String maNV;
                try (CallableStatement cs = con.prepareCall("{CALL dbo.sp_LayMaTuDong(?)}")) {
                    cs.setString(1, "NHAN_VIEN");
                    try (ResultSet rs = cs.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Không cấp được mã nhân viên.");
                        maNV = rs.getString("MaMoi");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO dbo.NhanVien "
                        + "(MaNV, HoTen, BoPhan, ChucVu, NgayVaoLam, TrangThai) "
                        + "VALUES (?, ?, N'Quản lý', N'Trưởng bộ phận', "
                        + "CONVERT(DATE, GETDATE()), N'Đang làm')")) {
                    ps.setString(1, maNV);
                    ps.setString(2, ten);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO dbo.TaiKhoan "
                        + "(TenDangNhap, MatKhau, MaNV, MaVaiTro, TrangThai) "
                        + "VALUES (?, ?, ?, 'ADMIN', 1)")) {
                    ps.setString(1, user);
                    ps.setString(2, hash);
                    ps.setString(3, maNV);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public TaiKhoan dangNhap(String tenDangNhap, char[] matKhau) throws SQLException {
        if (tenDangNhap == null || tenDangNhap.isBlank() || matKhau == null
                || matKhau.length == 0) return null;
        String hash = MaHoa.sha256(new String(matKhau));
        Arrays.fill(matKhau, '\0');
        String sql = "SELECT tk.TenDangNhap, tk.MaNV, tk.MaVaiTro "
                + "FROM dbo.TaiKhoan tk JOIN dbo.NhanVien nv ON nv.MaNV = tk.MaNV "
                + "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ? "
                + "AND tk.TrangThai = 1 AND nv.TrangThai <> N'Đã nghỉ việc'";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap.trim());
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                TaiKhoan account = new TaiKhoan();
                account.setTenDangNhap(rs.getString("TenDangNhap"));
                account.setMaNV(rs.getString("MaNV"));
                account.setMaVaiTro(rs.getString("MaVaiTro"));
                account.setTrangThai(true);
                return account;
            }
        }
    }
}

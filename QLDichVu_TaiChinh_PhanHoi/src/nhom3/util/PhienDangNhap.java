package nhom3.util;

import nhom3.model.TaiKhoan;

/**
 * Lưu tài khoản đang đăng nhập để các màn hình khác dùng chung.
 * Dùng biến static nên ở đâu cũng gọi được PhienDangNhap.getTaiKhoan()
 */
public class PhienDangNhap {

    private static TaiKhoan taiKhoan;

    public static TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public static void setTaiKhoan(TaiKhoan tk) {
        taiKhoan = tk;
    }

    public static void dangXuat() {
        taiKhoan = null;
    }

    public static boolean laAdmin() {
        return taiKhoan != null && taiKhoan.getMaVaiTro().equals("ADMIN");
    }

    public static boolean laVaiTro(String maVaiTro) {
        return taiKhoan != null && taiKhoan.getMaVaiTro().equals(maVaiTro);
    }
}

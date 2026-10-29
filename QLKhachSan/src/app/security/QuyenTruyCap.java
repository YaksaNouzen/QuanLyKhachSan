package app.security;

import java.util.Set;

/** Quyền mở màn hình theo mã vai trò đã có trong bảng VaiTro. */
public final class QuyenTruyCap {
    private static final Set<String> LE_TAN = Set.of("KHACHHANG", "DATPHONG", "LUUTRU");
    private static final Set<String> PHONG = Set.of("PHONG", "BUONGPHONG", "BAOTRI");
    private static final Set<String> KHO = Set.of("VATTU", "NHACUNGCAP", "PHIEUNHAP");
    private static final Set<String> TAI_CHINH = Set.of(
            "HOADON", "CHITIET", "THANHTOAN", "THONGKE", "DICHVU", "PHANHOI");
    private static final Set<String> ADMIN_ONLY = Set.of("NHANVIEN", "CALAM", "TAIKHOAN");

    private QuyenTruyCap() { }

    public static boolean duocMo(String vaiTro, String trang) {
        if (vaiTro == null || trang == null) return false;
        if (!LE_TAN.contains(trang) && !PHONG.contains(trang)
                && !KHO.contains(trang) && !TAI_CHINH.contains(trang)
                && !ADMIN_ONLY.contains(trang) && !"PHANCA".equals(trang)) return false;
        if ("ADMIN".equals(vaiTro)) return true;
        if ("PHANCA".equals(trang)) return Set.of("NV", "LETAN", "KETOAN").contains(vaiTro);
        return switch (vaiTro) {
            case "NV" -> PHONG.contains(trang) || "DICHVU".equals(trang);
            case "LETAN" -> LE_TAN.contains(trang) || PHONG.contains(trang)
                    || "PHANHOI".equals(trang);
            case "KETOAN" -> KHO.contains(trang) || TAI_CHINH.contains(trang);
            default -> false;
        };
    }

    public static boolean chiXemPhanHoi(String vaiTro) {
        return "LETAN".equals(vaiTro);
    }
}

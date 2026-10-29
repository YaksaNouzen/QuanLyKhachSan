package integration;

import app.security.QuyenTruyCap;
import java.util.Set;

public final class QuyenTruyCapTest {
    private static final Set<String> ALL = Set.of(
            "KHACHHANG", "DATPHONG", "LUUTRU", "PHONG", "BUONGPHONG", "BAOTRI",
            "NHANVIEN", "CALAM", "PHANCA", "TAIKHOAN", "VATTU", "NHACUNGCAP",
            "PHIEUNHAP", "HOADON", "CHITIET", "THANHTOAN", "THONGKE",
            "DICHVU", "PHANHOI");

    public static void main(String[] args) {
        checkRole("ADMIN", ALL);
        checkRole("NV", Set.of("PHONG", "BUONGPHONG", "BAOTRI", "DICHVU", "PHANCA"));
        checkRole("LETAN", Set.of("KHACHHANG", "DATPHONG", "LUUTRU",
                "PHONG", "BUONGPHONG", "BAOTRI", "PHANHOI", "PHANCA"));
        checkRole("KETOAN", Set.of("VATTU", "NHACUNGCAP", "PHIEUNHAP",
                "HOADON", "CHITIET", "THANHTOAN", "THONGKE", "DICHVU",
                "PHANHOI", "PHANCA"));
        checkRole("SAI", Set.of());
        if (!QuyenTruyCap.chiXemPhanHoi("LETAN")
                || QuyenTruyCap.chiXemPhanHoi("KETOAN")
                || QuyenTruyCap.chiXemPhanHoi("ADMIN")) {
            throw new AssertionError("Quyền chỉ xem phản hồi không đúng");
        }
        System.out.println("QuyenTruyCapTest PASS");
    }

    private static void checkRole(String role, Set<String> expected) {
        for (String page : ALL) {
            if (QuyenTruyCap.duocMo(role, page) != expected.contains(page)) {
                throw new AssertionError(role + " / " + page);
            }
        }
        if (QuyenTruyCap.duocMo(role, "KHONG_CO")) {
            throw new AssertionError("Màn hình lạ được mở: " + role);
        }
    }
}

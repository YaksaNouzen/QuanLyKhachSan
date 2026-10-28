package Module_TaiChinh.dao;

import java.time.LocalDate;

public class HoaDonLuuTruTest {
    public static void main(String[] args) {
        LocalDate ngay1 = LocalDate.of(2026, 10, 1);
        LocalDate ngay3 = LocalDate.of(2026, 10, 3);
        LocalDate ngay5 = LocalDate.of(2026, 10, 5);
        check(HoaDonLuuTruDAO.tinhSoDem(ngay1, ngay5, false) == 4,
                "trả sớm vẫn tính 4 đêm đã đặt");
        check(HoaDonLuuTruDAO.tinhSoDem(ngay1, ngay3, true) == 2,
                "chặng phòng cũ đến trước ngày đổi");
        check(HoaDonLuuTruDAO.tinhSoDem(ngay3, ngay5, false) == 2,
                "chặng phòng mới từ ngày đổi");
        check(HoaDonLuuTruDAO.tinhSoDem(ngay1, ngay1, true) == 0,
                "đổi cùng ngày không tính phòng cũ");
        check(HoaDonLuuTruDAO.tinhSoDem(ngay1, ngay3, false) == 2,
                "ngày đổi thuộc phòng mới");
        System.out.println("HoaDonLuuTruTest PASS");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}

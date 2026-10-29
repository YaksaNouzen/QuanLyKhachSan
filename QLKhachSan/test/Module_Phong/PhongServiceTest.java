package Module_Phong;

import java.math.BigDecimal;
import Module_Phong.dao.MemoryPhongDAO;
import Module_Phong.model.Phong;
import Module_Phong.model.TrangThaiPhong;
import Module_Phong.service.PhongService;

public final class PhongServiceTest {
    public static void main(String[] args) {
        MemoryPhongDAO dao = new MemoryPhongDAO();
        PhongService service = PhongService.demo(dao);
        check(service.taiDuLieu().phongs().size() == 9, "du lieu demo co 9 phong");
        check(service.getGiaHienHanh("P101").compareTo(new BigDecimal("650000")) == 0,
                "gia phong lay tu loai phong");

        Phong current = service.getPhong("P101");
        service.luuPhong(new Phong(current.maPhong(), current.soPhong(),
                current.maLoaiPhong(), current.maTang(), current.giaPhong(),
                TrangThaiPhong.DA_DAT, true), true);
        Phong saved = service.getPhong("P101");
        check(saved.trangThai() == TrangThaiPhong.TRONG && !saved.ngungSuDung(),
                "form phong khong duoc sua trang thai hay ngung su dung");

        expect(() -> service.luuPhong(new Phong("P109", "101", "STD", "T1",
                null, TrangThaiPhong.TRONG, false), false));
        service.ngungSuDung("P102");
        check(service.getPhong("P102").ngungSuDung(), "phong duoc ngung su dung mem");
        check(service.getPhongTrong().size() == 8, "phong ngung khong nam trong danh sach trong");
        service.kichHoatLai("P102");
        check(!service.getPhong("P102").ngungSuDung(), "phong duoc kich hoat lai");
        dao.setPhongDangCoKhach("P102", true);
        expect(() -> service.xoaPhongChuaDung("P102"));
        dao.setPhongDangCoKhach("P102", false);
        service.xoaPhongChuaDung("P102");
        check(service.taiDuLieu().phongs().size() == 8, "phong mau chua dung duoc xoa");
        expect(() -> service.xoaLoai("STD"));
        System.out.println("PhongServiceTest PASS");
    }

    private static void expect(Runnable action) {
        try {
            action.run();
            throw new AssertionError("du kien thao tac bi tu choi");
        } catch (IllegalArgumentException expected) {
            // Kiem tra service tu choi du lieu khong hop le.
        }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}

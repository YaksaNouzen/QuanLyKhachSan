package Module_BuongPhong;

import Module_BuongPhong.dao.MemoryVeSinhDAO;
import Module_BuongPhong.model.CongViecVeSinh;
import Module_BuongPhong.model.TrangThaiVeSinh;
import Module_BuongPhong.service.BuongPhongService;
import Module_Phong.dao.MemoryPhongDAO;
import Module_Phong.model.TrangThaiPhong;
import Module_Phong.service.PhongService;

public final class BuongPhongServiceTest {
    public static void main(String[] args) {
        MemoryPhongDAO phongDAO = new MemoryPhongDAO();
        PhongService.demo(phongDAO);
        MemoryVeSinhDAO veSinhDAO = new MemoryVeSinhDAO(phongDAO);
        BuongPhongService service = new BuongPhongService(veSinhDAO);

        service.phanCong("P101", "Don phong", "Lan");
        String ma = viecDau(veSinhDAO).maVeSinh();
        service.batDauDon(ma);
        check(phongDAO.giaoDich(s -> s.phong("P101").trangThai())
                        == TrangThaiPhong.DANG_DON,
                "bat dau don chuyen trang thai phong");

        phongDAO.setPhongDangCoKhach("P101", true);
        expect(() -> service.xacNhanHoanThanh(ma, "Minh"));
        CongViecVeSinh sauLoi = veSinhDAO.giaoDich(s -> s.congViec(ma));
        check(sauLoi.trangThai() == TrangThaiVeSinh.DANG_DON,
                "loi guard khong doi trang thai cong viec");
        check(phongDAO.giaoDich(s -> s.phong("P101").trangThai())
                        == TrangThaiPhong.DANG_DON,
                "loi guard rollback trang thai phong");

        phongDAO.setPhongDangCoKhach("P101", false);
        service.xacNhanHoanThanh(ma, "Minh");
        check(phongDAO.giaoDich(s -> s.phong("P101").trangThai()) == TrangThaiPhong.TRONG,
                "hoan thanh don mo lai phong");

        service.phanCong("P102", "Don theo lich", "Hoa");
        String maHuy = viecDau(veSinhDAO).maVeSinh();
        service.huyPhanCong(maHuy);
        check(veSinhDAO.giaoDich(s -> s.congViec(maHuy).trangThai()) == TrangThaiVeSinh.HUY,
                "huy phan cong cho don");
        System.out.println("BuongPhongServiceTest PASS");
    }

    private static CongViecVeSinh viecDau(MemoryVeSinhDAO dao) {
        return dao.giaoDich(s -> s.congViecs().get(s.congViecs().size() - 1));
    }

    private static void expect(Runnable action) {
        try {
            action.run();
            throw new AssertionError("du kien thao tac bi tu choi");
        } catch (IllegalArgumentException expected) {
            // Kiem tra guard cua nghiep vu.
        }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}

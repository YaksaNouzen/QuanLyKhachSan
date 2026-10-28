package Module_BaoTri;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import Module_BaoTri.dao.MemoryBaoTriDAO;
import Module_BaoTri.model.MucDoUuTien;
import Module_BaoTri.model.PhieuBaoTri;
import Module_BaoTri.model.TrangThaiBaoTri;
import Module_BaoTri.service.BaoTriService;
import Module_Phong.dao.MemoryPhongDAO;
import Module_Phong.model.TrangThaiPhong;
import Module_Phong.service.PhongService;

public final class BaoTriServiceTest {
    public static void main(String[] args) {
        MemoryPhongDAO phongDAO = new MemoryPhongDAO();
        PhongService.demo(phongDAO);
        MemoryBaoTriDAO baoTriDAO = new MemoryBaoTriDAO(phongDAO);
        BaoTriService service = new BaoTriService(baoTriDAO);

        service.baoHong("P101", "Dieu hoa hu hong", "An", MucDoUuTien.CAO);
        String ma = phieuDau(baoTriDAO).maBaoTri();
        phongDAO.setPhongDangCoKhach("P101", true);
        expect(() -> service.lapPhieuVaBatDauSua(ma, "Binh"));
        check(baoTriDAO.giaoDich(s -> s.phieu(ma).trangThai()) == TrangThaiBaoTri.BAO_HONG,
                "guard khong doi phieu bao hong");

        phongDAO.setPhongDangCoKhach("P101", false);
        service.lapPhieuVaBatDauSua(ma, "Binh");
        check(phongDAO.giaoDich(s -> s.phong("P101").trangThai()) == TrangThaiPhong.BAO_TRI,
                "bat dau sua khoa phong");
        service.hoanTatSua(ma, new BigDecimal("125000.50"), "Thay quat dieu hoa");
        PhieuBaoTri xong = baoTriDAO.giaoDich(s -> s.phieu(ma));
        check(xong.trangThai() == TrangThaiBaoTri.HOAN_TAT, "phieu sua da hoan tat");
        check(xong.chiPhi().compareTo(new BigDecimal("125000.50")) == 0,
                "chi phi duoc luu");
        check(phongDAO.giaoDich(s -> s.phong("P101").trangThai()) == TrangThaiPhong.TRONG,
                "hoan tat sua mo lai phong");

        service.baoHong("P102", "Vo nuoc", "Chi", MucDoUuTien.TRUNG_BINH);
        String maCho = phieuDau(baoTriDAO).maBaoTri();
        phongDAO.setPhongConGiuDen("P102", LocalDateTime.now().plusDays(1));
        expect(() -> service.lapPhieuVaBatDauSua(maCho, "Binh"));
        phongDAO.setPhongConGiuDen("P102", null);
        service.huyBaoHong(maCho);
        check(baoTriDAO.giaoDich(s -> s.phieu(maCho).trangThai()) == TrangThaiBaoTri.HUY,
                "huy phieu chua bat dau");
        System.out.println("BaoTriServiceTest PASS");
    }

    private static PhieuBaoTri phieuDau(MemoryBaoTriDAO dao) {
        return dao.giaoDich(s -> s.phieuBaoTris().get(s.phieuBaoTris().size() - 1));
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

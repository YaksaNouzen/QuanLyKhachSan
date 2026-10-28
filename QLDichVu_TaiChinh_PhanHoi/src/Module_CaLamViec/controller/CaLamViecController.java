package Module_CaLamViec.controller;

import Module_CaLamViec.model.CaLam;
import Module_CaLamViec.model.PhanCa;
import Module_CaLamViec.service.CaLamViecService;
import Module_NhanVien.model.NhanVien;
import java.sql.Date;
import java.util.List;

public class CaLamViecController {
    private final CaLamViecService service = new CaLamViecService();

    public List<CaLam> getAll() { return service.getAll(); }
    public CaLam getById(String maCa) { return service.getById(maCa); }
    public boolean them(CaLam ca) { return service.them(ca); }
    public boolean sua(CaLam ca) { return service.sua(ca); }
    public boolean xoa(String maCa) { return service.xoa(maCa); }
    public List<NhanVien> getNhanVienDangLam() { return service.getNhanVienDangLam(); }
    public List<PhanCa> getLichTruc(Date tuNgay, Date denNgay, String maNV) {
        return service.getLichTruc(tuNgay, denNgay, maNV);
    }
    public CaLam kiemTraTrungCa(String maNV, CaLam ca, Date ngay) {
        return service.kiemTraTrungCa(maNV, ca, ngay);
    }
    public boolean them(PhanCa pc) { return service.them(pc); }
    public boolean xoa(int maPhanCa) { return service.xoa(maPhanCa); }
    public boolean banGiao(int maPhanCa, String noiDung) {
        return service.banGiao(maPhanCa, noiDung);
    }
}

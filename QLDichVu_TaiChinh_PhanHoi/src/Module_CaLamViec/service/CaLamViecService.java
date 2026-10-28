package Module_CaLamViec.service;

import Module_CaLamViec.dao.CaLamDAO;
import Module_CaLamViec.dao.PhanCaDAO;
import Module_CaLamViec.model.CaLam;
import Module_CaLamViec.model.PhanCa;
import Module_NhanVien.service.NhanVienService;
import Module_NhanVien.model.NhanVien;
import java.sql.Date;
import java.util.List;

public class CaLamViecService {
    private final CaLamDAO caLamDao = new CaLamDAO();
    private final PhanCaDAO phanCaDao = new PhanCaDAO();
    private final NhanVienService nhanVienService = new NhanVienService();

    public List<CaLam> getAll() { return caLamDao.getAll(); }
    public CaLam getById(String maCa) { return caLamDao.getById(maCa); }
    public boolean them(CaLam ca) { return caLamDao.them(ca); }
    public boolean sua(CaLam ca) { return caLamDao.sua(ca); }
    public boolean xoa(String maCa) { return caLamDao.xoa(maCa); }
    public List<NhanVien> getNhanVienDangLam() { return nhanVienService.getNhanVienDangLam(); }
    public List<PhanCa> getLichTruc(Date tuNgay, Date denNgay, String maNV) {
        return phanCaDao.getLichTruc(tuNgay, denNgay, maNV);
    }
    public CaLam kiemTraTrungCa(String maNV, CaLam ca, Date ngay) {
        return phanCaDao.kiemTraTrungCa(maNV, ca, ngay);
    }
    public boolean them(PhanCa pc) { return phanCaDao.them(pc); }
    public boolean xoa(int maPhanCa) { return phanCaDao.xoa(maPhanCa); }
    public boolean banGiao(int maPhanCa, String noiDung) {
        return phanCaDao.banGiao(maPhanCa, noiDung);
    }
}

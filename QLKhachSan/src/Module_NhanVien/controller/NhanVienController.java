package Module_NhanVien.controller;

import Module_NhanVien.model.NhanVien;
import Module_NhanVien.service.NhanVienService;
import java.util.List;

public class NhanVienController {
    private final NhanVienService service = new NhanVienService();

    public List<NhanVien> getAll() { return service.getAll(); }
    public List<NhanVien> getNhanVienDangLam() { return service.getNhanVienDangLam(); }
    public List<NhanVien> timKiem(String tuKhoa, String boPhan, String trangThai) {
        return service.timKiem(tuKhoa, boPhan, trangThai);
    }
    public NhanVien getById(String maNV) { return service.getById(maNV); }
    public boolean them(NhanVien nv) { return service.them(nv); }
    public boolean sua(NhanVien nv) { return service.sua(nv); }
    public boolean xoa(String maNV) { return service.xoa(maNV); }
}

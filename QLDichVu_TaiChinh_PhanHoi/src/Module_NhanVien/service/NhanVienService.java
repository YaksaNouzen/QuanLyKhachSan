package Module_NhanVien.service;

import Module_NhanVien.dao.NhanVienDAO;
import Module_NhanVien.model.NhanVien;
import java.util.List;

/** Tầng nghiệp vụ nhân viên; giữ các quy tắc hiện có trong lúc chuẩn hóa cấu trúc. */
public class NhanVienService {
    private final NhanVienDAO dao = new NhanVienDAO();

    public List<NhanVien> getAll() { return dao.getAll(); }
    public List<NhanVien> getNhanVienDangLam() { return dao.getNhanVienDangLam(); }
    public List<NhanVien> timKiem(String tuKhoa, String boPhan, String trangThai) {
        return dao.timKiem(tuKhoa, boPhan, trangThai);
    }
    public NhanVien getById(String maNV) { return dao.getById(maNV); }
    public boolean them(NhanVien nv) { return dao.them(nv); }
    public boolean sua(NhanVien nv) { return dao.sua(nv); }
    public boolean xoa(String maNV) { return dao.xoa(maNV); }
}

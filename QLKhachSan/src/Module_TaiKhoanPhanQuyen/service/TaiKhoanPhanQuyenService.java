package Module_TaiKhoanPhanQuyen.service;

import Module_NhanVien.model.NhanVien;
import Module_NhanVien.service.NhanVienService;
import Module_TaiKhoanPhanQuyen.dao.TaiKhoanDAO;
import Module_TaiKhoanPhanQuyen.dao.VaiTroDAO;
import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import Module_TaiKhoanPhanQuyen.model.VaiTro;
import java.util.List;

public class TaiKhoanPhanQuyenService {
    private final TaiKhoanDAO dao = new TaiKhoanDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();
    private final NhanVienService nhanVienService = new NhanVienService();

    public List<TaiKhoan> getAll() { return dao.getAll(); }
    public List<NhanVien> getNhanVienDangLam() { return nhanVienService.getNhanVienDangLam(); }
    public List<VaiTro> getVaiTro() { return vaiTroDAO.getAll(); }
    public boolean kiemTraTenDangNhap(String tenDangNhap) {
        return dao.kiemTraTenDangNhap(tenDangNhap);
    }
    public boolean nhanVienDaCoTaiKhoan(String maNV) { return dao.nhanVienDaCoTaiKhoan(maNV); }
    public boolean them(TaiKhoan tk) { return dao.them(tk); }
    public boolean sua(TaiKhoan tk) { return dao.sua(tk); }
    public boolean xoa(String tenDangNhap) { return dao.xoa(tenDangNhap); }
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        return dao.capNhatMatKhau(tenDangNhap, matKhauMoi);
    }
}

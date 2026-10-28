package Module_TaiKhoanPhanQuyen.controller;

import Module_NhanVien.model.NhanVien;
import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import Module_TaiKhoanPhanQuyen.model.VaiTro;
import Module_TaiKhoanPhanQuyen.service.TaiKhoanPhanQuyenService;
import java.util.List;

public class TaiKhoanPhanQuyenController {
    private final TaiKhoanPhanQuyenService service = new TaiKhoanPhanQuyenService();

    public List<TaiKhoan> getAll() { return service.getAll(); }
    public List<NhanVien> getNhanVienDangLam() { return service.getNhanVienDangLam(); }
    public List<VaiTro> getVaiTro() { return service.getVaiTro(); }
    public boolean kiemTraTenDangNhap(String tenDangNhap) {
        return service.kiemTraTenDangNhap(tenDangNhap);
    }
    public boolean nhanVienDaCoTaiKhoan(String maNV) { return service.nhanVienDaCoTaiKhoan(maNV); }
    public boolean them(TaiKhoan tk) { return service.them(tk); }
    public boolean sua(TaiKhoan tk) { return service.sua(tk); }
    public boolean xoa(String tenDangNhap) { return service.xoa(tenDangNhap); }
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        return service.capNhatMatKhau(tenDangNhap, matKhauMoi);
    }
}

package Module_QuanLyNhapHang.controller;

import Module_QuanLyNhapHang.model.PhieuNhap;
import Module_QuanLyNhapHang.service.PhieuNhapService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PhieuNhapController {
    private final PhieuNhapService service = new PhieuNhapService();
    public List<PhieuNhap> timKiem(String q, String status, LocalDate from, LocalDate to) throws SQLException { return service.timKiem(q, status, from, to); }
    public PhieuNhap timTheoMa(String ma) throws SQLException { return service.timTheoMa(ma); }
    public void luu(PhieuNhap value) throws SQLException { service.luu(value); }
    public void xacNhan(String ma) throws SQLException { service.xacNhan(ma); }
    public void huy(String ma) throws SQLException { service.huy(ma); }
}

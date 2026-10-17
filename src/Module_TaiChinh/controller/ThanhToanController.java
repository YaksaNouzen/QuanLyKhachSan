package Module_TaiChinh.controller;

import Module_TaiChinh.model.ThanhToan;
import Module_TaiChinh.service.ThanhToanService;
import java.util.List;

public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    public ThanhToanController() {
        thanhToanService = new ThanhToanService();
    }

    public List<ThanhToan> getAll() {
        return thanhToanService.getAll();
    }

    public List<ThanhToan> getByMaHoaDon(String maHoaDon) {
        return thanhToanService.getByMaHoaDon(maHoaDon);
    }

    public ThanhToan findById(String maThanhToan) {
        return thanhToanService.findById(maThanhToan);
    }

    public double tinhTongDaThanhToan(String maHoaDon) {
        return thanhToanService.tinhTongDaThanhToan(maHoaDon);
    }

    public double tinhSoTienConLai(String maHoaDon) {
        return thanhToanService.tinhSoTienConLai(maHoaDon);
    }

    public boolean themThanhToan(ThanhToan thanhToan) {
        return thanhToanService.insert(thanhToan);
    }

    public boolean suaThanhToan(ThanhToan thanhToan) {
        return thanhToanService.update(thanhToan);
    }

    public boolean xoaThanhToan(String maThanhToan) {
        return thanhToanService.delete(maThanhToan);
    }

    public boolean capNhatTrangThaiHoaDon(String maHoaDon) {
        return thanhToanService.capNhatTrangThaiHoaDon(maHoaDon);
    }
}

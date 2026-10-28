package Module_TaiChinh.controller;

import Module_TaiChinh.model.ChiTietHoaDon;
import Module_TaiChinh.service.ChiTietHoaDonService;
import java.util.List;

public class ChiTietHoaDonController {

    private final ChiTietHoaDonService chiTietHoaDonService;

    // Khoi tao Service
    public ChiTietHoaDonController() {
        chiTietHoaDonService = new ChiTietHoaDonService();
    }

    // 1. Lay tat ca chi tiet hoa don
    public List<ChiTietHoaDon> getAll() {
        return chiTietHoaDonService.getAll();
    }

    // 2. Lay chi tiet theo ma hoa don
    public List<ChiTietHoaDon> getByMaHoaDon(String maHoaDon) {
        return chiTietHoaDonService.getByMaHoaDon(maHoaDon);
    }

    // 3. Them chi tiet hoa don
    public boolean themChiTiet(ChiTietHoaDon chiTiet) {
        return chiTietHoaDonService.insert(chiTiet);
    }

    // 4. Sua chi tiet hoa don
    public boolean suaChiTiet(ChiTietHoaDon chiTiet) {
        return chiTietHoaDonService.update(chiTiet);
    }

    // 5. Xoa mot dong chi tiet
    public boolean xoaChiTiet(String maChiTiet) {
        return chiTietHoaDonService.delete(maChiTiet);
    }

    // 6. Xoa tat ca chi tiet cua mot hoa don
    public boolean xoaTheoMaHoaDon(String maHoaDon) {
        return chiTietHoaDonService.deleteByMaHoaDon(maHoaDon);
    }

    // 7. Tinh thanh tien cua mot dong chi tiet
    public double tinhThanhTien(ChiTietHoaDon chiTiet) {
        return chiTietHoaDonService.tinhThanhTien(chiTiet);
    }

    // 8. Tinh tong tien cac dong chi tiet cua mot hoa don
    public double tinhTongTien(String maHoaDon) {
        return chiTietHoaDonService.tinhTongTien(maHoaDon);
    }
}
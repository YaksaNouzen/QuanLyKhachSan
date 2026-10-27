
package Module_TaiChinh.controller;

import Module_TaiChinh.model.HoaDon;
import Module_TaiChinh.service.HoaDonService;

import java.util.List;


public class HoaDonController {

    private final HoaDonService hoaDonService;

    // Khoi tao Controller va Service
    public HoaDonController() {
        hoaDonService = new HoaDonService();
    }

    // 1. Lay danh sach tat ca hoa don
    public List<HoaDon> getAll() {
        return hoaDonService.getAll();
    }

    // 2. Tim hoa don theo ma
    public HoaDon findById(String maHoaDon) {

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma hoa don khong duoc de trong."
            );
        }

        return hoaDonService.findById(maHoaDon.trim());
    }

    // 3. Tim kiem hoa don theo nhieu dieu kien
    public List<HoaDon> search(
            String maHoaDon,
            String maDatPhong,
            String maKhachHang,
            String tuNgay,
            String denNgay) {

        return hoaDonService.search(
                maHoaDon,
                maDatPhong,
                maKhachHang,
                tuNgay,
                denNgay
        );
    }

    // 4. Them hoa don
    public boolean themHoaDon(HoaDon hoaDon) {

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                    "Thong tin hoa don khong hop le."
            );
        }

        return hoaDonService.insert(hoaDon);
    }

    // 5. Sua hoa don
    public boolean suaHoaDon(HoaDon hoaDon) {

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                    "Thong tin hoa don khong hop le."
            );
        }

        if (hoaDon.getMaHoaDon() == null
                || hoaDon.getMaHoaDon().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma hoa don khong duoc de trong."
            );
        }

        return hoaDonService.update(hoaDon);
    }

    // 6. Xoa hoa don
    public boolean xoaHoaDon(String maHoaDon) {

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Can cung cap ma hoa don de xoa."
            );
        }

        return hoaDonService.delete(maHoaDon.trim());
    }

    // 7. Tinh va cap nhat tong tien hoa don
    public double tinhTongTien(String maHoaDon) {

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma hoa don khong duoc de trong."
            );
        }

        return hoaDonService.tinhTongTien(maHoaDon.trim());
    }

    // 8. Tinh va cap nhat tong tien hoa don
    public boolean capNhatTongTien(String maHoaDon) {

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma hoa don khong duoc de trong."
            );
        }

        return hoaDonService.capNhatTongTien(maHoaDon.trim());
    }
}

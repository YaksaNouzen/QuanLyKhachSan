
package Module_TaiChinh.service;

import Module_TaiChinh.dao.ChiTietHoaDonDAO;
import Module_TaiChinh.model.ChiTietHoaDon;

import java.util.Collections;
import java.util.List;

public class ChiTietHoaDonService {

    private final ChiTietHoaDonDAO chiTietHoaDonDAO;

    // Khoi tao doi tuong DAO de lam viec voi database
    public ChiTietHoaDonService() {
        chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    }

    // 1. Lay tat ca chi tiet hoa don
    public List<ChiTietHoaDon> getAll() {
        return chiTietHoaDonDAO.getAll();
    }

    // 2. Lay cac dong chi tiet cua mot hoa don
    public List<ChiTietHoaDon> getByMaHoaDon(String maHoaDon) {
        if (!coGiaTri(maHoaDon)) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }

        return chiTietHoaDonDAO.getByMaHoaDon(maHoaDon.trim());
    }

    // 3. Them chi tiet hoa don
    public boolean insert(ChiTietHoaDon chiTiet) {
        kiemTraChiTiet(chiTiet);

        // Tinh thanh tien truoc khi luu vao database
        chiTiet.setThanhTien(tinhThanhTien(chiTiet));

        return chiTietHoaDonDAO.insert(chiTiet);
    }

    // 4. Cap nhat chi tiet hoa don
    public boolean update(ChiTietHoaDon chiTiet) {
        kiemTraChiTiet(chiTiet);

        if (!coGiaTri(chiTiet.getMaChiTiet())) {
            throw new IllegalArgumentException(
                "Ma chi tiet hoa don khong duoc de trong."
            );
        }

        // Neu thay doi so luong hoac don gia,
        // thanh tien phai duoc tinh lai
        chiTiet.setThanhTien(tinhThanhTien(chiTiet));

        return chiTietHoaDonDAO.update(chiTiet);
    }

    // 5. Xoa mot dong chi tiet hoa don
    public boolean delete(String maChiTiet) {
        if (!coGiaTri(maChiTiet)) {
            throw new IllegalArgumentException(
                "Ma chi tiet hoa don khong duoc de trong."
            );
        }

        return chiTietHoaDonDAO.delete(maChiTiet.trim());
    }

    // 6. Xoa tat ca chi tiet cua mot hoa don
    public boolean deleteByMaHoaDon(String maHoaDon) {
        if (!coGiaTri(maHoaDon)) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }

        return chiTietHoaDonDAO.deleteByMaHoaDon(
            maHoaDon.trim()
        );
    }

    // 7. Tinh thanh tien cua mot dong chi tiet
    public double tinhThanhTien(ChiTietHoaDon chiTiet) {
        if (chiTiet == null) {
            throw new IllegalArgumentException(
                "Chi tiet hoa don khong duoc null."
            );
        }

        if (chiTiet.getSoLuong() <= 0) {
            throw new IllegalArgumentException(
                "So luong phai lon hon 0."
            );
        }

        double donGia = chiTiet.getDonGia();

        if (!Double.isFinite(donGia) || donGia < 0) {
            throw new IllegalArgumentException(
                "Don gia khong hop le."
            );
        }

        return chiTiet.getSoLuong() * donGia;
    }

    // 8. Tinh tong tien cac dong chi tiet cua mot hoa don
    public double tinhTongTien(String maHoaDon) {
        List<ChiTietHoaDon> danhSach =
            getByMaHoaDon(maHoaDon);

        if (danhSach == null) {
            danhSach = Collections.emptyList();
        }

        double tongTien = 0;

        for (ChiTietHoaDon chiTiet : danhSach) {
            tongTien += tinhThanhTien(chiTiet);
        }

        return tongTien;
    }

    // 9. Kiem tra du lieu truoc khi them hoac sua
    private void kiemTraChiTiet(ChiTietHoaDon chiTiet) {
        if (chiTiet == null) {
            throw new IllegalArgumentException(
                "Du lieu chi tiet hoa don khong hop le."
            );
        }

        if (!coGiaTri(chiTiet.getMaHoaDon())) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }

        if (!coGiaTri(chiTiet.getLoaiChiPhi())) {
            throw new IllegalArgumentException(
                "Loai chi phi khong duoc de trong."
            );
        }

        if (!coGiaTri(chiTiet.getMoTa())) {
            throw new IllegalArgumentException(
                "Mo ta chi phi khong duoc de trong."
            );
        }

        // Goi ham tinh tien de kiem tra so luong va don gia
        tinhThanhTien(chiTiet);
    }

    // 10. Ham kiem tra chuoi co rong hay khong
    private boolean coGiaTri(String giaTri) {
        return giaTri != null && !giaTri.trim().isEmpty();
    }
}

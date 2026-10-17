
package Module_TaiChinh.service;

import Module_TaiChinh.Dao.ThanhToanDAO;
import Module_TaiChinh.model.HoaDon;
import Module_TaiChinh.model.ThanhToan;

import java.util.List;

public class ThanhToanService {

    private final ThanhToanDAO thanhToanDAO;
    private final HoaDonService hoaDonService;

    public ThanhToanService() {
        thanhToanDAO = new ThanhToanDAO();
        hoaDonService = new HoaDonService();
    }

    // 1. Lay tat ca cac lan thanh toan
    public List<ThanhToan> getAll() {
        return thanhToanDAO.getAll();
    }

    // 2. Lay lich su thanh toan cua mot hoa don
    public List<ThanhToan> getByMaHoaDon(String maHoaDon) {
        kiemTraMaHoaDon(maHoaDon);
        return thanhToanDAO.getByMaHoaDon(maHoaDon.trim());
    }

    // Tim mot giao dich theo ma
    public ThanhToan findById(String maThanhToan) {
        if (maThanhToan == null
                || maThanhToan.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma thanh toan khong duoc de trong."
            );
        }

        return thanhToanDAO.findById(maThanhToan.trim());
    }

    // 3. Tinh tong tien da thanh toan thanh cong
    public double tinhTongDaThanhToan(String maHoaDon) {
        HoaDon hoaDon = layHoaDonHopLe(maHoaDon);

        List<ThanhToan> danhSach =
            thanhToanDAO.getByMaHoaDon(hoaDon.getMaHoaDon());

        double tongDaThanhToan = 0;

        if (danhSach != null) {
            for (ThanhToan tt : danhSach) {
                if (tt != null && laThanhToanThanhCong(tt)) {
                    tongDaThanhToan += tt.getSoTien();
                }
            }
        }

        return tongDaThanhToan;
    }

    // 4. Tinh so tien con phai tra
    public double tinhSoTienConLai(String maHoaDon) {
        HoaDon hoaDon = layHoaDonHopLe(maHoaDon);

        double tongTienHoaDon =
            hoaDonService.tinhTongTien(hoaDon.getMaHoaDon());

        double daThanhToan =
            tinhTongDaThanhToan(hoaDon.getMaHoaDon());

        double conLai = tongTienHoaDon - daThanhToan;

        return Math.max(0, conLai);
    }

    // 5. Them mot lan thanh toan
    public boolean insert(ThanhToan thanhToan) {
        kiemTraThanhToan(thanhToan);

        if (findById(thanhToan.getMaThanhToan()) != null) {
            throw new IllegalArgumentException(
                "Ma thanh toan da ton tai."
            );
        }

        HoaDon hoaDon = layHoaDonHopLe(
            thanhToan.getMaHoaDon()
        );

        // Chi kiem tra vuot tien neu day la thanh toan thanh cong
        if (laThanhToanThanhCong(thanhToan)) {
            double conLai =
                tinhSoTienConLai(hoaDon.getMaHoaDon());

            if (thanhToan.getSoTien() > conLai) {
                throw new IllegalArgumentException(
                    "So tien thanh toan vuot qua so tien con lai."
                );
            }
        }

        // Luu giao dich vao database
        boolean ketQua = thanhToanDAO.insert(thanhToan);

        if (ketQua && laThanhToanThanhCong(thanhToan)) {
            capNhatTrangThaiHoaDon(hoaDon.getMaHoaDon());
        }

        return ketQua;
    }

    // 6. Sua mot giao dich thanh toan
    public boolean update(ThanhToan thanhToan) {
        kiemTraThanhToan(thanhToan);

        ThanhToan giaoDichCu = findById(
            thanhToan.getMaThanhToan()
        );

        if (giaoDichCu == null) {
            throw new IllegalArgumentException(
                "Khong tim thay giao dich can cap nhat."
            );
        }

        HoaDon hoaDonMoi = layHoaDonHopLe(
            thanhToan.getMaHoaDon()
        );

        if (laThanhToanThanhCong(thanhToan)) {
            double soTienCoTheThanhToan =
                tinhSoTienConLai(hoaDonMoi.getMaHoaDon());

            // Khi sua giao dich cua cung hoa don, giao dich cu dang
            // nam trong tong da tra. Cong lai so tien cu de so sanh.
            if (giaoDichCu.getMaHoaDon().equals(hoaDonMoi.getMaHoaDon())
                    && laThanhToanThanhCong(giaoDichCu)) {
                soTienCoTheThanhToan += giaoDichCu.getSoTien();
            }

            if (thanhToan.getSoTien() > soTienCoTheThanhToan) {
                throw new IllegalArgumentException(
                    "So tien thanh toan vuot qua so tien co the cap nhat."
                );
            }
        }

        boolean ketQua = thanhToanDAO.update(thanhToan);

        if (ketQua) {
            capNhatTrangThaiHoaDon(giaoDichCu.getMaHoaDon());

            if (!giaoDichCu.getMaHoaDon().equals(
                    thanhToan.getMaHoaDon())) {
                capNhatTrangThaiHoaDon(thanhToan.getMaHoaDon());
            }
        }

        return ketQua;
    }

    // 7. Cap nhat trang thai hoa don sau khi thanh toan
    public boolean capNhatTrangThaiHoaDon(String maHoaDon) {
        HoaDon hoaDon = layHoaDonHopLe(maHoaDon);

        double tongTien =
            hoaDonService.tinhTongTien(hoaDon.getMaHoaDon());

        double daThanhToan =
            tinhTongDaThanhToan(hoaDon.getMaHoaDon());

        if (daThanhToan >= tongTien) {
            hoaDon.setTrangThai("Đã thanh toán");
        } else {
            hoaDon.setTrangThai("Chưa thanh toán");
        }

        return hoaDonService.update(hoaDon);
    }

    // 8. Xoa mot giao dich thanh toan
    public boolean delete(String maThanhToan) {
        if (maThanhToan == null
                || maThanhToan.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma thanh toan khong duoc de trong."
            );
        }

        ThanhToan giaoDich = findById(maThanhToan.trim());

        if (giaoDich == null) {
            throw new IllegalArgumentException(
                "Khong tim thay giao dich can xoa."
            );
        }

        boolean ketQua = thanhToanDAO.delete(maThanhToan.trim());

        if (ketQua) {
            capNhatTrangThaiHoaDon(giaoDich.getMaHoaDon());
        }

        return ketQua;
    }

    // 9. Kiem tra du lieu thanh toan
    private void kiemTraThanhToan(ThanhToan tt) {
        if (tt == null) {
            throw new IllegalArgumentException(
                "Du lieu thanh toan khong hop le."
            );
        }

        if (tt.getMaThanhToan() == null
                || tt.getMaThanhToan().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma thanh toan khong duoc de trong."
            );
        }

        kiemTraMaHoaDon(tt.getMaHoaDon());

        if (tt.getNgayThanhToan() == null
                || tt.getNgayThanhToan().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ngay thanh toan khong duoc de trong."
            );
        }

        try {
            java.time.LocalDate.parse(
                tt.getNgayThanhToan().trim()
            );
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Ngay thanh toan phai co dang yyyy-MM-dd."
            );
        }

        if (tt.getPhuongThuc() == null
                || tt.getPhuongThuc().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Phuong thuc thanh toan khong duoc de trong."
            );
        }

        if (tt.getTrangThai() == null
                || tt.getTrangThai().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Trang thai thanh toan khong duoc de trong."
            );
        }

        if (!Double.isFinite(tt.getSoTien())
                || tt.getSoTien() <= 0) {
            throw new IllegalArgumentException(
                "So tien thanh toan phai lon hon 0."
            );
        }
    }

    // 10. Kiem tra ma hoa don
    private void kiemTraMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }
    }

    // 11. Lay hoa don va dam bao hoa don ton tai
    private HoaDon layHoaDonHopLe(String maHoaDon) {
        kiemTraMaHoaDon(maHoaDon);

        HoaDon hoaDon =
            hoaDonService.findById(maHoaDon.trim());

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                "Khong tim thay hoa don."
            );
        }

        return hoaDon;
    }

    // 12. Xac dinh giao dich da thanh toan thanh cong hay chua
    private boolean laThanhToanThanhCong(ThanhToan tt) {
        if (tt == null || tt.getTrangThai() == null) {
            return false;
        }

        String trangThai = tt.getTrangThai().trim();

        return trangThai.equalsIgnoreCase("Thành công")
            || trangThai.equalsIgnoreCase("Đã thanh toán");
    }
}

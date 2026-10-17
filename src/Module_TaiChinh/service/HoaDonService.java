
package Module_TaiChinh.service;

import Module_TaiChinh.Dao.HoaDonDAO;
import Module_TaiChinh.model.HoaDon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HoaDonService {

    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonService chiTietHoaDonService;

    // Khoi tao cac doi tuong can thiet
    public HoaDonService() {
        hoaDonDAO = new HoaDonDAO();
        chiTietHoaDonService = new ChiTietHoaDonService();
    }

    // 1. Lay tat ca hoa don
    public List<HoaDon> getAll() {
        return hoaDonDAO.getAll();
    }

    // 2. Tim hoa don theo ma
    public HoaDon findById(String maHoaDon) {
        if (!coGiaTri(maHoaDon)) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }

        return hoaDonDAO.findById(maHoaDon.trim());
    }

    // 3. Tim kiem hoa don theo cac dieu kien
    public List<HoaDon> search(
            String maHoaDon,
            String maDatPhong,
            String maKhachHang,
            String tuNgay,
            String denNgay) {

        kiemTraNgay(tuNgay, "Tu ngay");
        kiemTraNgay(denNgay, "Den ngay");

        if (coGiaTri(tuNgay) && coGiaTri(denNgay)) {
            LocalDate ngayBatDau = LocalDate.parse(tuNgay.trim());
            LocalDate ngayKetThuc = LocalDate.parse(denNgay.trim());

            if (ngayKetThuc.isBefore(ngayBatDau)) {
                throw new IllegalArgumentException(
                    "Den ngay khong duoc truoc tu ngay."
                );
            }
        }

        return hoaDonDAO.search(
            maHoaDon,
            maDatPhong,
            maKhachHang,
            tuNgay,
            denNgay
        );
    }

    // 4. Them hoa don
    public boolean insert(HoaDon hoaDon) {
        kiemTraHoaDon(hoaDon);

        if (findById(hoaDon.getMaHoaDon()) != null) {
            throw new IllegalArgumentException(
                "Ma hoa don da ton tai."
            );
        }

        // Hoa don moi chua co chi tiet thi tong tien ban dau bang 0
        hoaDon.setTongTien(0);

        return hoaDonDAO.insert(hoaDon);
    }

    // 5. Cap nhat thong tin hoa don
    public boolean update(HoaDon hoaDon) {
        kiemTraHoaDon(hoaDon);

        HoaDon hoaDonCu = findById(hoaDon.getMaHoaDon());

        if (hoaDonCu == null) {
            throw new IllegalArgumentException(
                "Khong tim thay hoa don can cap nhat."
            );
        }

        // Khong de viec sua thong tin chung lam mat tong tien da tinh
        hoaDon.setTongTien(hoaDonCu.getTongTien());

        return hoaDonDAO.update(hoaDon);
    }

    // 6. Tinh tong tien phai thanh toan
    // Cong cac dong chi tiet va tru tien giam
    public double tinhTongTien(String maHoaDon) {
        HoaDon hoaDon = findById(maHoaDon);

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                "Khong tim thay hoa don."
            );
        }

        double tongChiTiet =
            chiTietHoaDonService.tinhTongTien(maHoaDon);

        double tienGiam = hoaDon.getTienGiam();

        if (!Double.isFinite(tienGiam) || tienGiam < 0) {
            throw new IllegalArgumentException(
                "Tien giam khong hop le."
            );
        }

        double tongTien = tongChiTiet - tienGiam;

        // Khong cho phep tong tien am
        if (tongTien < 0) {
            throw new IllegalArgumentException(
                "Tien giam khong duoc lon hon tong tien chi tiet."
            );
        }

        return tongTien;
    }

    // 7. Tinh lai va luu tong tien vao database
    public boolean capNhatTongTien(String maHoaDon) {
        HoaDon hoaDon = findById(maHoaDon);

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                "Khong tim thay hoa don."
            );
        }

        double tongTienMoi = tinhTongTien(maHoaDon);

        hoaDon.setTongTien(tongTienMoi);

        return hoaDonDAO.update(hoaDon);
    }

    // 8. Xoa hoa don
    public boolean delete(String maHoaDon) {
        if (!coGiaTri(maHoaDon)) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }

        HoaDon hoaDon = findById(maHoaDon);

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                "Khong tim thay hoa don can xoa."
            );
        }

        return hoaDonDAO.delete(maHoaDon.trim());
    }

    // 9. Kiem tra du lieu hoa don
    private void kiemTraHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException(
                "Du lieu hoa don khong hop le."
            );
        }

        if (!coGiaTri(hoaDon.getMaHoaDon())) {
            throw new IllegalArgumentException(
                "Ma hoa don khong duoc de trong."
            );
        }

        if (!coGiaTri(hoaDon.getMaDatPhong())) {
            throw new IllegalArgumentException(
                "Ma dat phong khong duoc de trong."
            );
        }

        if (!coGiaTri(hoaDon.getNgayLap())) {
            throw new IllegalArgumentException(
                "Ngay lap khong duoc de trong."
            );
        }

        kiemTraNgay(hoaDon.getNgayLap(), "Ngay lap");

        double tienGiam = hoaDon.getTienGiam();

        if (!Double.isFinite(tienGiam) || tienGiam < 0) {
            throw new IllegalArgumentException(
                "Tien giam phai la so khong am hop le."
            );
        }

        if (!Double.isFinite(hoaDon.getTongTien())
                || hoaDon.getTongTien() < 0) {
            throw new IllegalArgumentException(
                "Tong tien khong hop le."
            );
        }

        if (!coGiaTri(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException(
                "Trang thai hoa don khong duoc de trong."
            );
        }
    }

    // 10. Kiem tra ngay theo dinh dang yyyy-MM-dd
    private void kiemTraNgay(String ngay, String tenTruong) {
        if (!coGiaTri(ngay)) {
            return;
        }

        try {
            LocalDate.parse(ngay.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                tenTruong + " phai co dinh dang yyyy-MM-dd."
            );
        }
    }

    // 11. Kiem tra chuoi co gia tri hay khong
    private boolean coGiaTri(String giaTri) {
        return giaTri != null && !giaTri.trim().isEmpty();
    }
}

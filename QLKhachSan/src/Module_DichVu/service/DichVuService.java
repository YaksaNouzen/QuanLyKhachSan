package Module_DichVu.service;

import Module_DichVu.dao.DichVuDAO;
import Module_DichVu.dao.LoaiDichVuDAO;
import Module_DichVu.model.DichVu;
import Module_DichVu.model.LoaiDichVu;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

/**
 * Chua quy tac nghiep vu cua module quan ly dich vu.
 * View va Controller khong truy cap DAO truc tiep.
 */
public class DichVuService {

    private final DichVuDAO dichVuDAO;
    private final LoaiDichVuDAO loaiDichVuDAO;

    public DichVuService() {
        this(new DichVuDAO(), new LoaiDichVuDAO());
    }

    public DichVuService(DichVuDAO dichVuDAO,
                         LoaiDichVuDAO loaiDichVuDAO) {
        if (dichVuDAO == null || loaiDichVuDAO == null) {
            throw new IllegalArgumentException("DAO dich vu khong hop le.");
        }
        this.dichVuDAO = dichVuDAO;
        this.loaiDichVuDAO = loaiDichVuDAO;
    }

    public List<DichVu> layTatCa() {
        try {
            return dichVuDAO.getAll();
        } catch (SQLException e) {
            throw loiDuLieu("Khong the tai danh sach dich vu.", e);
        }
    }

    public List<LoaiDichVu> layDanhSachLoai() {
        try {
            return loaiDichVuDAO.getAll();
        } catch (SQLException e) {
            throw loiDuLieu("Khong the tai danh sach loai dich vu.", e);
        }
    }

    public List<DichVu> timTheoTen(String tuKhoa) {
        String giaTri = chuanHoa(tuKhoa);
        if (giaTri.isEmpty()) {
            return layTatCa();
        }
        if (giaTri.length() > 150) {
            throw new IllegalArgumentException(
                    "Tu khoa tim kiem khong duoc qua 150 ky tu.");
        }

        try {
            return dichVuDAO.searchByName(giaTri);
        } catch (SQLException e) {
            throw loiDuLieu("Khong the tim kiem dich vu.", e);
        }
    }

    public boolean them(DichVu dichVu) {
        kiemTraVaChuanHoa(dichVu, false);
        try {
            return dichVuDAO.insert(dichVu);
        } catch (SQLException e) {
            throw loiDuLieu("Them dich vu that bai.", e);
        }
    }

    public boolean capNhat(DichVu dichVu) {
        kiemTraVaChuanHoa(dichVu, true);
        DichVu hienTai = timTheoMaNoiBo(dichVu.getMaDV());
        if (hienTai == null) {
            throw new IllegalArgumentException(
                    "Khong tim thay dich vu can cap nhat.");
        }

        try {
            return dichVuDAO.update(dichVu);
        } catch (SQLException e) {
            throw loiDuLieu("Cap nhat dich vu that bai.", e);
        }
    }

    public boolean ngungHoatDong(int maDichVu) {
        DichVu dichVu = yeuCauDichVuTonTai(maDichVu);
        if (!dichVu.isTrangThai()) {
            throw new IllegalArgumentException(
                    "Dich vu nay da ngung hoat dong.");
        }

        try {
            return dichVuDAO.delete(maDichVu);
        } catch (SQLException e) {
            throw loiDuLieu("Ngung hoat dong dich vu that bai.", e);
        }
    }

    public boolean khoiPhuc(int maDichVu) {
        DichVu dichVu = yeuCauDichVuTonTai(maDichVu);
        if (dichVu.isTrangThai()) {
            throw new IllegalArgumentException(
                    "Dich vu nay dang hoat dong.");
        }

        try {
            return dichVuDAO.restore(maDichVu);
        } catch (SQLException e) {
            throw loiDuLieu("Khoi phuc dich vu that bai.", e);
        }
    }

    private DichVu yeuCauDichVuTonTai(int maDichVu) {
        if (maDichVu <= 0) {
            throw new IllegalArgumentException("Ma dich vu khong hop le.");
        }
        DichVu dichVu = timTheoMaNoiBo(maDichVu);
        if (dichVu == null) {
            throw new IllegalArgumentException("Khong tim thay dich vu.");
        }
        return dichVu;
    }

    private DichVu timTheoMaNoiBo(int maDichVu) {
        try {
            return dichVuDAO.getById(maDichVu);
        } catch (SQLException e) {
            throw loiDuLieu("Khong the kiem tra dich vu.", e);
        }
    }

    private void kiemTraVaChuanHoa(DichVu dichVu,
                                   boolean yeuCauMa) {
        if (dichVu == null) {
            throw new IllegalArgumentException(
                    "Thong tin dich vu khong hop le.");
        }
        if (yeuCauMa && dichVu.getMaDV() <= 0) {
            throw new IllegalArgumentException("Ma dich vu khong hop le.");
        }
        if (dichVu.getMaLoaiDV() <= 0) {
            throw new IllegalArgumentException("Hay chon loai dich vu.");
        }

        String ten = batBuoc(dichVu.getTenDV(),
                "Ten dich vu", 150);
        String donVi = batBuoc(dichVu.getDonViTinh(),
                "Don vi tinh", 30);
        String moTa = chuanHoa(dichVu.getMoTa());
        if (moTa.length() > 255) {
            throw new IllegalArgumentException(
                    "Mo ta khong duoc qua 255 ky tu.");
        }

        BigDecimal donGia = dichVu.getDonGia();
        if (donGia == null || donGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Don gia phai la so khong am.");
        }
        donGia = donGia.setScale(2, RoundingMode.HALF_UP);
        int soChuSoNguyen = donGia.precision() - donGia.scale();
        if (soChuSoNguyen > 16) {
            throw new IllegalArgumentException(
                    "Don gia vuot qua gioi han DECIMAL(18,2).");
        }

        dichVu.setTenDV(ten);
        dichVu.setDonViTinh(donVi);
        dichVu.setMoTa(moTa);
        dichVu.setDonGia(donGia);
    }

    private static String batBuoc(String giaTri,
                                  String tenTruong,
                                  int doDaiToiDa) {
        String ketQua = chuanHoa(giaTri);
        if (ketQua.isEmpty()) {
            throw new IllegalArgumentException(
                    tenTruong + " khong duoc de trong.");
        }
        if (ketQua.length() > doDaiToiDa) {
            throw new IllegalArgumentException(
                    tenTruong + " khong duoc qua "
                    + doDaiToiDa + " ky tu.");
        }
        return ketQua;
    }

    private static String chuanHoa(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private static IllegalStateException loiDuLieu(
            String thongBao, SQLException nguyenNhan) {
        return new IllegalStateException(thongBao, nguyenNhan);
    }
}

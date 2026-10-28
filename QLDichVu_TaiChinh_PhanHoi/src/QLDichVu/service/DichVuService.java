package QLDichVu.service;

import QLDichVu.dao.DichVuDAO;
import QLDichVu.dao.LoaiDichVuDAO;
import QLDichVu.model.DichVu;
import QLDichVu.model.LoaiDichVu;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class DichVuService {
    private final DichVuDAO dao;
    private final LoaiDichVuDAO loaiDAO;

    public DichVuService() {
        this(new DichVuDAO(), new LoaiDichVuDAO());
    }

    // Cho phep kiem thu bang DAO gia, khong ket noi database.
    public DichVuService(DichVuDAO dao, LoaiDichVuDAO loaiDAO) {
        this.dao = java.util.Objects.requireNonNull(dao);
        this.loaiDAO = java.util.Objects.requireNonNull(loaiDAO);
    }

    public List<DichVu> getAll() throws SQLException { return dao.getAll(); }
    public List<LoaiDichVu> getDanhSachLoai() throws SQLException { return loaiDAO.getAll(); }
    public DichVu getById(int maDV) throws SQLException { return dao.getById(maDV); }
    public List<DichVu> searchByName(String tuKhoa) throws SQLException {
        String ten = tuKhoa.trim();
        return ten.isEmpty() ? dao.getAll() : dao.searchByName(ten);
    }

    public boolean insert(DichVu dv) throws SQLException {
        return dao.insert(chuanHoa(dv));
    }

    public boolean update(DichVu dv) throws SQLException {
        return dao.update(chuanHoa(dv));
    }

    public boolean delete(int maDV) throws SQLException { return dao.delete(maDV); }
    public boolean restore(int maDV) throws SQLException { return dao.restore(maDV); }

    // Kiem tra lai tai diem ghi; dung cung quy tac voi du lieu nhap.
    private DichVu chuanHoa(DichVu dv) {
        java.util.Objects.requireNonNull(dv, "Dich vu khong duoc null.");
        DichVu hopLe = taoDuLieu(new LoaiDichVu(dv.getMaLoaiDV(), ""),
                dv.getTenDV(), dv.getDonGia() == null ? "" : dv.getDonGia().toPlainString(),
                dv.getDonViTinh(), dv.getMoTa(), dv.isTrangThai());
        hopLe.setMaDV(dv.getMaDV());
        return hopLe;
    }
public DichVu taoDuLieu(LoaiDichVu loai, String ten, String gia, String donVi, String moTa, boolean trangThai) {

    ten = ten.trim();
    gia = gia.trim();
    donVi = donVi.trim();
    moTa = moTa.trim();

    if (loai == null) {
        throw new IllegalArgumentException(
                "Hay chon loai dich vu."
        );
    }

    if (ten.isEmpty() || gia.isEmpty() || donVi.isEmpty()) {
        throw new IllegalArgumentException(
                "Hay nhap day du ten dich vu, don gia "
                + "va don vi tinh."
        );
    }

    BigDecimal donGia;

    try {
        donGia = new BigDecimal(gia);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
                "Don gia phai la so hop le, vi du: 50000"
        );
    }

    if (donGia.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException(
                "Don gia khong duoc am."
        );
    }

    donGia = donGia.setScale(2, RoundingMode.HALF_UP);

    return new DichVu(
            loai.getMaLoaiDV(),
            ten,
            donGia,
            donVi,
            moTa,
            trangThai
    );
}



}
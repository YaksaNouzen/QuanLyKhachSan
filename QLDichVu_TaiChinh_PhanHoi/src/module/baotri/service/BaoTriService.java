package module.baotri.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import module.baotri.dao.BaoTriDAO;
import module.baotri.model.MucDoUuTien;
import module.baotri.model.PhieuBaoTri;
import module.baotri.model.TrangThaiBaoTri;
import module.phong.model.Phong;
import module.phong.model.TrangThaiPhong;

public class BaoTriService {
    private static final ZoneId HOTEL_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final BigDecimal MAX_COST = new BigDecimal("9999999999999999.99");
    private final BaoTriDAO dao;

    public BaoTriService(BaoTriDAO dao) {
        this.dao = dao;
    }

    public List<PhieuBaoTri> danhSach() {
        return dao.giaoDich(s -> s.phieuBaoTris().stream()
                .sorted(Comparator.comparing(PhieuBaoTri::ngayBao).reversed())
                .toList());
    }

    public void baoHong(String maPhong, String noiDung, String nguoiBao, MucDoUuTien mucDo) {
        dao.giaoDich(s -> {
            text(noiDung, 500, "Noi dung bao hong");
            text(nguoiBao, 100, "Nguoi bao");
            require(mucDo != null, "Chon muc do uu tien.");
            Phong phong = s.phong().phong(maPhong);
            LocalDateTime now = LocalDateTime.now(HOTEL_ZONE);
            String ma = "BT" + UUID.randomUUID().toString().replace("-", "");
            s.luu(new PhieuBaoTri(ma, phong.maPhong(), noiDung.trim(), mucDo,
                    nguoiBao.trim(), now, null, TrangThaiBaoTri.BAO_HONG,
                    null, null, BigDecimal.ZERO, ""), false);
            return null;
        });
    }

    public void lapPhieuVaBatDauSua(String maBaoTri, String nguoiPhuTrach) {
        dao.giaoDich(s -> {
            PhieuBaoTri banDau = s.phieu(maBaoTri);
            Phong phong = s.phong().phong(banDau.maPhong());
            PhieuBaoTri phieu = s.phieu(maBaoTri);
            text(nguoiPhuTrach, 100, "Nguoi phu trach");
            require(phieu.trangThai() == TrangThaiBaoTri.BAO_HONG,
                    "Phieu khong con o trang thai bao hong.");
            require(!phong.ngungSuDung() && phong.trangThai() == TrangThaiPhong.TRONG,
                    "Chi sua khi phong dang trong va dang hoat dong.");
            require(!s.phong().phongDangCoKhach(phong.maPhong()),
                    "Khong the sua khi khach dang o trong phong.");
            require(!s.phong().phongConDonGiu(phong.maPhong()),
                    "Phong co booking chua ket thuc.");
            require(s.phieuBaoTris().stream().noneMatch(other ->
                            !other.maBaoTri().equals(maBaoTri)
                                    && other.maPhong().equals(phong.maPhong())
                                    && other.trangThai() == TrangThaiBaoTri.DANG_SUA),
                    "Phong dang co phieu sua chua khac.");
            LocalDateTime now = LocalDateTime.now(HOTEL_ZONE);
            s.phong().capNhatTrangThai(phong.maPhong(), TrangThaiPhong.TRONG,
                    TrangThaiPhong.BAO_TRI);
            s.luu(new PhieuBaoTri(phieu.maBaoTri(), phieu.maPhong(), phieu.noiDung(),
                    phieu.mucDo(), phieu.nguoiBao(), phieu.ngayBao(), nguoiPhuTrach.trim(),
                    TrangThaiBaoTri.DANG_SUA, now, null, phieu.chiPhi(), phieu.ketQua()), true);
            return null;
        });
    }

    public void hoanTatSua(String maBaoTri, BigDecimal chiPhi, String ketQua) {
        dao.giaoDich(s -> {
            PhieuBaoTri banDau = s.phieu(maBaoTri);
            Phong phong = s.phong().phong(banDau.maPhong());
            PhieuBaoTri phieu = s.phieu(maBaoTri);
            tien(chiPhi);
            text(ketQua, 500, "Ket qua sua");
            require(phieu.trangThai() == TrangThaiBaoTri.DANG_SUA,
                    "Phieu sua chua chua duoc bat dau.");
            require(phong.trangThai() == TrangThaiPhong.BAO_TRI,
                    "Trang thai phong da thay doi.");
            require(!s.phong().phongDangCoKhach(phong.maPhong())
                            && !s.phong().phongConDonGiu(phong.maPhong()),
                    "Phong dang co khach hoac booking con hieu luc.");
            LocalDateTime now = LocalDateTime.now(HOTEL_ZONE);
            s.phong().capNhatTrangThai(phong.maPhong(), TrangThaiPhong.BAO_TRI,
                    TrangThaiPhong.TRONG);
            s.luu(new PhieuBaoTri(phieu.maBaoTri(), phieu.maPhong(), phieu.noiDung(),
                    phieu.mucDo(), phieu.nguoiBao(), phieu.ngayBao(), phieu.nguoiPhuTrach(),
                    TrangThaiBaoTri.HOAN_TAT, phieu.batDau(), now, chiPhi, ketQua.trim()), true);
            return null;
        });
    }

    public void huyBaoHong(String maBaoTri) {
        dao.giaoDich(s -> {
            PhieuBaoTri banDau = s.phieu(maBaoTri);
            s.phong().phong(banDau.maPhong());
            PhieuBaoTri phieu = s.phieu(maBaoTri);
            require(phieu.trangThai() == TrangThaiBaoTri.BAO_HONG,
                    "Chi huy phieu chua bat dau sua.");
            s.luu(new PhieuBaoTri(phieu.maBaoTri(), phieu.maPhong(), phieu.noiDung(),
                    phieu.mucDo(), phieu.nguoiBao(), phieu.ngayBao(), phieu.nguoiPhuTrach(),
                    TrangThaiBaoTri.HUY, phieu.batDau(), null,
                    phieu.chiPhi(), phieu.ketQua()), true);
            return null;
        });
    }

    private static void tien(BigDecimal value) {
        require(value != null && value.signum() >= 0 && value.compareTo(MAX_COST) <= 0
                        && value.stripTrailingZeros().scale() <= 2,
                "Chi phi khong am va toi da 2 so thap phan.");
    }

    private static void text(String value, int max, String field) {
        require(value != null && !value.isBlank() && value.trim().length() <= max,
                field + " bat buoc, toi da " + max + " ky tu.");
    }

    private static void require(boolean ok, String message) {
        if (!ok) throw new IllegalArgumentException(message);
    }
}

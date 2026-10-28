package module.buongphong.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import module.buongphong.dao.VeSinhDAO;
import module.buongphong.model.CongViecVeSinh;
import module.buongphong.model.TrangThaiVeSinh;
import module.phong.model.Phong;
import module.phong.model.TrangThaiPhong;

public class BuongPhongService {
    private static final ZoneId HOTEL_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final VeSinhDAO dao;

    public BuongPhongService(VeSinhDAO dao) {
        this.dao = dao;
    }

    public List<CongViecVeSinh> danhSach() {
        return dao.giaoDich(s -> s.congViecs().stream()
                .sorted(Comparator.comparing(CongViecVeSinh::ngayPhanCong).reversed())
                .toList());
    }

    public void phanCong(String maPhong, String noiDung, String nguoiPhuTrach) {
        dao.giaoDich(s -> {
            text(noiDung, 500, "Noi dung");
            text(nguoiPhuTrach, 100, "Nguoi phu trach");
            Phong phong = s.phong().phong(maPhong);
            require(!phong.ngungSuDung(), "Phong da ngung su dung.");
            require(phong.trangThai() == TrangThaiPhong.TRONG
                            || phong.trangThai() == TrangThaiPhong.DANG_DON,
                    "Chi phan cong don phong trong hoac dang don.");
            require(s.congViecs().stream().noneMatch(job ->
                            job.maPhong().equals(maPhong) && job.dangMo()),
                    "Phong da co viec don chua dong.");
            LocalDateTime now = LocalDateTime.now(HOTEL_ZONE);
            String ma = "VS" + UUID.randomUUID().toString().replace("-", "");
            s.luu(new CongViecVeSinh(ma, maPhong, noiDung.trim(), nguoiPhuTrach.trim(),
                    TrangThaiVeSinh.CHO_DON, now, null, null, null, null), false);
            return null;
        });
    }

    public void batDauDon(String maVeSinh) {
        dao.giaoDich(s -> {
            CongViecVeSinh banDau = s.congViec(maVeSinh);
            Phong phong = s.phong().phong(banDau.maPhong());
            CongViecVeSinh job = s.congViec(maVeSinh);
            require(job.trangThai() == TrangThaiVeSinh.CHO_DON,
                    "Cong viec khong con o trang thai cho don.");
            require(!phong.ngungSuDung(), "Phong da ngung su dung.");
            require(!s.phong().phongDangCoKhach(job.maPhong()),
                    "Khong the don khi khach dang o trong phong.");
            require(phong.trangThai() == TrangThaiPhong.TRONG
                            || phong.trangThai() == TrangThaiPhong.DANG_DON,
                    "Phong dang bao tri hoac khong san sang.");
            if (phong.trangThai() == TrangThaiPhong.TRONG)
                s.phong().capNhatTrangThai(job.maPhong(), TrangThaiPhong.TRONG,
                        TrangThaiPhong.DANG_DON);
            s.luu(new CongViecVeSinh(job.maVeSinh(), job.maPhong(), job.noiDung(),
                    job.nguoiPhuTrach(), TrangThaiVeSinh.DANG_DON,
                    job.ngayPhanCong(), LocalDateTime.now(HOTEL_ZONE), null,
                    job.nguoiXacNhan(), job.xacNhanLuc()), true);
            return null;
        });
    }

    public void xacNhanHoanThanh(String maVeSinh, String nguoiXacNhan) {
        dao.giaoDich(s -> {
            CongViecVeSinh banDau = s.congViec(maVeSinh);
            Phong phong = s.phong().phong(banDau.maPhong());
            CongViecVeSinh job = s.congViec(maVeSinh);
            text(nguoiXacNhan, 100, "Nguoi xac nhan");
            require(job.trangThai() == TrangThaiVeSinh.DANG_DON,
                    "Cong viec chua bat dau hoac da dong.");
            require(phong.trangThai() == TrangThaiPhong.DANG_DON,
                    "Trang thai phong da thay doi.");
            require(!s.phong().phongDangCoKhach(job.maPhong()),
                    "Phong dang co khach.");
            LocalDateTime now = LocalDateTime.now(HOTEL_ZONE);
            s.phong().capNhatTrangThai(job.maPhong(), TrangThaiPhong.DANG_DON,
                    TrangThaiPhong.TRONG);
            s.luu(new CongViecVeSinh(job.maVeSinh(), job.maPhong(), job.noiDung(),
                    job.nguoiPhuTrach(), TrangThaiVeSinh.HOAN_THANH,
                    job.ngayPhanCong(), job.batDau(), now, nguoiXacNhan.trim(), now), true);
            return null;
        });
    }

    public void huyPhanCong(String maVeSinh) {
        dao.giaoDich(s -> {
            CongViecVeSinh banDau = s.congViec(maVeSinh);
            s.phong().phong(banDau.maPhong());
            CongViecVeSinh job = s.congViec(maVeSinh);
            require(job.trangThai() == TrangThaiVeSinh.CHO_DON,
                    "Chi huy viec dang cho don.");
            s.luu(new CongViecVeSinh(job.maVeSinh(), job.maPhong(), job.noiDung(),
                    job.nguoiPhuTrach(), TrangThaiVeSinh.HUY,
                    job.ngayPhanCong(), job.batDau(), null, null, null), true);
            return null;
        });
    }

    private static void text(String value, int max, String field) {
        require(value != null && !value.isBlank() && value.trim().length() <= max,
                field + " bat buoc, toi da " + max + " ky tu.");
    }

    private static void require(boolean ok, String message) {
        if (!ok) throw new IllegalArgumentException(message);
    }
}

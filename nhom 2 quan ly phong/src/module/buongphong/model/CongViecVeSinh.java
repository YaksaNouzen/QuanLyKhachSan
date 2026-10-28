package module.buongphong.model;

import java.time.LocalDateTime;

public record CongViecVeSinh(
        String maVeSinh,
        String maPhong,
        String noiDung,
        String nguoiPhuTrach,
        TrangThaiVeSinh trangThai,
        LocalDateTime ngayPhanCong,
        LocalDateTime batDau,
        LocalDateTime ketThuc,
        String nguoiXacNhan,
        LocalDateTime xacNhanLuc) {
    public boolean dangMo() {
        return trangThai == TrangThaiVeSinh.CHO_DON
                || trangThai == TrangThaiVeSinh.DANG_DON;
    }
}

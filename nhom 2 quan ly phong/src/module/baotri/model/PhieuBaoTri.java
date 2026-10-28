package module.baotri.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PhieuBaoTri(
        String maBaoTri,
        String maPhong,
        String noiDung,
        MucDoUuTien mucDo,
        String nguoiBao,
        LocalDateTime ngayBao,
        String nguoiPhuTrach,
        TrangThaiBaoTri trangThai,
        LocalDateTime batDau,
        LocalDateTime ketThuc,
        BigDecimal chiPhi,
        String ketQua) {
    public boolean dangMo() {
        return trangThai == TrangThaiBaoTri.BAO_HONG
                || trangThai == TrangThaiBaoTri.DANG_SUA;
    }
}

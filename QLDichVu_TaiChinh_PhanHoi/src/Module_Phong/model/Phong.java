package Module_Phong.model;

import java.math.BigDecimal;

public record Phong(
        String maPhong,
        String soPhong,
        String maLoaiPhong,
        String maTang,
        BigDecimal giaPhong,
        TrangThaiPhong trangThai,
        boolean ngungSuDung) {}

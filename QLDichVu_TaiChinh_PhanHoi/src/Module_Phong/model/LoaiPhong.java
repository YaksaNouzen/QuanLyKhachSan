package Module_Phong.model;

import java.math.BigDecimal;

public record LoaiPhong(
        String maLoaiPhong,
        String tenLoai,
        int soNguoiToiDa,
        BigDecimal giaTheoNgay) {}

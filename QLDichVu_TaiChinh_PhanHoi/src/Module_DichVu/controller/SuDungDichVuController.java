package Module_DichVu.controller;

import Module_DichVu.service.SuDungDichVuService;
import java.sql.SQLException;

public class SuDungDichVuController {
    private final SuDungDichVuService service = new SuDungDichVuService();

    public String ghiNhan(String maDatPhong, int maDichVu, int soLuong)
            throws SQLException {
        return service.ghiNhan(maDatPhong, maDichVu, soLuong);
    }
}

package Module_NhanTraPhong.controller;

import java.sql.SQLException;
import java.util.List;
import Module_NhanTraPhong.model.LuuTru;
import Module_NhanTraPhong.service.LuuTruService;

public class LuuTruController {
    private final LuuTruService service = new LuuTruService();

    public List<LuuTru> timLuuTru(String tuKhoa, String trangThai)
            throws SQLException {
        return service.timLuuTru(tuKhoa, trangThai);
    }

    public void nhanPhong(String maDatPhong) throws SQLException {
        service.nhanPhong(maDatPhong);
    }

    public void traPhong(String maDatPhong) throws SQLException {
        service.traPhong(maDatPhong);
    }
}

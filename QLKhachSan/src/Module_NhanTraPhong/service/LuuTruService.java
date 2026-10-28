package Module_NhanTraPhong.service;

import Module_NhanTraPhong.dao.LuuTruDAO;
import java.sql.SQLException;
import java.util.List;
import Module_NhanTraPhong.model.LuuTru;

public class LuuTruService {
    private final LuuTruDAO dao = new LuuTruDAO();

    public List<LuuTru> timLuuTru(String tuKhoa, String trangThai)
            throws SQLException {
        String status = trangThai == null ? "" : trangThai.trim();
        if (!status.isEmpty()
                && !"GIU_PHONG".equals(status)
                && !"DANG_O".equals(status)
                && !"DA_TRA".equals(status)
                && !"QUA_HAN_TRA".equals(status)) {
            throw new IllegalArgumentException("Bộ lọc trạng thái lưu trú không hợp lệ.");
        }
        return dao.timLuuTru(tuKhoa, status);
    }

    public void nhanPhong(String maDatPhong) throws SQLException {
        dao.nhanPhong(requiredId(maDatPhong));
    }

    public void traPhong(String maDatPhong) throws SQLException {
        dao.traPhong(requiredId(maDatPhong));
    }

    private String requiredId(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().length() > 20) {
            throw new IllegalArgumentException("Mã đặt phòng không hợp lệ.");
        }
        return value.trim();
    }
}

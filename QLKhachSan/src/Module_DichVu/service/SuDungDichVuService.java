package Module_DichVu.service;

import Module_DichVu.dao.SuDungDichVuDAO;
import java.sql.SQLException;

public class SuDungDichVuService {
    private final SuDungDichVuDAO dao = new SuDungDichVuDAO();

    public String ghiNhan(String maDatPhong, int maDichVu, int soLuong)
            throws SQLException {
        if (maDatPhong == null || maDatPhong.isBlank()
                || maDatPhong.trim().length() > 20) {
            throw new IllegalArgumentException("Mã đặt phòng không hợp lệ.");
        }
        if (maDichVu <= 0) throw new IllegalArgumentException("Mã dịch vụ không hợp lệ.");
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        return dao.ghiNhan(maDatPhong.trim(), maDichVu, soLuong);
    }
}

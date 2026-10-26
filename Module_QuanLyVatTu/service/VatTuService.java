package Module_QuanLyVatTu.service;

import Module_QuanLyVatTu.Dao.VatTuDAO;
import Module_QuanLyVatTu.model.VatTu;
import java.sql.SQLException;
import java.util.List;

public class VatTuService {
    private final VatTuDAO dao = new VatTuDAO();

    public List<VatTu> timKiem(String tuKhoa, String trangThai,
                               boolean sapHet) throws SQLException {
        return dao.timKiem(tuKhoa, trangThai, sapHet);
    }

    public List<VatTu> layDangHoatDong() throws SQLException {
        return dao.layVatTuDangSuDung();
    }

    public void them(VatTu vatTu) throws SQLException {
        kiemTra(vatTu);
        if (dao.timTheoMa(vatTu.getMaVatTu()) != null) {
            throw new IllegalArgumentException("Mã vật tư đã tồn tại.");
        }
        dao.them(vatTu);
    }

    public void capNhat(VatTu vatTu) throws SQLException {
        kiemTra(vatTu);
        if (!dao.capNhat(vatTu)) {
            throw new IllegalArgumentException("Không tìm thấy vật tư cần sửa.");
        }
    }

    public void capNhatTrangThai(String ma, String trangThai)
            throws SQLException {
        batBuoc(ma, "Hãy chọn vật tư cần đổi trạng thái.");
        if (!"Đang sử dụng".equals(trangThai)
                && !"Ngừng sử dụng".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái vật tư không hợp lệ.");
        }
        if (!dao.capNhatTrangThai(ma, trangThai)) {
            throw new IllegalArgumentException("Không tìm thấy vật tư.");
        }
    }

    public void xuatKho(String ma, int soLuong) throws SQLException {
        batBuoc(ma, "Hãy chọn vật tư cần xuất.");
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0.");
        }
        if (!dao.xuatKho(ma, soLuong)) {
            throw new IllegalArgumentException(
                    "Không thể xuất kho: vật tư ngừng sử dụng hoặc tồn kho không đủ.");
        }
    }

    private void kiemTra(VatTu vatTu) {
        batBuoc(vatTu.getMaVatTu(), "Mã vật tư không được để trống.");
        batBuoc(vatTu.getTenVatTu(), "Tên vật tư không được để trống.");
        batBuoc(vatTu.getDonViTinh(), "Đơn vị tính không được để trống.");
        if (vatTu.getMucTonToiThieu() < 0) {
            throw new IllegalArgumentException("Mức tồn tối thiểu không được âm.");
        }
    }

    private void batBuoc(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}

package Module_NhapHang.service;

import Module_NhapHang.dao.PhieuNhapDAO;
import Module_NhapHang.model.ChiTietPhieuNhap;
import Module_NhapHang.model.PhieuNhap;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhieuNhapService {
    private final PhieuNhapDAO dao = new PhieuNhapDAO();

    public List<PhieuNhap> timKiem(String tuKhoa, String trangThai,
                                  LocalDate tuNgay, LocalDate denNgay)
            throws SQLException {
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Từ ngày không được sau đến ngày.");
        }
        return dao.timKiem(tuKhoa, trangThai, tuNgay, denNgay);
    }

    public PhieuNhap timTheoMa(String ma) throws SQLException {
        return dao.timTheoMa(ma);
    }

    public void luu(PhieuNhap phieu) throws SQLException {
        kiemTra(phieu);
        phieu.tinhTongTien();
        dao.luuPhieuNhap(phieu);
    }

    public void xacNhan(String ma) throws SQLException {
        if (ma == null || ma.isBlank()) {
            throw new IllegalArgumentException("Hãy chọn phiếu nhập cần xác nhận.");
        }
        dao.xacNhan(ma);
    }

    public void huy(String ma) throws SQLException {
        if (ma == null || ma.isBlank() || !dao.huyPhieu(ma)) {
            throw new IllegalArgumentException(
                    "Chỉ có thể hủy phiếu nhập đang ở trạng thái Nháp.");
        }
    }

    private void kiemTra(PhieuNhap phieu) {
        if (phieu.getMaPhieuNhap() == null || phieu.getMaPhieuNhap().isBlank()) {
            throw new IllegalArgumentException("Mã phiếu nhập không được để trống.");
        }
        if (phieu.getMaNhaCungCap() == null || phieu.getMaNhaCungCap().isBlank()) {
            throw new IllegalArgumentException("Hãy chọn nhà cung cấp.");
        }
        if (phieu.getNgayNhap() == null) {
            throw new IllegalArgumentException("Ngày nhập không hợp lệ.");
        }
        if (phieu.getChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một vật tư.");
        }

        Set<String> maDaCo = new HashSet<>();
        for (ChiTietPhieuNhap dong : phieu.getChiTiet()) {
            if (!maDaCo.add(dong.getMaVatTu())) {
                throw new IllegalArgumentException(
                        "Vật tư " + dong.getMaVatTu() + " bị trùng trong phiếu.");
            }
            if (dong.getSoLuong() <= 0) {
                throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
            }
            if (dong.getDonGia() == null
                    || dong.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Đơn giá không được âm.");
            }
        }
    }
}

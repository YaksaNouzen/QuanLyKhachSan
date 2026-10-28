package Module_KhachHang.service;

import Module_KhachHang.dao.KhachHangDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import Module_DatPhong.model.DatPhong;
import Module_KhachHang.model.KhachHang;

public class KhachHangService {
    private static final Pattern EMAIL = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final KhachHangDAO dao = new KhachHangDAO();

    public List<KhachHang> timKhachHang(String tuKhoa, String trangThai)
            throws SQLException {
        return dao.timKhachHang(tuKhoa, trangThai);
    }

    public String themKhachHang(KhachHang khachHang) throws SQLException {
        chuanHoaVaKiemTra(khachHang);
        return dao.themKhachHang(khachHang);
    }

    public void capNhatKhachHang(KhachHang khachHang) throws SQLException {
        if (khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().isBlank()) {
            throw new IllegalArgumentException("Thiếu mã khách hàng.");
        }
        chuanHoaVaKiemTra(khachHang);
        dao.capNhatKhachHang(khachHang);
    }

    public void ngungHoatDong(String maKhachHang) throws SQLException {
        dao.ngungHoatDong(requiredId(maKhachHang));
    }

    public void kichHoatLai(String maKhachHang) throws SQLException {
        dao.kichHoatLai(requiredId(maKhachHang));
    }

    public List<DatPhong> layLichSu(String maKhachHang) throws SQLException {
        if (maKhachHang == null || maKhachHang.isBlank()) {
            throw new IllegalArgumentException("Thiếu mã khách hàng.");
        }
        return dao.layLichSu(maKhachHang.trim());
    }

    private void chuanHoaVaKiemTra(KhachHang item) {
        if (item == null) throw new IllegalArgumentException("Thiếu dữ liệu khách hàng.");
        item.setHoTen(trim(item.getHoTen()));
        item.setLoaiGiayTo(emptyToNull(item.getLoaiGiayTo()));
        item.setSoGiayTo(emptyToNull(item.getSoGiayTo()));
        item.setQuocGiaCap(upperOrNull(item.getQuocGiaCap()));
        item.setSoDienThoai(emptyToNull(item.getSoDienThoai()));
        item.setEmail(emptyToNull(item.getEmail()));
        item.setGioiTinh(emptyToDefault(item.getGioiTinh(), "KHONG_CUNG_CAP"));
        item.setQuocTich(upperOrNull(item.getQuocTich()));
        item.setDiaChi(emptyToNull(item.getDiaChi()));

        requireLength(item.getHoTen(), "Họ tên", 1, 100);
        optionalLength(item.getLoaiGiayTo(), "Loại giấy tờ", 10);
        optionalLength(item.getSoGiayTo(), "Số giấy tờ", 30);
        optionalLength(item.getSoDienThoai(), "Số điện thoại", 20);
        optionalLength(item.getEmail(), "Email", 254);
        optionalLength(item.getDiaChi(), "Địa chỉ", 255);
        countryCode(item.getQuocGiaCap(), "Quốc gia cấp");
        countryCode(item.getQuocTich(), "Quốc tịch");
        if (item.getNgaySinh() != null && item.getNgaySinh().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không được ở tương lai.");
        }
        if (item.getEmail() != null && !EMAIL.matcher(item.getEmail()).matches()) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        boolean hasDocumentType = item.getLoaiGiayTo() != null;
        boolean hasDocumentNumber = item.getSoGiayTo() != null;
        boolean hasCountry = item.getQuocGiaCap() != null;
        if ((hasDocumentType || hasDocumentNumber || hasCountry)
                && !(hasDocumentType && hasDocumentNumber && hasCountry)) {
            throw new IllegalArgumentException(
                    "Loại, số và quốc gia cấp giấy tờ phải được nhập cùng nhau.");
        }
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }
    private String emptyToNull(String value) {
        String normalized = trim(value);
        return normalized.isEmpty() ? null : normalized;
    }
    private String upperOrNull(String value) {
        String normalized = emptyToNull(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
    private String emptyToDefault(String value, String defaultValue) {
        String normalized = trim(value);
        return normalized.isEmpty() ? defaultValue : normalized;
    }
    private void requireLength(String value, String label, int min, int max) {
        if (value == null || value.length() < min || value.length() > max) {
            throw new IllegalArgumentException(label + " phải có từ " + min + " đến " + max + " ký tự.");
        }
    }
    private void optionalLength(String value, String label, int max) {
        if (value != null && value.length() > max) {
            throw new IllegalArgumentException(label + " tối đa " + max + " ký tự.");
        }
    }
    private void countryCode(String value, String label) {
        if (value != null && value.length() != 2) {
            throw new IllegalArgumentException(label + " phải là mã quốc gia gồm 2 ký tự.");
        }
    }

    private String requiredId(String value) {
        if (value == null || value.isBlank() || value.trim().length() > 20) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
        }
        return value.trim();
    }
}

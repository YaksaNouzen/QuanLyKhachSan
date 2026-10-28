package Module_NhaCungCap.service;

import Module_NhaCungCap.dao.NhaCungCapDAO;
import Module_NhaCungCap.model.NhaCungCap;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class NhaCungCapService {
    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final NhaCungCapDAO dao = new NhaCungCapDAO();

    public List<NhaCungCap> timKiem(String tuKhoa, String trangThai)
            throws SQLException {
        return dao.timKiem(tuKhoa, trangThai);
    }

    public List<NhaCungCap> layDangHoatDong() throws SQLException {
        return dao.layDangHoatDong();
    }

    public void them(NhaCungCap ncc) throws SQLException {
        kiemTra(ncc);
        if (dao.timTheoMa(ncc.getMaNhaCungCap()) != null) {
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại.");
        }
        dao.them(ncc);
    }

    public void capNhat(NhaCungCap ncc) throws SQLException {
        kiemTra(ncc);
        if (!dao.capNhat(ncc)) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp.");
        }
    }

    public void capNhatTrangThai(String ma, String trangThai)
            throws SQLException {
        if (!"Đang hoạt động".equals(trangThai)
                && !"Ngừng hoạt động".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái nhà cung cấp không hợp lệ.");
        }
        if (ma == null || ma.isBlank()
                || !dao.capNhatTrangThai(ma, trangThai)) {
            throw new IllegalArgumentException("Hãy chọn nhà cung cấp hợp lệ.");
        }
    }

    public List<Object[]> layLichSuNhap(String ma) throws SQLException {
        return dao.layLichSuNhap(ma);
    }

    public List<Object[]> layVatTuDaCungCap(String ma) throws SQLException {
        return dao.layVatTuDaCungCap(ma);
    }

    private void kiemTra(NhaCungCap ncc) {
        if (ncc.getMaNhaCungCap() == null || ncc.getMaNhaCungCap().isBlank()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống.");
        }
        if (ncc.getTenNhaCungCap() == null || ncc.getTenNhaCungCap().isBlank()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống.");
        }
        if (ncc.getEmail() != null && !ncc.getEmail().isBlank()
                && !EMAIL.matcher(ncc.getEmail()).matches()) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
    }
}

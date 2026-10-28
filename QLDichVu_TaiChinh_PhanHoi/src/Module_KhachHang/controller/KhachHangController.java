package Module_KhachHang.controller;

import java.sql.SQLException;
import java.util.List;
import Module_DatPhong.model.DatPhong;
import Module_KhachHang.model.KhachHang;
import Module_KhachHang.service.KhachHangService;

public class KhachHangController {
    private final KhachHangService service = new KhachHangService();

    public List<KhachHang> timKhachHang(String tuKhoa, String trangThai)
            throws SQLException {
        return service.timKhachHang(tuKhoa, trangThai);
    }

    public String themKhachHang(KhachHang khachHang) throws SQLException {
        return service.themKhachHang(khachHang);
    }

    public void capNhatKhachHang(KhachHang khachHang) throws SQLException {
        service.capNhatKhachHang(khachHang);
    }

    public void ngungHoatDong(String maKhachHang) throws SQLException {
        service.ngungHoatDong(maKhachHang);
    }

    public void kichHoatLai(String maKhachHang) throws SQLException {
        service.kichHoatLai(maKhachHang);
    }

    public List<DatPhong> layLichSu(String maKhachHang) throws SQLException {
        return service.layLichSu(maKhachHang);
    }
}

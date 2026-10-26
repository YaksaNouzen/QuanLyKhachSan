package Module_QuanLyVatTu.controller;

import Module_QuanLyVatTu.model.VatTu;
import Module_QuanLyVatTu.service.VatTuService;
import java.sql.SQLException;
import java.util.List;

public class VatTuController {
    private final VatTuService service = new VatTuService();
    public List<VatTu> timKiem(String q, String status, boolean low) throws SQLException { return service.timKiem(q, status, low); }
    public List<VatTu> layDangHoatDong() throws SQLException { return service.layDangHoatDong(); }
    public void them(VatTu value) throws SQLException { service.them(value); }
    public void capNhat(VatTu value) throws SQLException { service.capNhat(value); }
    public void capNhatTrangThai(String ma, String trangThai) throws SQLException { service.capNhatTrangThai(ma, trangThai); }
    public void xuatKho(String ma, int soLuong) throws SQLException { service.xuatKho(ma, soLuong); }
}

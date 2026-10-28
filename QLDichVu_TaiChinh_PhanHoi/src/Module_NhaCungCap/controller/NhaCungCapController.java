package Module_NhaCungCap.controller;

import Module_NhaCungCap.model.NhaCungCap;
import Module_NhaCungCap.service.NhaCungCapService;
import java.sql.SQLException;
import java.util.List;

public class NhaCungCapController {
    private final NhaCungCapService service = new NhaCungCapService();
    public List<NhaCungCap> timKiem(String q, String status) throws SQLException { return service.timKiem(q, status); }
    public List<NhaCungCap> layDangHoatDong() throws SQLException { return service.layDangHoatDong(); }
    public void them(NhaCungCap value) throws SQLException { service.them(value); }
    public void capNhat(NhaCungCap value) throws SQLException { service.capNhat(value); }
    public void capNhatTrangThai(String ma, String trangThai) throws SQLException { service.capNhatTrangThai(ma, trangThai); }
    public List<Object[]> lichSuNhap(String ma) throws SQLException { return service.layLichSuNhap(ma); }
    public List<Object[]> vatTuDaCungCap(String ma) throws SQLException { return service.layVatTuDaCungCap(ma); }
}

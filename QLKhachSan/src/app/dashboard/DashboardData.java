package app.dashboard;

import Module_TaiChinh.model.ThongKeTheoNgay;
import java.util.List;

/** Dữ liệu chỉ đọc cho trang chủ; không thay đổi nghiệp vụ của các module. */
public record DashboardData(
        String tenNhanVien,
        List<CaHomNay> caHomNay,
        List<CongViec> canLam,
        List<CongViec> canChuY,
        List<ThongKeTheoNgay> doanhThuBayNgay) {

    public record CaHomNay(String tenCa, String gioLam, String trangThai) { }

    public record CongViec(String tieuDe, String moTa, String maTrang) { }
}

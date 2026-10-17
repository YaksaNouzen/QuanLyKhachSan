import Module_TaiChinh.model.ThongKeTaiChinh;
import Module_TaiChinh.model.ThongKeTheoNgay;
import Module_TaiChinh.service.ThongKeTaiChinhService;
import java.util.List;

public class TestThongKeTaiChinh {

    public static void main(String[] args) {
        ThongKeTaiChinhService service
                = new ThongKeTaiChinhService();

        String tuNgay = "2026-10-01";
        String denNgay = "2026-10-31";

        ThongKeTaiChinh tongQuan
                = service.thongKeTongQuan(tuNgay, denNgay);

        System.out.println("===== THONG KE TONG QUAN =====");
        System.out.println("So hoa don: " + tongQuan.getSoHoaDon());
        System.out.println("Da thanh toan: "
                + tongQuan.getSoDaThanhToan());
        System.out.println("Chua thanh toan: "
                + tongQuan.getSoChuaThanhToan());
        System.out.println("Doanh thu: "
                + tongQuan.getTongDoanhThu());
        System.out.println("Thuc thu: "
                + tongQuan.getTongThucThu());
        System.out.println("Con no: "
                + tongQuan.getTongConNo());

        System.out.println("===== THONG KE THEO NGAY =====");
        List<ThongKeTheoNgay> danhSach
                = service.thongKeTheoNgay(tuNgay, denNgay);

        for (ThongKeTheoNgay dong : danhSach) {
            System.out.println(
                    dong.getNgayLap()
                    + " | " + dong.getSoHoaDon()
                    + " | " + dong.getDoanhThu()
                    + " | " + dong.getThucThu()
                    + " | " + dong.getConNo());
        }
    }
}

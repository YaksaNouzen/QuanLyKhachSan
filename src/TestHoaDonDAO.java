import Module_TaiChinh.Dao.HoaDonDAO;
import Module_TaiChinh.model.HoaDon;
import java.util.List;

public class TestHoaDonDAO {

    public static void main(String[] args) {

        HoaDonDAO dao = new HoaDonDAO();

        // Ví dụ 1: Hiển thị tất cả hóa đơn
        hienThi(dao.search("", "", "", "", ""));

        // Ví dụ 2: Tìm theo mã khách hàng
        hienThi(dao.search("", "", "KH001", "", ""));

        // Ví dụ 3: Tìm hóa đơn trong tháng 10/2026
        hienThi(dao.search(
                "", "", "",
                "2026-10-01",
                "2026-10-31"
        ));

        // Ví dụ 4: Kết hợp mã khách hàng và khoảng thời gian
        hienThi(dao.search(
                "", "", "KH001",
                "2026-10-01",
                "2026-10-31"
        ));
    }

    public static void hienThi(List<HoaDon> list) {

        System.out.println("===== KET QUA TIM KIEM =====");

        for (HoaDon hd : list) {
            System.out.println(
                    hd.getMaHoaDon()
                    + " | " + hd.getMaDatPhong()
                    + " | " + hd.getNgayLap()
                    + " | " + hd.getTongTien()
                    + " | " + hd.getTrangThai()
            );
        }

        System.out.println("Tong so: " + list.size());
    }
}
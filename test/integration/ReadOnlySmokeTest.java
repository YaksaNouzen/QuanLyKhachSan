package integration;

import module.datphong.controller.DatPhongController;
import module.khachhang.controller.KhachHangController;
import module.luutru.controller.LuuTruController;

/** Kiểm tra tích hợp chỉ đọc; không tạo hoặc thay đổi dữ liệu. */
public class ReadOnlySmokeTest {
    public static void main(String[] args) throws Exception {
        int customers = new KhachHangController().timKhachHang("", "").size();
        int bookings = new DatPhongController().layDanhSachDatPhong().size();
        int stays = new LuuTruController().timLuuTru("", "").size();
        System.out.println("READ_ONLY_SMOKE_PASS"
                + " customers=" + customers
                + " bookings=" + bookings
                + " stays=" + stays);
    }
}

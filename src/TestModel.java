import Module_TaiChinh.model.HoaDon;

public class TestModel {

    public static void main(String[] args) {

        HoaDon hd = new HoaDon(
                "HD999",
                "DP999",
                "2026-10-03",
                100000,
                2000000,
                "Chưa thanh toán"
        );
        
        System.out.println("Mã hóa đơn: " + hd.getMaHoaDon());
        System.out.println("Mã đặt phòng: " + hd.getMaDatPhong());
        System.out.println("Tổng tiền: " + hd.getTongTien());
        System.out.println("Trạng thái: " + hd.getTrangThai());
    }
}
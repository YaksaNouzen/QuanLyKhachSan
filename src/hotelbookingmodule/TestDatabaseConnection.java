package hotelbookingmodule;

import controller.DatPhongController;
import model.DatPhong;
import java.sql.SQLException;
import java.util.List;

public class TestDatabaseConnection {

    public static void main(String[] args) {
        try {
            DatPhongController controller =
                    new DatPhongController();

            List<DatPhong> danhSach =
                    controller.layDanhSachDatPhong();

            System.out.println("=== KIEM THU DAT PHONG CONTROLLER ===");
            System.out.println("Tong so: " + danhSach.size());

            for (DatPhong dp : danhSach) {
                System.out.println(
                    dp.getMaDatPhong()
                    + " | " + dp.getMaKhachHang()
                    + " | " + dp.getMaPhong()
                    + " | " + dp.getTrangThai()
                );
            }

        } catch (SQLException e) {
            System.out.println("LOI CONTROLLER!");
            System.out.println("Ma loi: " + e.getErrorCode());
            System.out.println("Thong bao: " + e.getMessage());
        }
    }
}
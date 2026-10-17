

import Module_TaiChinh.model.HoaDon;
import Module_TaiChinh.model.ChiTietHoaDon;
import Module_TaiChinh.service.HoaDonService;
import Module_TaiChinh.service.ChiTietHoaDonService;
import Module_TaiChinh.service.ThanhToanService;

public class TestTaiChinhService {

    public static void main(String[] args) {

        HoaDonService hoaDonService = new HoaDonService();
        ChiTietHoaDonService chiTietService =
            new ChiTietHoaDonService();
        ThanhToanService thanhToanService =
            new ThanhToanService();

        System.out.println("===== KIEM THU MODULE TAI CHINH =====");

        // TEST 1: Tim hoa don
        System.out.println("\n--- TEST 1: Tim hoa don HD001 ---");

        try {
            HoaDon hoaDon = hoaDonService.findById("HD001");

            if (hoaDon != null) {
                System.out.println("Tim thay hoa don: "
                    + hoaDon.getMaHoaDon());
                System.out.println("Ma dat phong: "
                    + hoaDon.getMaDatPhong());
                System.out.println("Tong tien trong database: "
                    + hoaDon.getTongTien());
            } else {
                System.out.println("Khong tim thay HD001.");
            }
        } catch (Exception e) {
            System.out.println("TEST 1 THAT BAI: "
                + e.getMessage());
        }

        // TEST 2: Tinh tong cac dong chi tiet
        System.out.println(
            "\n--- TEST 2: Tinh tong chi tiet HD001 ---"
        );

        try {
            double tongChiTiet =
                chiTietService.tinhTongTien("HD001");

            System.out.println("Tong chi tiet: "
                + tongChiTiet);

            System.out.println("Ket qua mong doi voi du lieu mau: "
                + "2860000");
        } catch (Exception e) {
            System.out.println("TEST 2 THAT BAI: "
                + e.getMessage());
        }

        // TEST 3: Tinh tong hoa don sau giam gia
        System.out.println(
            "\n--- TEST 3: Tinh tong hoa don HD001 ---"
        );

        try {
            double tongHoaDon =
                hoaDonService.tinhTongTien("HD001");

            System.out.println("Tong hoa don: "
                + tongHoaDon);

            System.out.println("Ket qua mong doi voi du lieu mau: "
                + "2760000");
        } catch (Exception e) {
            System.out.println("TEST 3 THAT BAI: "
                + e.getMessage());
        }

        // TEST 4: Kiem tra da thanh toan va con no
        System.out.println(
            "\n--- TEST 4: Kiem tra thanh toan HD001 ---"
        );

        try {
            double daThanhToan =
                thanhToanService.tinhTongDaThanhToan("HD001");

            double conLai =
                thanhToanService.tinhSoTienConLai("HD001");

            System.out.println("Da thanh toan thanh cong: "
                + daThanhToan);

            System.out.println("So tien con lai: "
                + conLai);
        } catch (Exception e) {
            System.out.println("TEST 4 THAT BAI: "
                + e.getMessage());
        }

        // TEST 5: Thu tinh thanh tien voi so luong khong hop le
        System.out.println(
            "\n--- TEST 5: Kiem tra so luong khong hop le ---"
        );

        try {
            ChiTietHoaDon chiTiet = new ChiTietHoaDon();

            chiTiet.setSoLuong(0);
            chiTiet.setDonGia(500000);

            chiTietService.tinhThanhTien(chiTiet);

            System.out.println(
                "LOI: Du lieu khong hop le nhung van duoc chap nhan."
            );
        } catch (IllegalArgumentException e) {
            System.out.println("DA CHAN DU LIEU SAI: "
                + e.getMessage());
        } catch (Exception e) {
            System.out.println("LOI KHAC: " + e.getMessage());
        }

        // TEST 6: Tim hoa don khong ton tai
        System.out.println(
            "\n--- TEST 6: Tim hoa don khong ton tai ---"
        );

        try {
            HoaDon hoaDon =
                hoaDonService.findById("HD999");

            if (hoaDon == null) {
                System.out.println(
                    "DUNG: Khong tim thay HD999."
                );
            } else {
                System.out.println(
                    "HD999 ton tai trong database."
                );
            }
        } catch (Exception e) {
            System.out.println("TEST 6 THAT BAI: "
                + e.getMessage());
        }

        System.out.println(
            "\n===== KET THUC KIEM THU ====="
        );
    }
}

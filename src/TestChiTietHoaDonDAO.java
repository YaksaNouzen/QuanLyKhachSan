import Module_TaiChinh.Dao.ChiTietHoaDonDAO;
import Module_TaiChinh.model.ChiTietHoaDon;
import java.util.List;

public class TestChiTietHoaDonDAO {

    public static void main(String[] args) {

        ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

        List<ChiTietHoaDon> list =
                dao.getByMaHoaDon("HD001");

        System.out.println("CHI TIET HOA DON HD001");

        for (ChiTietHoaDon ct : list) {

            System.out.println(
                    ct.getMaChiTiet()
                    + " | " + ct.getLoaiChiPhi()
                    + " | " + ct.getMoTa()
                    + " | " + ct.getSoLuong()
                    + " | " + ct.getDonGia()
                    + " | " + ct.getThanhTien()
            );
        }

        System.out.println("Tong so dong: " + list.size());
    }
}
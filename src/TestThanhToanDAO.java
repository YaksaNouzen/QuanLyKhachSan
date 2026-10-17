import Module_TaiChinh.Dao.ThanhToanDAO;
import Module_TaiChinh.model.ThanhToan;
import java.util.List;

public class TestThanhToanDAO {

    public static void main(String[] args) {

        ThanhToanDAO dao = new ThanhToanDAO();

        // Xem tất cả giao dịch
        List<ThanhToan> list = dao.getAll();

        for (ThanhToan tt : list) {

            System.out.println(
                    tt.getMaThanhToan()
                    + " | " + tt.getMaHoaDon()
                    + " | " + tt.getNgayThanhToan()
                    + " | " + tt.getPhuongThuc()
                    + " | " + tt.getSoTien()
                    + " | " + tt.getTrangThai()
            );
        }

        // Xem giao dịch của HD001
        System.out.println("\nGIAO DICH CUA HD001:");

        List<ThanhToan> listHD =
                dao.getByMaHoaDon("HD001");

        for (ThanhToan tt : listHD) {
            System.out.println(
                    tt.getMaThanhToan()
                    + " | " + tt.getPhuongThuc()
                    + " | " + tt.getSoTien()
            );
        }
    }
}
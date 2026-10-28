package QLDichVu;

import QLDichVu.controller.DichVuController;
import QLDichVu.dao.DichVuDAO;
import QLDichVu.dao.LoaiDichVuDAO;
import QLDichVu.model.DichVu;
import QLDichVu.model.LoaiDichVu;
import QLDichVu.service.DichVuService;
import QLDichVu.view.DichVuPanel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/** Kiem thu offline, khong ket noi va khong ghi vao SQL Server. */
public final class DichVuLayerTest {

    public static void main(String[] args) throws Exception {
        FakeDichVuDAO dichVuDAO = new FakeDichVuDAO();
        FakeLoaiDichVuDAO loaiDAO = new FakeLoaiDichVuDAO();
        DichVuService service = new DichVuService(dichVuDAO, loaiDAO);
        DichVuController controller = new DichVuController(service);

        kiemTra(controller.layDanhSachLoai().size() == 1,
                "Khong tai duoc loai dich vu qua Controller/Service.");

        kiemTra(controller.themDichVu(1, "  An sang  ",
                "80000.125", " Suat ", "  Buffet  ", true),
                "Them dich vu that bai.");
        kiemTra("An sang".equals(dichVuDAO.daThem.getTenDV()),
                "Service chua chuan hoa ten.");
        kiemTra(new BigDecimal("80000.13").equals(
                        dichVuDAO.daThem.getDonGia()),
                "Service chua lam tron DECIMAL(18,2).");

        dichVuDAO.duLieu.add(new DichVu(7, 1, "Minibar",
                new BigDecimal("50000.00"), "Lan", "", true));
        kiemTra(controller.capNhatDichVu(7, 1, "Minibar moi",
                "60000", "Lan", "", true),
                "Cap nhat dich vu that bai.");
        kiemTra(controller.ngungHoatDong(7),
                "Ngung hoat dong that bai.");
        dichVuDAO.duLieu.get(0).setTrangThai(false);
        kiemTra(controller.khoiPhuc(7), "Khoi phuc that bai.");

        kiemTra(controller.timTheoTen(" mini ").size() == 1,
                "Tim kiem khong di qua Controller/Service.");
        mongDoiLoi(() -> controller.themDichVu(0, "X",
                "100", "Lan", "", true), "loai");
        mongDoiLoi(() -> controller.themDichVu(1, "X",
                "-1", "Lan", "", true), "khong am");
        mongDoiLoi(() -> controller.themDichVu(1, "X",
                "abc", "Lan", "", true), "so hop le");
        mongDoiLoi(() -> controller.ngungHoatDong(999),
                "Khong tim thay");

        SwingUtilities.invokeAndWait(() -> {
            DichVuPanel panel = new DichVuPanel(controller);
            kiemTra(panel.getComponentCount() == 3,
                    "DichVuPanel khong khoi tao duoc qua Controller.");
        });

        System.out.println("DICH_VU_LAYER_TEST_OK");
    }

    private static void mongDoiLoi(Runnable thaoTac,
                                   String noiDung) {
        try {
            thaoTac.run();
            throw new AssertionError("Thieu loi: " + noiDung);
        } catch (IllegalArgumentException e) {
            kiemTra(e.getMessage().toLowerCase()
                            .contains(noiDung.toLowerCase()),
                    "Thong bao loi khong dung: " + e.getMessage());
        }
    }

    private static void kiemTra(boolean dieuKien,
                                String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static final class FakeDichVuDAO extends DichVuDAO {
        private final List<DichVu> duLieu = new ArrayList<>();
        private DichVu daThem;

        @Override
        public List<DichVu> getAll() {
            return new ArrayList<>(duLieu);
        }

        @Override
        public DichVu getById(int maDichVu) {
            return duLieu.stream()
                    .filter(dv -> dv.getMaDV() == maDichVu)
                    .findFirst().orElse(null);
        }

        @Override
        public List<DichVu> searchByName(String tuKhoa) {
            String key = tuKhoa.toLowerCase();
            List<DichVu> ketQua = new ArrayList<>();
            for (DichVu dichVu : duLieu) {
                if (dichVu.getTenDV().toLowerCase().contains(key)) {
                    ketQua.add(dichVu);
                }
            }
            return ketQua;
        }

        @Override
        public boolean insert(DichVu dichVu) {
            daThem = dichVu;
            return true;
        }

        @Override
        public boolean update(DichVu dichVu) {
            return true;
        }

        @Override
        public boolean delete(int maDichVu) {
            return true;
        }

        @Override
        public boolean restore(int maDichVu) {
            return true;
        }
    }

    private static final class FakeLoaiDichVuDAO
            extends LoaiDichVuDAO {
        @Override
        public List<LoaiDichVu> getAll() throws SQLException {
            return List.of(new LoaiDichVu(1, "An uong"));
        }
    }
}

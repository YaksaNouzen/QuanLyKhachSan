package Module_TaiChinh.service;

import Module_TaiChinh.dao.ThongKeTaiChinhDAO;
import Module_TaiChinh.model.ThongKeTaiChinh;
import Module_TaiChinh.model.ThongKeTheoNgay;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ThongKeTaiChinhService {

    private final ThongKeTaiChinhDAO thongKeTaiChinhDAO;

    public ThongKeTaiChinhService() {
        thongKeTaiChinhDAO = new ThongKeTaiChinhDAO();
    }

    public ThongKeTaiChinh thongKeTongQuan(
            String tuNgay, String denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        return thongKeTaiChinhDAO.thongKeTongQuan(
                chuanHoa(tuNgay), chuanHoa(denNgay));
    }

    public List<ThongKeTheoNgay> thongKeTheoNgay(
            String tuNgay, String denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        return thongKeTaiChinhDAO.thongKeTheoNgay(
                chuanHoa(tuNgay), chuanHoa(denNgay));
    }

    private void kiemTraKhoangNgay(
            String tuNgay, String denNgay) {
        LocalDate ngayBatDau = docNgay(tuNgay, "Tu ngay");
        LocalDate ngayKetThuc = docNgay(denNgay, "Den ngay");

        if (ngayBatDau != null
                && ngayKetThuc != null
                && ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException(
                    "Den ngay khong duoc truoc tu ngay.");
        }
    }

    private LocalDate docNgay(String giaTri, String tenTruong) {
        if (giaTri == null || giaTri.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(giaTri.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    tenTruong + " phai co dinh dang yyyy-MM-dd.");
        }
    }

    private String chuanHoa(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }
}

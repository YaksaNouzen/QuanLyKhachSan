package Module_TaiChinh.controller;

import Module_TaiChinh.model.ThongKeTaiChinh;
import Module_TaiChinh.model.ThongKeTheoNgay;
import Module_TaiChinh.service.ThongKeTaiChinhService;
import java.util.List;

public class ThongKeTaiChinhController {

    private final ThongKeTaiChinhService thongKeTaiChinhService;

    public ThongKeTaiChinhController() {
        thongKeTaiChinhService = new ThongKeTaiChinhService();
    }

    public ThongKeTaiChinh thongKeTongQuan(
            String tuNgay, String denNgay) {
        return thongKeTaiChinhService.thongKeTongQuan(
                tuNgay, denNgay);
    }

    public List<ThongKeTheoNgay> thongKeTheoNgay(
            String tuNgay, String denNgay) {
        return thongKeTaiChinhService.thongKeTheoNgay(
                tuNgay, denNgay);
    }
}

package integration;

import common.ui.HotelTheme;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import Module_BaoTri.controller.BaoTriController;
import Module_BaoTri.dao.MemoryBaoTriDAO;
import Module_BaoTri.service.BaoTriService;
import Module_BaoTri.view.BaoTriPanel;
import Module_BuongPhong.controller.BuongPhongController;
import Module_BuongPhong.dao.MemoryVeSinhDAO;
import Module_BuongPhong.service.BuongPhongService;
import Module_BuongPhong.view.BuongPhongPanel;
import Module_Phong.controller.PhongController;
import Module_Phong.dao.MemoryPhongDAO;
import Module_Phong.service.PhongService;
import Module_Phong.view.PhongPanel;

public final class Nhom2UiRenderTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        MemoryPhongDAO roomDAO = new MemoryPhongDAO();
        PhongService roomService = PhongService.demo(roomDAO);
        PhongController roomController = new PhongController(roomService);
        BuongPhongController housekeepingController = new BuongPhongController(
                new BuongPhongService(new MemoryVeSinhDAO(roomDAO)));
        BaoTriController maintenanceController = new BaoTriController(
                new BaoTriService(new MemoryBaoTriDAO(roomDAO)));

        SwingUtilities.invokeAndWait(() -> {
            HotelTheme.install();
            Component[] panels = {
                new PhongPanel(roomController),
                new BuongPhongPanel(housekeepingController, roomController),
                new BaoTriPanel(maintenanceController, roomController)
            };
            for (Component panel : panels) render(panel);
        });
        System.out.println("Nhom2UiRenderTest PASS");
    }

    private static void render(Component component) {
        component.setSize(1100, 700);
        component.doLayout();
        BufferedImage image = new BufferedImage(1100, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        component.paint(graphics);
        graphics.dispose();
    }
}

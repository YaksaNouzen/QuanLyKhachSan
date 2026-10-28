package integration;

import common.ui.HotelTheme;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import module.baotri.controller.BaoTriController;
import module.baotri.dao.MemoryBaoTriDAO;
import module.baotri.service.BaoTriService;
import module.baotri.view.BaoTriPanel;
import module.buongphong.controller.BuongPhongController;
import module.buongphong.dao.MemoryVeSinhDAO;
import module.buongphong.service.BuongPhongService;
import module.buongphong.view.BuongPhongPanel;
import module.phong.controller.PhongController;
import module.phong.dao.MemoryPhongDAO;
import module.phong.service.PhongService;
import module.phong.view.PhongPanel;

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

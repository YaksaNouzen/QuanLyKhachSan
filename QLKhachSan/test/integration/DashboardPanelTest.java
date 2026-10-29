package integration;

import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import Module_TaiKhoanPhanQuyen.util.PhienDangNhap;
import app.dashboard.DashboardService;
import app.view.DashboardPanel;
import app.view.MainFrame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/** Kiểm tra dashboard không cần database và có thể render khi chưa đăng nhập. */
public final class DashboardPanelTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        java.lang.reflect.Field menuField = MainFrame.class.getDeclaredField("MENU_GROUPS");
        menuField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<DashboardPanel.MenuGroup> groups =
                (List<DashboardPanel.MenuGroup>) menuField.get(null);
        check(groups.size() == 5, "Trang chủ phải có 5 nhóm chức năng");
        check(groups.stream().mapToInt(g -> g.chucNang().size()).sum() == 19,
                "Thiếu chức năng điều hướng trên trang chủ");
        AtomicReference<String> opened = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            DashboardPanel panel = new DashboardPanel(groups,
                    opened::set, () -> opened.set("LOGIN"));
            panel.setSize(1000, 760);
            layoutTree(panel);
            layoutTree(panel);
            JButton group = findButton(panel, "Lễ tân");
            check(group != null, "Thiếu nhóm Lễ tân trên trang chủ");
            group.doClick();
            layoutTree(panel);
            JButton target = findButton(panel, "Khách hàng");
            check(target != null, "Thiếu nút điều hướng trong trang chủ");
            check(target.getParent().isVisible(), "Chức năng chưa hiện sau khi mở nhóm");
            target.doClick();
            check("KHACHHANG".equals(opened.get()), "Nút không điều hướng đúng chức năng");
            JButton otherGroup = findButton(panel, "Phòng &amp; vận hành");
            check(otherGroup != null, "Thiếu nhóm Phòng trên trang chủ");
            otherGroup.doClick();
            check(!target.getParent().isVisible(), "Mở nhóm khác phải đóng nhóm cũ");
            JScrollPane scroll = (JScrollPane) panel.getComponent(0);
            JComponent body = (JComponent) scroll.getViewport().getView();
            body.invalidate();
            body.setSize(scroll.getViewport().getWidth(), body.getPreferredSize().height);
            layoutTree(body);
            JButton room = findButton(panel, "Quản lý phòng");
            check(room != null && room.getParent().isVisible(), "Nhóm Phòng chưa hiện danh sách");
            JComponent cards = (JComponent) room.getParent().getParent().getParent();
            check(cards.getHeight() >= cards.getPreferredSize().height,
                    "Danh sách nhóm đè lên phần tổng quan");
            panel.xoaDuLieuCaNhan();
            layoutTree(panel);
            check(countLabels(panel, "Chưa đăng nhập.") == 3,
                    "Thiếu trạng thái chưa đăng nhập trong ba thẻ cá nhân");
            body.setSize(scroll.getViewport().getWidth(), body.getPreferredSize().height);
            layoutTree(body);
            BufferedImage image = new BufferedImage(1000, 760, BufferedImage.TYPE_INT_RGB);
            panel.paint(image.getGraphics());
            if (args.length > 0) {
                try { ImageIO.write(image, "png", new File(args[0])); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            }
            TaiKhoan staff = new TaiKhoan();
            staff.setMaVaiTro("NV");
            PhienDangNhap.setTaiKhoan(staff);
            panel.capNhatQuyen();
            check(!findButton(panel, "Khách hàng").isVisible(),
                    "Nhân viên còn nhìn thấy mục Khách hàng");
            check(findButton(panel, "Quản lý phòng").isVisible(),
                    "Nhân viên không thấy mục Phòng");
            check(findButton(panel, "Lịch trực").isVisible(),
                    "Nhân viên không thấy Lịch trực & bàn giao");
            PhienDangNhap.dangXuat();
        });
        TaiKhoan nv = new TaiKhoan();
        nv.setMaVaiTro("NV");
        check(!DashboardService.duocXemDoanhThu(nv), "Nhân viên không được xem doanh thu");
        nv.setMaVaiTro("ADMIN");
        check(DashboardService.duocXemDoanhThu(nv), "Admin phải xem được doanh thu");
        nv.setMaVaiTro("KETOAN");
        check(DashboardService.duocXemDoanhThu(nv), "Kế toán phải xem được doanh thu");
        System.out.println("DashboardPanelTest PASS");
    }

    private static JButton findButton(JComponent parent, String text) {
        for (java.awt.Component component : parent.getComponents()) {
            if (component instanceof JButton button && button.getText().contains(text)) return button;
            if (component instanceof JComponent child) {
                JButton found = findButton(child, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static void layoutTree(java.awt.Container parent) {
        parent.doLayout();
        for (java.awt.Component component : parent.getComponents()) {
            if (component instanceof java.awt.Container child) layoutTree(child);
        }
    }

    private static int countLabels(java.awt.Container parent, String text) {
        int count = 0;
        for (java.awt.Component component : parent.getComponents()) {
            if (component instanceof javax.swing.JLabel label && text.equals(label.getText())) {
                count++;
            }
            if (component instanceof java.awt.Container child) count += countLabels(child, text);
        }
        return count;
    }

    private static void check(boolean valid, String message) {
        if (!valid) throw new AssertionError(message);
    }
}

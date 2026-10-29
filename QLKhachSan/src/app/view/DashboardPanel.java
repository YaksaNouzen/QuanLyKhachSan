package app.view;

import Module_TaiChinh.model.ThongKeTheoNgay;
import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import Module_TaiKhoanPhanQuyen.util.PhienDangNhap;
import app.dashboard.DashboardData;
import app.dashboard.DashboardData.CaHomNay;
import app.dashboard.DashboardData.CongViec;
import app.dashboard.DashboardService;
import app.security.QuyenTruyCap;
import common.ui.ColoredButton;
import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/** Trang chủ chung: điều hướng theo nhóm và dữ liệu cá nhân của phiên đăng nhập. */
public final class DashboardPanel extends JPanel {
    public record MenuItem(String ten, String maTrang) { }
    public record MenuGroup(String ten, String moTa, List<MenuItem> chucNang) { }

    private static final Color NAVY = new Color(23, 54, 93);
    private static final Color BG = HotelTheme.BACKGROUND;
    private final Consumer<String> moTrang;
    private final Runnable dangNhap;
    private final DashboardService service = new DashboardService();
    private JPanel nhomDangMo;
    private JButton nutNhomDangMo;
    private MenuGroup duLieuNhomDangMo;
    private final List<Runnable> capNhatNhom = new ArrayList<>();
    private JPanel cardsNhom;
    private final JPanel lich = new JPanel();
    private final JPanel viec = new JPanel();
    private final JPanel canhBao = new JPanel();
    private final DoanhThuChart bieuDo = new DoanhThuChart();
    private final JLabel loiChao = new JLabel("Trang chủ khách sạn");
    private final JLabel trangThai = new JLabel("Đăng nhập để xem lịch và công việc của bạn.");
    private final JLabel tongDoanhThu = new JLabel();
    private final JButton nutTaiLai = new ColoredButton("Đăng nhập / Tải dữ liệu",
            new Color(47, 98, 151), new Color(57, 119, 183), Color.WHITE);

    public DashboardPanel(List<MenuGroup> nhom, Consumer<String> moTrang, Runnable dangNhap) {
        this.moTrang = moTrang;
        this.dangNhap = dangNhap;
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(18, 28, 24, 28));
        body.add(taoDauTrang());
        body.add(Box.createVerticalStrut(14));
        body.add(tieuDe("Khám phá chức năng", "Chọn một nhóm để mở các chức năng liên quan"));
        body.add(Box.createVerticalStrut(10));
        body.add(taoNhomChucNang(nhom));
        body.add(Box.createVerticalStrut(14));
        body.add(tieuDe("Tổng quan hôm nay", "Lịch và công việc chỉ thuộc tài khoản đang đăng nhập"));
        body.add(Box.createVerticalStrut(10));
        JPanel overview = new JPanel(new GridLayout(1, 3, 12, 0));
        overview.setOpaque(false);
        overview.setPreferredSize(new Dimension(0, 180));
        overview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 195));
        overview.setAlignmentX(LEFT_ALIGNMENT);
        overview.add(taoCot("Lịch làm việc", lich, NAVY));
        overview.add(taoCot("Cần hoàn thành", viec, HotelTheme.BUTTON_WARNING));
        overview.add(taoCot("Cảnh báo", canhBao, HotelTheme.BUTTON_DANGER));
        body.add(overview);
        body.add(Box.createVerticalStrut(14));

        JPanel doanhThu = theTrang();
        doanhThu.setLayout(new BorderLayout(0, 8));
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        chartHeader.add(tieuDe("Doanh thu 7 ngày gần nhất", "Theo ngày lập hóa đơn"), BorderLayout.WEST);
        tongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tongDoanhThu.setForeground(NAVY);
        chartHeader.add(tongDoanhThu, BorderLayout.EAST);
        doanhThu.add(chartHeader, BorderLayout.NORTH);
        bieuDo.setPreferredSize(new Dimension(0, 190));
        doanhThu.add(bieuDo, BorderLayout.CENTER);
        body.add(doanhThu);

        JScrollPane scroll = new JScrollPane(body,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(22);
        add(scroll, BorderLayout.CENTER);

        xoaDuLieuCaNhan();
    }

    private JPanel taoDauTrang() {
        JPanel header = theTrang();
        header.setLayout(new BorderLayout(16, 0));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        loiChao.setFont(new Font("Segoe UI", Font.BOLD, 25));
        loiChao.setForeground(NAVY);
        trangThai.setFont(HotelTheme.FONT_REGULAR);
        trangThai.setForeground(HotelTheme.TEXT_SECONDARY);
        text.add(loiChao);
        text.add(Box.createVerticalStrut(6));
        text.add(trangThai);
        header.add(text, BorderLayout.CENTER);
        nutTaiLai.addActionListener(e -> {
            if (PhienDangNhap.getTaiKhoan() == null) dangNhap.run();
            else taiLai();
        });
        nutTaiLai.setPreferredSize(new Dimension(185, 42));
        header.add(nutTaiLai, BorderLayout.EAST);
        return header;
    }

    private JPanel taoNhomChucNang(List<MenuGroup> nhom) {
        JPanel cards = new JPanel();
        cardsNhom = cards;
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setOpaque(false);
        cards.setAlignmentX(LEFT_ALIGNMENT);
        for (MenuGroup group : nhom) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setAlignmentX(LEFT_ALIGNMENT);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(HotelTheme.BORDER),
                    BorderFactory.createEmptyBorder(9, 9, 9, 9)));
            JButton header = new ColoredButton(nhanNhom(group, false),
                    new Color(40, 81, 130), new Color(55, 107, 165), Color.WHITE);
            header.setHorizontalAlignment(SwingConstants.LEFT);
            header.setFont(HotelTheme.FONT_REGULAR);
            header.setPreferredSize(new Dimension(0, 62));
            header.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 10));
            JPanel list = new JPanel(new GridLayout(0, 2, 8, 8));
            List<JButton> buttons = new ArrayList<>();
            list.setOpaque(false);
            list.setBorder(BorderFactory.createEmptyBorder(9, 0, 0, 0));
            list.setVisible(false);
            for (MenuItem item : group.chucNang()) {
                JButton button = new ColoredButton(item.ten() + "  →",
                        new Color(227, 239, 252), new Color(207, 226, 247), NAVY);
                button.setFont(HotelTheme.FONT_BOLD);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 6));
                button.setPreferredSize(new Dimension(0, 43));
                button.addActionListener(e -> moTrang.accept(item.maTrang()));
                list.add(button);
                buttons.add(button);
            }
            header.addActionListener(e -> {
                boolean open = !list.isVisible();
                if (nhomDangMo != null) {
                    nhomDangMo.setVisible(false);
                    nutNhomDangMo.setText(nhanNhom(duLieuNhomDangMo, false,
                            soChucNangDuocXem(duLieuNhomDangMo)));
                    ((JPanel) nhomDangMo.getParent()).setMaximumSize(
                            new Dimension(Integer.MAX_VALUE,
                                    nhomDangMo.getParent().getPreferredSize().height));
                }
                list.setVisible(open);
                header.setText(nhanNhom(group, open, soChucNangDuocXem(group)));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                        card.getPreferredSize().height));
                nhomDangMo = open ? list : null;
                nutNhomDangMo = open ? header : null;
                duLieuNhomDangMo = open ? group : null;
                cards.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                        cards.getPreferredSize().height));
                cards.revalidate();
                cards.repaint();
            });
            card.add(header, BorderLayout.NORTH);
            card.add(list, BorderLayout.CENTER);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    card.getPreferredSize().height));
            cards.add(card);
            cards.add(Box.createVerticalStrut(8));
            capNhatNhom.add(() -> {
                TaiKhoan account = PhienDangNhap.getTaiKhoan();
                int visible = 0;
                for (int i = 0; i < buttons.size(); i++) {
                    boolean allowed = account == null || QuyenTruyCap.duocMo(
                            account.getMaVaiTro(), group.chucNang().get(i).maTrang());
                    buttons.get(i).setVisible(allowed);
                    if (allowed) visible++;
                }
                list.setVisible(false);
                header.setText(nhanNhom(group, false, visible));
                card.setVisible(visible > 0);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                        card.getPreferredSize().height));
            });
        }
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                cards.getPreferredSize().height));
        return cards;
    }

    public void capNhatQuyen() {
        nhomDangMo = null;
        nutNhomDangMo = null;
        duLieuNhomDangMo = null;
        for (Runnable update : capNhatNhom) update.run();
        cardsNhom.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                cardsNhom.getPreferredSize().height));
        cardsNhom.revalidate();
        cardsNhom.repaint();
    }

    private int soChucNangDuocXem(MenuGroup group) {
        TaiKhoan account = PhienDangNhap.getTaiKhoan();
        if (account == null) return group.chucNang().size();
        return (int) group.chucNang().stream().filter(item ->
                QuyenTruyCap.duocMo(account.getMaVaiTro(), item.maTrang())).count();
    }

    private String nhanNhom(MenuGroup group, boolean open) {
        return nhanNhom(group, open, group.chucNang().size());
    }

    private String nhanNhom(MenuGroup group, boolean open, int count) {
        return "<html><b>" + esc(group.ten()) + "  " + (open ? "▴" : "▾")
                + "</b><br><font color='#E8F2FF'>" + esc(group.moTa())
                + " · " + count + " chức năng</font></html>";
    }

    private JPanel taoCot(String ten, JPanel items, Color accent) {
        JPanel card = theTrang();
        card.setLayout(new BorderLayout(0, 10));
        JLabel title = new JLabel(ten);
        title.setForeground(accent);
        title.setFont(HotelTheme.FONT_TITLE);
        card.add(title, BorderLayout.NORTH);
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setOpaque(false);
        card.add(items, BorderLayout.CENTER);
        return card;
    }

    private JPanel theTrang() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HotelTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    private JPanel tieuDe(String title, String note) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(HotelTheme.FONT_HEADER);
        heading.setForeground(NAVY);
        JLabel desc = new JLabel(note);
        desc.setFont(HotelTheme.FONT_SMALL);
        desc.setForeground(HotelTheme.TEXT_SECONDARY);
        panel.add(heading);
        panel.add(desc);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    public void xoaDuLieuCaNhan() {
        loiChao.setText("Trang chủ khách sạn");
        trangThai.setText("Đăng nhập để xem lịch và công việc của bạn.");
        nutTaiLai.setText("Đăng nhập / Tải dữ liệu");
        ghiChu(lich, "Chưa đăng nhập.");
        ghiChu(viec, "Chưa đăng nhập.");
        ghiChu(canhBao, "Chưa đăng nhập.");
        tongDoanhThu.setText("");
        bieuDo.hienThi(List.of(), false);
    }

    public void taiLai() {
        TaiKhoan taiKhoan = PhienDangNhap.getTaiKhoan();
        if (taiKhoan == null) {
            xoaDuLieuCaNhan();
            return;
        }
        nutTaiLai.setEnabled(false);
        nutTaiLai.setText("Đang tải...");
        trangThai.setText("Đang cập nhật dữ liệu hôm nay.");
        LocalDate homNay = LocalDate.now();
        new SwingWorker<DashboardData, Void>() {
            @Override protected DashboardData doInBackground() throws Exception {
                return service.taiDuLieu(taiKhoan, homNay);
            }
            @Override protected void done() {
                nutTaiLai.setEnabled(true);
                nutTaiLai.setText("Làm mới");
                if (PhienDangNhap.getTaiKhoan() != taiKhoan) {
                    xoaDuLieuCaNhan();
                    return;
                }
                try {
                    hienDuLieu(get(), taiKhoan, homNay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    trangThai.setText("Đã dừng tải dữ liệu.");
                } catch (ExecutionException ex) {
                    String message = ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage();
                    trangThai.setText("Không tải được dashboard: " + message);
                    ghiChu(lich, "Hãy kiểm tra kết nối và thử lại.");
                    ghiChu(viec, "Chưa có dữ liệu.");
                    ghiChu(canhBao, "Chưa có dữ liệu.");
                    tongDoanhThu.setText("");
                    bieuDo.hienThi(List.of(), false);
                }
            }
        }.execute();
    }

    private void hienDuLieu(DashboardData data, TaiKhoan taiKhoan, LocalDate homNay) {
        loiChao.setText("Xin chào, " + (data.tenNhanVien().isBlank()
                ? taiKhoan.getTenDangNhap() : data.tenNhanVien()));
        trangThai.setText("Hôm nay " + homNay.format(DateTimeFormatter.ofPattern(
                "EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN")))
                + " · " + taiKhoan.getMaVaiTro());
        lich.removeAll();
        if (data.caHomNay().isEmpty()) ghiChu(lich, "Hôm nay chưa được phân ca.");
        for (CaHomNay ca : data.caHomNay()) {
            JLabel row = new JLabel("<html><b>" + esc(ca.tenCa()) + "</b><br>"
                    + esc(ca.gioLam()) + " · " + esc(ca.trangThai()) + "</html>");
            row.setBorder(BorderFactory.createEmptyBorder(6, 0, 8, 0));
            lich.add(row);
        }
        hienCongViec(viec, data.canLam(), "Không có việc đang mở.");
        hienCongViec(canhBao, data.canChuY(), "Không có cảnh báo cá nhân.");
        boolean xemDoanhThu = DashboardService.duocXemDoanhThu(taiKhoan);
        bieuDo.hienThi(data.doanhThuBayNgay(), xemDoanhThu);
        if (xemDoanhThu) {
            double tong = data.doanhThuBayNgay().stream()
                    .mapToDouble(ThongKeTheoNgay::getDoanhThu).sum();
            tongDoanhThu.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0f ₫", tong));
        } else {
            tongDoanhThu.setText("Chỉ Admin / Kế toán");
        }
        revalidate();
        repaint();
    }

    private void hienCongViec(JPanel target, List<CongViec> items, String empty) {
        target.removeAll();
        if (items.isEmpty()) {
            ghiChu(target, empty);
            return;
        }
        int shown = Math.min(3, items.size());
        for (int i = 0; i < shown; i++) {
            CongViec item = items.get(i);
            JButton button = new JButton("<html><b>" + esc(item.tieuDe())
                    + "</b><br><font color='#64748B'>" + esc(item.moTa()) + "</font></html>");
            button.setToolTipText(item.moTa());
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setFont(HotelTheme.FONT_SMALL);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            if (item.maTrang() != null) {
                button.addActionListener(e -> moTrang.accept(item.maTrang()));
            } else {
                button.setEnabled(false);
            }
            button.setAlignmentX(LEFT_ALIGNMENT);
            target.add(button);
        }
        if (items.size() > shown) {
            JLabel more = new JLabel("+ " + (items.size() - shown) + " việc khác");
            more.setFont(HotelTheme.FONT_SMALL);
            more.setForeground(HotelTheme.TEXT_SECONDARY);
            target.add(more);
        }
        target.revalidate();
        target.repaint();
    }

    private void ghiChu(JPanel target, String message) {
        target.removeAll();
        JLabel label = new JLabel(message);
        label.setFont(HotelTheme.FONT_SMALL);
        label.setForeground(HotelTheme.TEXT_SECONDARY);
        target.add(label);
        target.revalidate();
        target.repaint();
    }

    private static String esc(String text) {
        return text == null ? "" : text.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Biểu đồ cột Swing thuần, dùng số liệu theo ngày đã có của module tài chính. */
    private static final class DoanhThuChart extends JPanel {
        private List<ThongKeTheoNgay> data = List.of();
        private boolean duocXem;

        DoanhThuChart() { setBackground(Color.WHITE); }

        void hienThi(List<ThongKeTheoNgay> rows, boolean allowed) {
            data = new ArrayList<>(rows);
            duocXem = allowed;
            repaint();
        }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (!duocXem) {
                    g.setColor(HotelTheme.TEXT_SECONDARY);
                    g.drawString("Đăng nhập bằng tài khoản Admin hoặc Kế toán để xem biểu đồ.", 10, 45);
                    return;
                }
                LocalDate start = LocalDate.now().minusDays(6);
                double[] values = new double[7];
                for (ThongKeTheoNgay row : data) {
                    try {
                        int index = (int) java.time.temporal.ChronoUnit.DAYS.between(
                                start, LocalDate.parse(row.getNgayLap()));
                        if (index >= 0 && index < 7) values[index] = row.getDoanhThu();
                    } catch (RuntimeException ignored) { /* Dòng ngày không hợp lệ không vẽ. */ }
                }
                double max = 0;
                for (double value : values) max = Math.max(max, value);
                int left = 42, top = 20, bottom = getHeight() - 28;
                int height = Math.max(20, bottom - top);
                int width = Math.max(1, getWidth() - left - 12);
                g.setColor(HotelTheme.BORDER);
                g.drawLine(left, bottom, left + width, bottom);
                for (int i = 0; i < 7; i++) {
                    int center = left + (i * 2 + 1) * width / 14;
                    int barWidth = Math.max(12, width / 15);
                    int barHeight = max <= 0 ? 0 : (int) (values[i] / max * (height - 18));
                    g.setColor(HotelTheme.PRIMARY_BLUE);
                    g.fillRoundRect(center - barWidth / 2, bottom - barHeight,
                            barWidth, barHeight, 7, 7);
                    g.setColor(HotelTheme.TEXT_SECONDARY);
                    String day = start.plusDays(i).format(DateTimeFormatter.ofPattern("dd/MM"));
                    g.drawString(day, center - 15, bottom + 17);
                }
                if (max == 0) {
                    g.setColor(HotelTheme.TEXT_SECONDARY);
                    g.drawString("Chưa có hóa đơn trong 7 ngày gần nhất.", left + 8, top + 32);
                }
            } finally {
                g.dispose();
            }
        }
    }
}
